package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotNull;

public class FindNearestBranchRequest {
    @NotNull(message = "Latitude không được để trống")
    private Double latitude;

    @NotNull(message = "Longitude không được để trống")
    private Double longitude;

    private Integer limit = 5; // Mặc định lấy 5 chi nhánh gần nhất

    public FindNearestBranchRequest() {
    }

    public FindNearestBranchRequest(Double latitude, Double longitude, Integer limit) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.limit = limit;
    }

    // Getters and Setters
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

