package com.autocare.platform.dto;

import com.autocare.platform.model.enums.ServiceType;
import com.autocare.platform.model.enums.UrgencyLevel;

import java.time.LocalDate;

public class MaintenancePredictionItem {

    private ServiceType serviceType;
    private String serviceName;
    private String category;
    private int intervalKm;
    private int intervalMonths;
    private int currentKm;
    private int lastServiceKm;
    private LocalDate lastServiceDate;
    private int remainingKm;
    private int remainingDays;
    private LocalDate predictedDueDate;
    private double healthPercentage;
    private UrgencyLevel urgency;
    private double estimatedCost;
    private String recommendation;
    private String icon;

    public MaintenancePredictionItem() {}

    public MaintenancePredictionItem(ServiceType serviceType, String serviceName, String category,
                                     int intervalKm, int intervalMonths, int currentKm, int lastServiceKm,
                                     LocalDate lastServiceDate, int remainingKm, int remainingDays,
                                     LocalDate predictedDueDate, double healthPercentage,
                                     UrgencyLevel urgency, double estimatedCost,
                                     String recommendation, String icon) {
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.category = category;
        this.intervalKm = intervalKm;
        this.intervalMonths = intervalMonths;
        this.currentKm = currentKm;
        this.lastServiceKm = lastServiceKm;
        this.lastServiceDate = lastServiceDate;
        this.remainingKm = remainingKm;
        this.remainingDays = remainingDays;
        this.predictedDueDate = predictedDueDate;
        this.healthPercentage = Math.round(healthPercentage * 10.0) / 10.0;
        this.urgency = urgency;
        this.estimatedCost = estimatedCost;
        this.recommendation = recommendation;
        this.icon = icon;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIntervalKm() {
        return intervalKm;
    }

    public void setIntervalKm(int intervalKm) {
        this.intervalKm = intervalKm;
    }

    public int getIntervalMonths() {
        return intervalMonths;
    }

    public void setIntervalMonths(int intervalMonths) {
        this.intervalMonths = intervalMonths;
    }

    public int getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(int currentKm) {
        this.currentKm = currentKm;
    }

    public int getLastServiceKm() {
        return lastServiceKm;
    }

    public void setLastServiceKm(int lastServiceKm) {
        this.lastServiceKm = lastServiceKm;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public int getRemainingKm() {
        return remainingKm;
    }

    public void setRemainingKm(int remainingKm) {
        this.remainingKm = remainingKm;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }

    public LocalDate getPredictedDueDate() {
        return predictedDueDate;
    }

    public void setPredictedDueDate(LocalDate predictedDueDate) {
        this.predictedDueDate = predictedDueDate;
    }

    public double getHealthPercentage() {
        return healthPercentage;
    }

    public void setHealthPercentage(double healthPercentage) {
        this.healthPercentage = healthPercentage;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
