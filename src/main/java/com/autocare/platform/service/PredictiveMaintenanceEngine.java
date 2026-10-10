package com.autocare.platform.service;

import com.autocare.platform.dto.MaintenancePredictionItem;
import com.autocare.platform.dto.VehiclePredictionReport;
import com.autocare.platform.model.ServiceRecord;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.model.enums.FuelType;
import com.autocare.platform.model.enums.ServiceType;
import com.autocare.platform.model.enums.UrgencyLevel;
import com.autocare.platform.repository.ServiceRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PredictiveMaintenanceEngine {

    private final ServiceRecordRepository recordRepository;
    private final NotificationService notificationService;

    public PredictiveMaintenanceEngine(ServiceRecordRepository recordRepository,
                                      NotificationService notificationService) {
        this.recordRepository = recordRepository;
        this.notificationService = notificationService;
    }

    /**
     * Generates a comprehensive, rule-based predictive maintenance report for a vehicle.
     */
    public VehiclePredictionReport generatePrediction(Vehicle vehicle) {
        VehiclePredictionReport report = new VehiclePredictionReport();
        report.setVehicleId(vehicle.getId());
        report.setVehicleName(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
        report.setLicensePlate(vehicle.getLicensePlate());
        report.setFuelType(vehicle.getFuelType() != null ? vehicle.getFuelType().name() : "PETROL");
        report.setCurrentMileage(vehicle.getCurrentMileage());
        report.setGeneratedAt(LocalDateTime.now());

        List<MaintenancePredictionItem> items = new ArrayList<>();
        List<String> keyAlerts = new ArrayList<>();
        double totalEstimatedCost = 0.0;
        int urgentCount = 0;
        int dueSoonCount = 0;
        int healthyCount = 0;
        double totalWeightedHealth = 0.0;
        double totalWeights = 0.0;

        for (ServiceType serviceType : ServiceType.values()) {
            // Check applicability for electric vehicles
            if (vehicle.getFuelType() == FuelType.ELECTRIC && !serviceType.isAppliesToElectric()) {
                continue;
            }

            MaintenancePredictionItem item = evaluateServiceItem(vehicle, serviceType);
            items.add(item);

            // Weights for overall calculation
            double weight = getComponentWeight(serviceType);
            totalWeightedHealth += item.getHealthPercentage() * weight;
            totalWeights += weight;

            if (item.getUrgency() == UrgencyLevel.OVERDUE) {
                urgentCount++;
                totalEstimatedCost += item.getEstimatedCost();
                keyAlerts.add("CRITICAL: " + item.getServiceName() + " is overdue by "
                        + Math.abs(item.getRemainingKm()) + " km / " + Math.abs(item.getRemainingDays()) + " days!");
            } else if (item.getUrgency() == UrgencyLevel.DUE_SOON) {
                dueSoonCount++;
                totalEstimatedCost += item.getEstimatedCost();
                keyAlerts.add("ATTENTION: " + item.getServiceName() + " due soon in "
                        + Math.max(0, item.getRemainingKm()) + " km (" + Math.max(0, item.getRemainingDays()) + " days).");
            } else {
                healthyCount++;
            }
        }

        report.setPredictions(items);
        report.setUrgentItemsCount(urgentCount);
        report.setDueSoonItemsCount(dueSoonCount);
        report.setHealthyItemsCount(healthyCount);
        report.setTotalEstimatedCost(totalEstimatedCost);
        report.setKeyAlerts(keyAlerts);

        double overallHealth = totalWeights > 0 ? (totalWeightedHealth / totalWeights) : 100.0;
        report.setOverallHealthScore(overallHealth);

        if (urgentCount > 0 || overallHealth < 50.0) {
            report.setOverallUrgency(UrgencyLevel.OVERDUE);
        } else if (dueSoonCount > 0 || overallHealth < 75.0) {
            report.setOverallUrgency(UrgencyLevel.DUE_SOON);
        } else {
            report.setOverallUrgency(UrgencyLevel.HEALTHY);
        }

        return report;
    }

    /**
     * Evaluates a single service component using current mileage, past records, and time intervals.
     */
    public MaintenancePredictionItem evaluateServiceItem(Vehicle vehicle, ServiceType serviceType) {
        // Find latest specific service record if available
        Optional<ServiceRecord> latestRecord = recordRepository
                .findLatestByVehicleAndType(vehicle.getId(), serviceType.name());

        int baseMileage;
        LocalDate baseDate;

        if (latestRecord.isPresent()) {
            baseMileage = latestRecord.get().getMileageAtService();
            baseDate = latestRecord.get().getServiceDate();
        } else if (vehicle.getLastServiceDate() != null && vehicle.getLastServiceMileage() != null && vehicle.getLastServiceMileage() > 0) {
            baseMileage = vehicle.getLastServiceMileage();
            baseDate = vehicle.getLastServiceDate();
        } else {
            // New vehicle or no prior recorded service: base on zero mileage and vehicle creation / 6 months ago
            baseMileage = 0;
            baseDate = vehicle.getCreatedAt() != null ? vehicle.getCreatedAt().toLocalDate().minusMonths(3) : LocalDate.now().minusMonths(3);
        }

        int currentKm = vehicle.getCurrentMileage() != null ? vehicle.getCurrentMileage() : 0;
        int kmDrivenSinceService = Math.max(0, currentKm - baseMileage);
        int remainingKm = serviceType.getIntervalKm() - kmDrivenSinceService;

        long daysPassed = ChronoUnit.DAYS.between(baseDate, LocalDate.now());
        if (daysPassed < 0) daysPassed = 0;
        int intervalDays = serviceType.getIntervalMonths() * 30;
        int remainingDaysByTime = intervalDays - (int) daysPassed;

        // Estimate remaining days based on average monthly mileage
        int monthlyKm = (vehicle.getAvgMonthlyMileage() != null && vehicle.getAvgMonthlyMileage() > 0)
                ? vehicle.getAvgMonthlyMileage()
                : 1200;
        double dailyKm = Math.max(5.0, (double) monthlyKm / 30.0);
        int remainingDaysByMileage = (int) Math.round((double) remainingKm / dailyKm);

        // Effective remaining days is the tighter of the two constraints
        int effectiveRemainingDays = Math.min(remainingDaysByTime, remainingDaysByMileage);
        LocalDate predictedDueDate = LocalDate.now().plusDays(effectiveRemainingDays);

        // Calculate wear ratios
        double mileageWearRatio = (double) kmDrivenSinceService / (double) serviceType.getIntervalKm();
        double timeWearRatio = (double) daysPassed / (double) intervalDays;
        double maxWearRatio = Math.max(mileageWearRatio, timeWearRatio);

        double healthPercentage = Math.max(0.0, Math.min(100.0, (1.0 - maxWearRatio) * 100.0));

        UrgencyLevel urgency;
        String recommendation;

        if (healthPercentage <= 15.0 || remainingKm <= 0 || effectiveRemainingDays <= 0) {
            urgency = UrgencyLevel.OVERDUE;
            recommendation = "Immediate service required. Running overdue increases risk of mechanical failure.";
        } else if (healthPercentage <= 40.0 || remainingKm <= 1000 || effectiveRemainingDays <= 30) {
            urgency = UrgencyLevel.DUE_SOON;
            recommendation = "Upcoming maintenance due within ~" + Math.max(1, effectiveRemainingDays) + " days. Schedule service now.";
        } else {
            urgency = UrgencyLevel.HEALTHY;
            recommendation = "Component is in healthy operating condition. Nominal wear detected.";
        }

        return new MaintenancePredictionItem(
                serviceType,
                serviceType.getDisplayName(),
                serviceType.getCategory(),
                serviceType.getIntervalKm(),
                serviceType.getIntervalMonths(),
                currentKm,
                baseMileage,
                baseDate,
                remainingKm,
                effectiveRemainingDays,
                predictedDueDate,
                healthPercentage,
                urgency,
                serviceType.getStandardCost(),
                recommendation,
                serviceType.getIcon()
        );
    }

    /**
     * Checks all components of a vehicle and sends reminder notifications if necessary.
     */
    public void checkAndTriggerReminders(Vehicle vehicle) {
        if (vehicle.getUser() == null) return;

        VehiclePredictionReport report = generatePrediction(vehicle);
        for (MaintenancePredictionItem item : report.getPredictions()) {
            if (item.getUrgency() == UrgencyLevel.OVERDUE) {
                String title = "Overdue Alert: " + item.getServiceName();
                String message = "Your " + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() +
                        ") is overdue for " + item.getServiceName() + ". Please book a service appointment immediately to avoid engine or component breakdown.";
                notificationService.createNotification(vehicle.getUser(), vehicle, title, message, "OVERDUE_ALERT");
            } else if (item.getUrgency() == UrgencyLevel.DUE_SOON) {
                String title = "Maintenance Reminder: " + item.getServiceName();
                String message = "Your " + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() +
                        ") will require " + item.getServiceName() + " in approximately " + Math.max(1, item.getRemainingDays()) +
                        " days (approx. " + Math.max(0, item.getRemainingKm()) + " km remaining).";
                notificationService.createNotification(vehicle.getUser(), vehicle, title, message, "MAINTENANCE_DUE");
            }
        }
    }

    private double getComponentWeight(ServiceType type) {
        return switch (type) {
            case OIL_CHANGE -> 1.5;
            case BRAKE_INSPECTION -> 1.5;
            case BATTERY_MAINTENANCE -> 1.2;
            case TIRE_ROTATION -> 1.0;
            case GENERAL_SERVICING -> 1.2;
            case TRANSMISSION_FLUID -> 1.1;
            case AIR_FILTER -> 0.8;
        };
    }
}
