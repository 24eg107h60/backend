package com.autocare.platform.repository;

import com.autocare.platform.model.ServiceBooking;
import com.autocare.platform.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByUserIdOrderByBookingDateDesc(Long userId);
    List<ServiceBooking> findByVehicleIdOrderByBookingDateDesc(Long vehicleId);
    List<ServiceBooking> findAllByOrderByBookingDateDesc();
    List<ServiceBooking> findByStatus(BookingStatus status);
    long countByStatus(BookingStatus status);
    List<ServiceBooking> findByBookingDateAndReminderSentFalse(LocalDate date);
}
