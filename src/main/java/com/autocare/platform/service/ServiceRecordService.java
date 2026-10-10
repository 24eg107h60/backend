package com.autocare.platform.service;

import com.autocare.platform.dto.ServiceRecordRequest;
import com.autocare.platform.model.ServiceRecord;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.repository.ServiceRecordRepository;
import com.autocare.platform.repository.VehicleRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceRecordService {

    private final ServiceRecordRepository recordRepository;
    private final VehicleRepository vehicleRepository;
    private final PredictiveMaintenanceEngine predictiveEngine;

    public ServiceRecordService(ServiceRecordRepository recordRepository,
                                VehicleRepository vehicleRepository,
                                @Lazy PredictiveMaintenanceEngine predictiveEngine) {
        this.recordRepository = recordRepository;
        this.vehicleRepository = vehicleRepository;
        this.predictiveEngine = predictiveEngine;
    }

    @Transactional
    public ServiceRecord createRecord(ServiceRecordRequest request, User currentUser) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + request.getVehicleId()));

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !vehicle.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Unauthorized: You do not own this vehicle");
        }

        String invoice = request.getInvoiceNumber();
        if (invoice == null || invoice.isBlank()) {
            invoice = "INV-" + (System.currentTimeMillis() % 1000000);
        }

        ServiceRecord record = new ServiceRecord(
                vehicle,
                request.getBookingId(),
                request.getServiceType(),
                request.getDescription(),
                request.getServiceDate(),
                request.getMileageAtService(),
                request.getCost(),
                invoice,
                request.getServiceCenter(),
                request.getTechnicianNotes()
        );

        ServiceRecord saved = recordRepository.save(record);

        // Update vehicle odometer if service mileage is higher
        if (request.getMileageAtService() > vehicle.getCurrentMileage()) {
            vehicle.setCurrentMileage(request.getMileageAtService());
        }

        // Update last service stats
        if (vehicle.getLastServiceDate() == null || request.getServiceDate().isAfter(vehicle.getLastServiceDate())) {
            vehicle.setLastServiceDate(request.getServiceDate());
            vehicle.setLastServiceMileage(request.getMileageAtService());
        }

        vehicleRepository.save(vehicle);
        predictiveEngine.checkAndTriggerReminders(vehicle);

        return saved;
    }

    public List<ServiceRecord> getRecordsByVehicle(Long vehicleId, User currentUser) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !vehicle.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Unauthorized: You do not own this vehicle");
        }

        return recordRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId);
    }

    public List<ServiceRecord> getRecordsByUser(Long userId) {
        return recordRepository.findByVehicleUserIdOrderByServiceDateDesc(userId);
    }

    public List<ServiceRecord> getAllRecords() {
        return recordRepository.findAllByOrderByServiceDateDesc();
    }

    @Transactional
    public void deleteRecord(Long id, User currentUser) {
        ServiceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + id));

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !record.getVehicle().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Unauthorized: You cannot delete this record");
        }

        recordRepository.delete(record);
    }
}
