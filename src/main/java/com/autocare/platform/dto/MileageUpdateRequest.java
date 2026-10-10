package com.autocare.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MileageUpdateRequest {

    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    public MileageUpdateRequest() {}

    public MileageUpdateRequest(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }
}
