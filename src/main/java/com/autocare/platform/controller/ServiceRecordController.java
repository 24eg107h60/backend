package com.autocare.platform.controller;

import com.autocare.platform.dto.ServiceRecordRequest;
import com.autocare.platform.model.ServiceRecord;
import com.autocare.platform.model.User;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.ServiceRecordService;
import com.autocare.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@Tag(name = "Service Records", description = "Service History and Maintenance Logs APIs")
public class ServiceRecordController {

    private final ServiceRecordService recordService;
    private final UserService userService;

    public ServiceRecordController(ServiceRecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get service records for current user (or all records if admin)")
    public ResponseEntity<List<ServiceRecord>> getRecords(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<ServiceRecord> records = (user.getRole() == Role.ROLE_ADMIN)
                ? recordService.getAllRecords()
                : recordService.getRecordsByUser(user.getId());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get service history records for a specific vehicle")
    public ResponseEntity<List<ServiceRecord>> getRecordsByVehicle(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable Long vehicleId) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<ServiceRecord> records = recordService.getRecordsByVehicle(vehicleId, user);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    @Operation(summary = "Log a new service record manually")
    public ResponseEntity<ServiceRecord> createRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Valid @RequestBody ServiceRecordRequest request) {
        User user = userService.getByEmail(userDetails.getUsername());
        ServiceRecord record = recordService.createRecord(request, user);
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service record")
    public ResponseEntity<Void> deleteRecord(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        User user = userService.getByEmail(userDetails.getUsername());
        recordService.deleteRecord(id, user);
        return ResponseEntity.noContent().build();
    }
}
