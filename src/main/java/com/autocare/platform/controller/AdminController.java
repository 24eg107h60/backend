package com.autocare.platform.controller;

import com.autocare.platform.dto.AdminDashboardDto;
import com.autocare.platform.dto.VehiclePredictionReport;
import com.autocare.platform.model.User;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.AdminService;
import com.autocare.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Dashboard", description = "Fleet Management, User Roles, and Operations Analytics APIs")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin operations dashboard metrics")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users in the platform")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user authorization role (USER or ADMIN)")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @GetMapping("/fleet-health")
    @Operation(summary = "Get health diagnostic reports for all fleet vehicles")
    public ResponseEntity<List<VehiclePredictionReport>> getFleetHealth() {
        return ResponseEntity.ok(adminService.getFleetHealthReports());
    }
}
