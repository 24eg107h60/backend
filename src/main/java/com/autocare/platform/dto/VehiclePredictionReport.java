package com.autocare.platform.dto;

import com.autocare.platform.model.enums.UrgencyLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehiclePredictionReport {

    private Long vehicleId;
    private String vehicleName;
    private String licensePlate;
    private String fuelType;
    private int currentMileage;
    private double overallHealthScore;
    private UrgencyLevel overallUrgency;
    private int urgentItemsCount;
    private int dueSoonItemsCount;
    private int healthyItemsCount;
    private double totalEstimatedCost;
    private List<MaintenancePredictionItem> predictions = new ArrayList<>();
    private List<String> keyAlerts = new ArrayList<>();
    private LocalDateTime generatedAt = LocalDateTime.now();

    public VehiclePredictionReport() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }

    public double getOverallHealthScore() {
        return overallHealthScore;
    }

    public void setOverallHealthScore(double overallHealthScore) {
        this.overallHealthScore = Math.round(overallHealthScore * 10.0) / 10.0;
    }

    public UrgencyLevel getOverallUrgency() {
        return overallUrgency;
    }

    public void setOverallUrgency(UrgencyLevel overallUrgency) {
        this.overallUrgency = overallUrgency;
    }

    public int getUrgentItemsCount() {
        return urgentItemsCount;
    }

    public void setUrgentItemsCount(int urgentItemsCount) {
        this.urgentItemsCount = urgentItemsCount;
    }

    public int getDueSoonItemsCount() {
        return dueSoonItemsCount;
    }

    public void setDueSoonItemsCount(int dueSoonItemsCount) {
        this.dueSoonItemsCount = dueSoonItemsCount;
    }

    public int getHealthyItemsCount() {
        return healthyItemsCount;
    }

    public void setHealthyItemsCount(int healthyItemsCount) {
        this.healthyItemsCount = healthyItemsCount;
    }

    public double getTotalEstimatedCost() {
        return totalEstimatedCost;
    }

    public void setTotalEstimatedCost(double totalEstimatedCost) {
        this.totalEstimatedCost = Math.round(totalEstimatedCost * 100.0) / 100.0;
    }

    public List<MaintenancePredictionItem> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<MaintenancePredictionItem> predictions) {
        this.predictions = predictions;
    }

    public List<String> getKeyAlerts() {
        return keyAlerts;
    }

    public void setKeyAlerts(List<String> keyAlerts) {
        this.keyAlerts = keyAlerts;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
