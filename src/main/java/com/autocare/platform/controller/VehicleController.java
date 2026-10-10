package com.autocare.platform.controller;

import com.autocare.platform.dto.MileageUpdateRequest;
import com.autocare.platform.dto.VehicleRequest;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.UserService;
import com.autocare.platform.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle Management and Telemetry APIs")
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserService userService;

    public VehicleController(VehicleService vehicleService, UserService userService) {
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get vehicles (user vehicles, or all vehicles if admin)")
    public ResponseEntity<List<Vehicle>> getVehicles(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<Vehicle> vehicles = (user.getRole() == Role.ROLE_ADMIN)
                ? vehicleService.getAllVehicles()
                : vehicleService.getUserVehicles(user.getId());
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle details by ID")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new vehicle")
    public ResponseEntity<Vehicle> addVehicle(@AuthenticationPrincipal UserDetails userDetails,
                                              @Valid @RequestBody VehicleRequest request) {
        User user = userService.getByEmail(userDetails.getUsername());
        Vehicle vehicle = vehicleService.addVehicle(request, user);
        return ResponseEntity.ok(vehicle);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle details")
    public ResponseEntity<Vehicle> updateVehicle(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody VehicleRequest request) {
        User user = userService.getByEmail(userDetails.getUsername());
        Vehicle updated = vehicleService.updateVehicle(id, request, user);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/mileage")
    @Operation(summary = "Update vehicle odometer/current mileage")
    public ResponseEntity<Vehicle> updateMileage(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody MileageUpdateRequest request) {
        User user = userService.getByEmail(userDetails.getUsername());
        Vehicle updated = vehicleService.updateMileage(id, request.getMileage(), user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle by ID")
    public ResponseEntity<Void> deleteVehicle(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        User user = userService.getByEmail(userDetails.getUsername());
        vehicleService.deleteVehicle(id, user);
        return ResponseEntity.noContent().build();
    }
}
