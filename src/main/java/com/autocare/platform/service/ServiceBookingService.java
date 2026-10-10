package com.autocare.platform.service;

import com.autocare.platform.dto.BookingRequest;
import com.autocare.platform.model.ServiceBooking;
import com.autocare.platform.model.ServiceRecord;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.BookingStatus;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.model.enums.ServiceType;
import com.autocare.platform.repository.ServiceBookingRepository;
import com.autocare.platform.repository.ServiceRecordRepository;
import com.autocare.platform.repository.VehicleRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRecordRepository recordRepository;
    private final NotificationService notificationService;
    private final PredictiveMaintenanceEngine predictiveEngine;

    public ServiceBookingService(ServiceBookingRepository bookingRepository,
                                 VehicleRepository vehicleRepository,
                                 ServiceRecordRepository recordRepository,
                                 NotificationService notificationService,
                                 @Lazy PredictiveMaintenanceEngine predictiveEngine) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.recordRepository = recordRepository;
        this.notificationService = notificationService;
        this.predictiveEngine = predictiveEngine;
    }

    @Transactional
    public ServiceBooking createBooking(BookingRequest request, User currentUser) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + request.getVehicleId()));

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !vehicle.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Unauthorized: You do not own this vehicle");
        }

        double calculatedEstimatedCost = 0.0;
        if (request.getEstimatedCost() != null && request.getEstimatedCost() > 0) {
            calculatedEstimatedCost = request.getEstimatedCost();
        } else {
            // Calculate from selected service types
            for (String rawType : request.getServiceTypes().split(",")) {
                String clean = rawType.trim();
                for (ServiceType st : ServiceType.values()) {
                    if (st.name().equalsIgnoreCase(clean) || st.getDisplayName().equalsIgnoreCase(clean)) {
                        calculatedEstimatedCost += st.getStandardCost();
                    }
                }
            }
            if (calculatedEstimatedCost == 0.0) calculatedEstimatedCost = 100.0;
        }

        ServiceBooking booking = new ServiceBooking(
                vehicle.getUser(),
                vehicle,
                request.getServiceTypes(),
                request.getBookingDate(),
                request.getBookingTime(),
                request.getServiceCenter(),
                calculatedEstimatedCost,
                request.getNotes()
        );

        ServiceBooking saved = bookingRepository.save(booking);

        // Notify user about booking creation
        notificationService.createNotification(
                vehicle.getUser(),
                vehicle,
                "Service Appointment Booked",
                "Your appointment for " + vehicle.getMake() + " " + vehicle.getModel() +
                        " on " + saved.getBookingDate() + " at " + saved.getBookingTime() +
                        " (" + saved.getServiceCenter() + ") has been submitted.",
                "BOOKING_UPDATE"
        );

        return saved;
    }

    @Transactional
    public ServiceBooking cancelBooking(Long id, User currentUser) {
        ServiceBooking booking = getBookingById(id);

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Unauthorized: You cannot cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        ServiceBooking saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getUser(),
                booking.getVehicle(),
                "Booking Cancelled",
                "Your service booking #" + booking.getId() + " scheduled for " + booking.getBookingDate() + " has been cancelled.",
                "BOOKING_UPDATE"
        );

        return saved;
    }

    @Transactional
    public ServiceBooking updateStatus(Long id, BookingStatus newStatus, String techNotes, Double finalCost, User adminUser) {
        ServiceBooking booking = getBookingById(id);
        booking.setStatus(newStatus);

        if (techNotes != null && !techNotes.isBlank()) {
            booking.setTechnicianNotes(techNotes);
        }

        if (finalCost != null && finalCost > 0) {
            booking.setFinalCost(finalCost);
        } else if (booking.getFinalCost() == null || booking.getFinalCost() == 0.0) {
            booking.setFinalCost(booking.getEstimatedCost());
        }

        // When a booking transitions to COMPLETED, create corresponding ServiceRecord(s)
        if (newStatus == BookingStatus.COMPLETED) {
            Vehicle vehicle = booking.getVehicle();
            LocalDate serviceDate = booking.getBookingDate() != null ? booking.getBookingDate() : LocalDate.now();
            int currentOdometer = vehicle.getCurrentMileage();

            // Update vehicle last service stats
            vehicle.setLastServiceDate(serviceDate);
            vehicle.setLastServiceMileage(currentOdometer);
            vehicleRepository.save(vehicle);

            // Log ServiceRecord for each service item
            String[] types = booking.getServiceTypes().split(",");
            double costPerItem = booking.getFinalCost() / Math.max(1, types.length);

            for (String rawType : types) {
                String clean = rawType.trim();
                ServiceRecord record = new ServiceRecord(
                        vehicle,
                        booking.getId(),
                        clean,
                        "Completed via service booking #" + booking.getId() + ". " + (techNotes != null ? techNotes : ""),
                        serviceDate,
                        currentOdometer,
                        Math.round(costPerItem * 100.0) / 100.0,
                        "INV-" + System.currentTimeMillis() % 1000000,
                        booking.getServiceCenter(),
                        techNotes
                );
                recordRepository.save(record);
            }

            // Recalculate reminders
            predictiveEngine.checkAndTriggerReminders(vehicle);
        }

        ServiceBooking saved = bookingRepository.save(booking);

        // Notify customer
        notificationService.createNotification(
                booking.getUser(),
                booking.getVehicle(),
                "Booking Update: " + newStatus,
                "Your appointment status for " + booking.getVehicle().getMake() + " " + booking.getVehicle().getModel() +
                        " is now: " + newStatus + ".",
                "BOOKING_UPDATE"
        );

        return saved;
    }

    public ServiceBooking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
    }

    public List<ServiceBooking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId);
    }

    public List<ServiceBooking> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDesc();
    }
}
