package org.example.storyreading.ibanking.dto.movie;

import java.util.List;

public class CinemaScreeningResponseDTO {
    private Long cinemaId;
    private String cinemaName;
    private String cinemaAddress;
    private String cinemaPhone;
    private String city;
    private Double latitude;
    private Double longitude;
    private List<ScreeningResponseDTO> screenings;

    // Constructors
    public CinemaScreeningResponseDTO() {
    }

    public CinemaScreeningResponseDTO(Long cinemaId, String cinemaName, String cinemaAddress,
                                     String cinemaPhone, String city, Double latitude,
                                     Double longitude, List<ScreeningResponseDTO> screenings) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.cinemaAddress = cinemaAddress;
        this.cinemaPhone = cinemaPhone;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.screenings = screenings;
    }

    // Getters and Setters
    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getCinemaPhone() {
        return cinemaPhone;
    }

    public void setCinemaPhone(String cinemaPhone) {
        this.cinemaPhone = cinemaPhone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public List<ScreeningResponseDTO> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<ScreeningResponseDTO> screenings) {
        this.screenings = screenings;
    }
}

