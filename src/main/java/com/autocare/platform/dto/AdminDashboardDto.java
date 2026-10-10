package com.autocare.platform.dto;

import com.autocare.platform.model.ServiceBooking;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDto {

    private long totalUsers;
    private long totalVehicles;
    private long totalBookings;
    private long pendingBookings;
    private long inProgressBookings;
    private long completedBookings;
    private double totalRevenue;
    private long overdueVehiclesCount;
    private List<ServiceBooking> recentBookings = new ArrayList<>();
    private List<VehiclePredictionReport> criticalVehicles = new ArrayList<>();

    public AdminDashboardDto() {}

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getInProgressBookings() {
        return inProgressBookings;
    }

    public void setInProgressBookings(long inProgressBookings) {
        this.inProgressBookings = inProgressBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
    }

    public long getOverdueVehiclesCount() {
        return overdueVehiclesCount;
    }

    public void setOverdueVehiclesCount(long overdueVehiclesCount) {
        this.overdueVehiclesCount = overdueVehiclesCount;
    }

    public List<ServiceBooking> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<ServiceBooking> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<VehiclePredictionReport> getCriticalVehicles() {
        return criticalVehicles;
    }

    public void setCriticalVehicles(List<VehiclePredictionReport> criticalVehicles) {
        this.criticalVehicles = criticalVehicles;
    }
}
