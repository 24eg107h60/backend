package com.autocare.platform.controller;

import com.autocare.platform.dto.BookingRequest;
import com.autocare.platform.dto.BookingStatusUpdateRequest;
import com.autocare.platform.model.ServiceBooking;
import com.autocare.platform.model.User;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.ServiceBookingService;
import com.autocare.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Service Bookings", description = "Service Appointment Booking and Status Workflow APIs")
public class ServiceBookingController {

    private final ServiceBookingService bookingService;
    private final UserService userService;

    public ServiceBookingController(ServiceBookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get service bookings for current user (or all bookings if admin)")
    public ResponseEntity<List<ServiceBooking>> getBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<ServiceBooking> bookings = (user.getRole() == Role.ROLE_ADMIN)
                ? bookingService.getAllBookings()
                : bookingService.getUserBookings(user.getId());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ServiceBooking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    @Operation(summary = "Book a new service appointment")
    public ResponseEntity<ServiceBooking> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                        @Valid @RequestBody BookingRequest request) {
        User user = userService.getByEmail(userDetails.getUsername());
        ServiceBooking booking = bookingService.createBooking(request, user);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a service booking")
    public ResponseEntity<ServiceBooking> cancelBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable Long id) {
        User user = userService.getByEmail(userDetails.getUsername());
        ServiceBooking cancelled = bookingService.cancelBooking(id, user);
        return ResponseEntity.ok(cancelled);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update booking status, technician notes, or final cost (Admin only)")
    public ResponseEntity<ServiceBooking> updateStatus(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody BookingStatusUpdateRequest request) {
        User admin = userService.getByEmail(userDetails.getUsername());
        ServiceBooking updated = bookingService.updateStatus(
                id,
                request.getStatus(),
                request.getTechnicianNotes(),
                request.getFinalCost(),
                admin
        );
        return ResponseEntity.ok(updated);
    }
}
