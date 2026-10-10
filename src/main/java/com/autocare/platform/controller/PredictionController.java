package com.autocare.platform.controller;

import com.autocare.platform.dto.VehiclePredictionReport;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.service.PredictiveMaintenanceEngine;
import com.autocare.platform.service.UserService;
import com.autocare.platform.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@Tag(name = "Maintenance Prediction", description = "Rule-based Maintenance and Component Diagnostics APIs")
public class PredictionController {

    private final PredictiveMaintenanceEngine predictiveEngine;
    private final VehicleService vehicleService;
    private final UserService userService;

    public PredictionController(PredictiveMaintenanceEngine predictiveEngine,
                                VehicleService vehicleService,
                                UserService userService) {
        this.predictiveEngine = predictiveEngine;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get detailed predictive maintenance report for a vehicle")
    public ResponseEntity<VehiclePredictionReport> getPrediction(@PathVariable Long vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        VehiclePredictionReport report = predictiveEngine.generatePrediction(vehicle);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/all-my-vehicles")
    @Operation(summary = "Get predictions for all vehicles owned by logged-in user")
    public ResponseEntity<List<VehiclePredictionReport>> getAllMyPredictions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<Vehicle> vehicles = (user.getRole() == Role.ROLE_ADMIN)
                ? vehicleService.getAllVehicles()
                : vehicleService.getUserVehicles(user.getId());

        List<VehiclePredictionReport> reports = new ArrayList<>();
        for (Vehicle v : vehicles) {
            reports.add(predictiveEngine.generatePrediction(v));
        }
        return ResponseEntity.ok(reports);
    }
}
