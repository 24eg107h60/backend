package com.autocare.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ServiceRecordRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    private Long bookingId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    private String description;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    @NotNull(message = "Mileage at service is required")
    @Min(value = 0, message = "Mileage must be non-negative")
    private Integer mileageAtService;

    @NotNull(message = "Cost is required")
    @Min(value = 0, message = "Cost must be non-negative")
    private Double cost;

    private String invoiceNumber;

    private String serviceCenter;

    private String technicianNotes;

    public ServiceRecordRequest() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Integer getMileageAtService() {
        return mileageAtService;
    }

    public void setMileageAtService(Integer mileageAtService) {
        this.mileageAtService = mileageAtService;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public String getTechnicianNotes() {
        return technicianNotes;
    }

    public void setTechnicianNotes(String technicianNotes) {
        this.technicianNotes = technicianNotes;
    }
}
