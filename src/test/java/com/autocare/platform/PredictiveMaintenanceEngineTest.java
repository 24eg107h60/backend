package com.autocare.platform;

import com.autocare.platform.dto.MaintenancePredictionItem;
import com.autocare.platform.dto.VehiclePredictionReport;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.FuelType;
import com.autocare.platform.model.enums.Role;
import com.autocare.platform.model.enums.ServiceType;
import com.autocare.platform.model.enums.UrgencyLevel;
import com.autocare.platform.service.PredictiveMaintenanceEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PredictiveMaintenanceEngineTest {

    @Autowired
    private PredictiveMaintenanceEngine engine;

    @Test
    public void testOverduePredictionCalculation() {
        User user = new User("Test Driver", "driver@test.com", "pass", "123", Role.ROLE_USER);
        // Vehicle driven 15,000 km since last service (8,000 km interval for oil)
        Vehicle vehicle = new Vehicle(
                user, "Honda", "Civic", 2020, "TEST-999", "VIN123",
                FuelType.PETROL, 55000, 1200, LocalDate.now().minusMonths(10), 40000
        );

        VehiclePredictionReport report = engine.generatePrediction(vehicle);
        assertNotNull(report);
        assertEquals(UrgencyLevel.OVERDUE, report.getOverallUrgency());
        assertTrue(report.getOverallHealthScore() < 60.0);
        assertTrue(report.getUrgentItemsCount() > 0);

        MaintenancePredictionItem oilItem = report.getPredictions().stream()
                .filter(p -> p.getServiceType() == ServiceType.OIL_CHANGE)
                .findFirst()
                .orElse(null);

        assertNotNull(oilItem);
        assertEquals(UrgencyLevel.OVERDUE, oilItem.getUrgency());
        assertTrue(oilItem.getRemainingKm() < 0);
    }

    @Test
    public void testElectricVehicleExcludesOilChange() {
        User user = new User("EV Driver", "ev@test.com", "pass", "123", Role.ROLE_USER);
        Vehicle ev = new Vehicle(
                user, "Tesla", "Model Y", 2023, "EV-TEST", "VIN456",
                FuelType.ELECTRIC, 15000, 1000, LocalDate.now().minusMonths(2), 14000
        );

        VehiclePredictionReport report = engine.generatePrediction(ev);
        assertNotNull(report);
        // Engine oil change should NOT be in the report for an EV
        boolean hasOil = report.getPredictions().stream()
                .anyMatch(p -> p.getServiceType() == ServiceType.OIL_CHANGE);
        assertFalse(hasOil, "Electric vehicle should not have an oil change service item");
    }
}
