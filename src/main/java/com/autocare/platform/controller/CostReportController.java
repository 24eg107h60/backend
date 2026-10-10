package com.autocare.platform.controller;

import com.autocare.platform.dto.CostReportDto;
import com.autocare.platform.model.User;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.CostReportService;
import com.autocare.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Cost Reports", description = "Service History and Maintenance Expense Analytics APIs")
public class CostReportController {

    private final CostReportService reportService;
    private final UserService userService;

    public CostReportController(CostReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/cost-summary")
    @Operation(summary = "Get aggregated cost reports and category breakdown")
    public ResponseEntity<CostReportDto> getCostSummary(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        CostReportDto report = (user.getRole() == Role.ROLE_ADMIN)
                ? reportService.generateFleetReport()
                : reportService.generateUserReport(user.getId());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get cost report for a specific vehicle")
    public ResponseEntity<CostReportDto> getVehicleReport(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reportService.generateVehicleReport(vehicleId));
    }

    @GetMapping("/fleet")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get fleet-wide expenditure reports (Admin only)")
    public ResponseEntity<CostReportDto> getFleetReport() {
        return ResponseEntity.ok(reportService.generateFleetReport());
    }
}
