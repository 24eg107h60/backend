package com.autocare.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"user"})
    private Vehicle vehicle;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "mileage_at_service", nullable = false)
    private Integer mileageAtService;

    @Column(nullable = false)
    private Double cost = 0.0;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "service_center", length = 150)
    private String serviceCenter;

    @Column(name = "technician_notes", columnDefinition = "TEXT")
    private String technicianNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ServiceRecord() {}

    public ServiceRecord(Vehicle vehicle, Long bookingId, String serviceType, String description,
                         LocalDate serviceDate, Integer mileageAtService, Double cost,
                         String invoiceNumber, String serviceCenter, String technicianNotes) {
        this.vehicle = vehicle;
        this.bookingId = bookingId;
        this.serviceType = serviceType;
        this.description = description;
        this.serviceDate = serviceDate;
        this.mileageAtService = mileageAtService;
        this.cost = cost != null ? cost : 0.0;
        this.invoiceNumber = invoiceNumber;
        this.serviceCenter = serviceCenter;
        this.technicianNotes = technicianNotes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
