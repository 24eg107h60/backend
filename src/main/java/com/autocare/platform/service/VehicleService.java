package com.autocare.platform.service;

import com.autocare.platform.dto.VehicleRequest;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.repository.VehicleRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final PredictiveMaintenanceEngine predictiveEngine;

    public VehicleService(VehicleRepository vehicleRepository,
                          @Lazy PredictiveMaintenanceEngine predictiveEngine) {
        this.vehicleRepository = vehicleRepository;
        this.predictiveEngine = predictiveEngine;
    }

    @Transactional
    public Vehicle addVehicle(VehicleRequest request, User user) {
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate().trim().toUpperCase())) {
            throw new IllegalArgumentException("License plate is already registered: " + request.getLicensePlate());
        }

        Vehicle vehicle = new Vehicle(
                user,
                request.getMake().trim(),
                request.getModel().trim(),
                request.getYear(),
                request.getLicensePlate().trim().toUpperCase(),
                request.getVin(),
                request.getFuelType(),
                request.getCurrentMileage(),
                request.getAvgMonthlyMileage(),
                request.getLastServiceDate(),
                request.getLastServiceMileage()
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        // Initial reminder check for any pre-existing overdue state
        predictiveEngine.checkAndTriggerReminders(saved);
        return saved;
    }

    @Transactional
    public Vehicle updateVehicle(Long id, VehicleRequest request, User currentUser) {
        Vehicle vehicle = getVehicleById(id);
        verifyOwnershipOrAdmin(vehicle, currentUser);

        String newPlate = request.getLicensePlate().trim().toUpperCase();
        if (!vehicle.getLicensePlate().equalsIgnoreCase(newPlate) && vehicleRepository.existsByLicensePlate(newPlate)) {
            throw new IllegalArgumentException("License plate is already in use by another vehicle: " + newPlate);
        }

        vehicle.setMake(request.getMake().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setYear(request.getYear());
        vehicle.setLicensePlate(newPlate);
        vehicle.setVin(request.getVin());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setCurrentMileage(request.getCurrentMileage());
        vehicle.setAvgMonthlyMileage(request.getAvgMonthlyMileage());
        vehicle.setLastServiceDate(request.getLastServiceDate());
        vehicle.setLastServiceMileage(request.getLastServiceMileage());

        Vehicle updated = vehicleRepository.save(vehicle);
        predictiveEngine.checkAndTriggerReminders(updated);
        return updated;
    }

    @Transactional
    public Vehicle updateMileage(Long id, Integer newMileage, User currentUser) {
        Vehicle vehicle = getVehicleById(id);
        verifyOwnershipOrAdmin(vehicle, currentUser);

        if (newMileage < vehicle.getCurrentMileage()) {
            throw new IllegalArgumentException("New mileage (" + newMileage + " km) cannot be lower than current mileage (" + vehicle.getCurrentMileage() + " km)");
        }

        vehicle.setCurrentMileage(newMileage);
        Vehicle saved = vehicleRepository.save(vehicle);

        // Mileage update may push maintenance items to DUE_SOON or OVERDUE
        predictiveEngine.checkAndTriggerReminders(saved);
        return saved;
    }

    @Transactional
    public void deleteVehicle(Long id, User currentUser) {
        Vehicle vehicle = getVehicleById(id);
        verifyOwnershipOrAdmin(vehicle, currentUser);
        vehicleRepository.delete(vehicle);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
    }

    public List<Vehicle> getUserVehicles(Long userId) {
        return vehicleRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    private void verifyOwnershipOrAdmin(Vehicle vehicle, User user) {
        if (!user.getRole().equals(Role.ROLE_ADMIN) && !vehicle.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized: You do not own this vehicle");
        }
    }
}
