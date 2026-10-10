package com.autocare.platform.dto;

import com.autocare.platform.model.ServiceRecord;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CostReportDto {

    private double totalExpenditure;
    private int totalServices;
    private double averageCostPerService;
    private Map<String, Double> costByCategory = new LinkedHashMap<>();
    private Map<String, Double> costByMonth = new LinkedHashMap<>();
    private Map<String, Double> costByVehicle = new LinkedHashMap<>();
    private List<ServiceRecord> recentRecords;

    public CostReportDto() {}

    public double getTotalExpenditure() {
        return totalExpenditure;
    }

    public void setTotalExpenditure(double totalExpenditure) {
        this.totalExpenditure = Math.round(totalExpenditure * 100.0) / 100.0;
    }

    public int getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(int totalServices) {
        this.totalServices = totalServices;
    }

    public double getAverageCostPerService() {
        return averageCostPerService;
    }

    public void setAverageCostPerService(double averageCostPerService) {
        this.averageCostPerService = Math.round(averageCostPerService * 100.0) / 100.0;
    }

    public Map<String, Double> getCostByCategory() {
        return costByCategory;
    }

    public void setCostByCategory(Map<String, Double> costByCategory) {
        this.costByCategory = costByCategory;
    }

    public Map<String, Double> getCostByMonth() {
        return costByMonth;
    }

    public void setCostByMonth(Map<String, Double> costByMonth) {
        this.costByMonth = costByMonth;
    }

    public Map<String, Double> getCostByVehicle() {
        return costByVehicle;
    }

    public void setCostByVehicle(Map<String, Double> costByVehicle) {
        this.costByVehicle = costByVehicle;
    }

    public List<ServiceRecord> getRecentRecords() {
        return recentRecords;
    }

    public void setRecentRecords(List<ServiceRecord> recentRecords) {
        this.recentRecords = recentRecords;
    }
}
