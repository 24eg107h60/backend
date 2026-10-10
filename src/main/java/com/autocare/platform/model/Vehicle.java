package com.autocare.platform.model;

import com.autocare.platform.model.enums.FuelType;
import com.autocare.platform.model.enums.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "createdAt"})
    private User user;

    @NotBlank(message = "Make is required")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1980, message = "Year must be 1980 or later")
    @Column(name = "vehicle_year", nullable = false)
    private Integer year;

    @NotBlank(message = "License plate is required")
    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(length = 50)
    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 20)
    private FuelType fuelType = FuelType.PETROL;

    @NotNull(message = "Current mileage is required")
    @Min(value = 0, message = "Current mileage must be non-negative")
    @Column(name = "current_mileage", nullable = false)
    private Integer currentMileage = 0;

    @Column(name = "avg_monthly_mileage")
    private Integer avgMonthlyMileage = 1200;

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "last_service_mileage")
    private Integer lastServiceMileage = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(User user, String make, String model, Integer year, String licensePlate,
                   String vin, FuelType fuelType, Integer currentMileage, Integer avgMonthlyMileage,
                   LocalDate lastServiceDate, Integer lastServiceMileage) {
        this.user = user;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.vin = vin;
        this.fuelType = fuelType != null ? fuelType : FuelType.PETROL;
        this.currentMileage = currentMileage != null ? currentMileage : 0;
        this.avgMonthlyMileage = (avgMonthlyMileage != null && avgMonthlyMileage > 0) ? avgMonthlyMileage : 1200;
        this.lastServiceDate = lastServiceDate;
        this.lastServiceMileage = lastServiceMileage != null ? lastServiceMileage : 0;
        this.status = VehicleStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getDisplayName() {
        return year + " " + make + " " + model + " (" + licensePlate + ")";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public Integer getAvgMonthlyMileage() {
        return avgMonthlyMileage;
    }

    public void setAvgMonthlyMileage(Integer avgMonthlyMileage) {
        this.avgMonthlyMileage = avgMonthlyMileage;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Integer getLastServiceMileage() {
        return lastServiceMileage;
    }

    public void setLastServiceMileage(Integer lastServiceMileage) {
        this.lastServiceMileage = lastServiceMileage;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
