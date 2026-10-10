package com.autocare.platform.service;

import com.autocare.platform.dto.AdminDashboardDto;
import com.autocare.platform.dto.VehiclePredictionReport;
import com.autocare.platform.model.ServiceBooking;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.BookingStatus;
import com.autocare.platform.model.enums.UrgencyLevel;
import com.autocare.platform.repository.ServiceBookingRepository;
import com.autocare.platform.repository.ServiceRecordRepository;
import com.autocare.platform.repository.UserRepository;
import com.autocare.platform.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceBookingRepository bookingRepository;
    private final ServiceRecordRepository recordRepository;
    private final PredictiveMaintenanceEngine predictiveEngine;

    public AdminService(UserRepository userRepository,
                        VehicleRepository vehicleRepository,
                        ServiceBookingRepository bookingRepository,
                        ServiceRecordRepository recordRepository,
                        PredictiveMaintenanceEngine predictiveEngine) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.recordRepository = recordRepository;
        this.predictiveEngine = predictiveEngine;
    }

    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalVehicles(vehicleRepository.count());
        dto.setTotalBookings(bookingRepository.count());
        dto.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        dto.setInProgressBookings(bookingRepository.countByStatus(BookingStatus.IN_PROGRESS));
        dto.setCompletedBookings(bookingRepository.countByStatus(BookingStatus.COMPLETED));

        Double revenue = recordRepository.sumTotalMaintenanceCost();
        dto.setTotalRevenue(revenue != null ? revenue : 0.0);

        List<ServiceBooking> allBookings = bookingRepository.findAllByOrderByBookingDateDesc();
        dto.setRecentBookings(allBookings.stream().limit(8).toList());

        // Scan all fleet vehicles for overdue maintenance items
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<VehiclePredictionReport> criticalVehicles = new ArrayList<>();
        long overdueCount = 0;

        for (Vehicle v : allVehicles) {
            VehiclePredictionReport rep = predictiveEngine.generatePrediction(v);
            if (rep.getOverallUrgency() == UrgencyLevel.OVERDUE || rep.getOverallHealthScore() < 60.0) {
                criticalVehicles.add(rep);
                overdueCount++;
            }
        }

        dto.setOverdueVehiclesCount(overdueCount);
        dto.setCriticalVehicles(criticalVehicles);

        return dto;
    }

    public List<VehiclePredictionReport> getFleetHealthReports() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<VehiclePredictionReport> reports = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            reports.add(predictiveEngine.generatePrediction(v));
        }
        return reports;
    }
}
