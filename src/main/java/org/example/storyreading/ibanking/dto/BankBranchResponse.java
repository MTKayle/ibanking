package org.example.storyreading.ibanking.dto;

public class BankBranchResponse {
    private Long branchId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distance; // Khoảng cách tính bằng km

    public BankBranchResponse() {
    }

    public BankBranchResponse(Long branchId, String name, String address,
                              Double latitude, Double longitude, Double distance) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    // Getters and Setters
    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}

