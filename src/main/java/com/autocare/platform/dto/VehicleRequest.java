package com.autocare.platform.dto;

import com.autocare.platform.model.enums.FuelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class VehicleRequest {

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1980, message = "Year must be 1980 or later")
    private Integer year;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    private String vin;

    private FuelType fuelType = FuelType.PETROL;

    @NotNull(message = "Current mileage is required")
    @Min(value = 0, message = "Current mileage cannot be negative")
    private Integer currentMileage;

    private Integer avgMonthlyMileage = 1200;

    private LocalDate lastServiceDate;

    private Integer lastServiceMileage = 0;

    public VehicleRequest() {}

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
}
