package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinema_halls")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private Long hallId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "hall_name", nullable = false, length = 100)
    private String hallName; // Ví dụ: "Phòng 1", "Phòng VIP 2"

    @NotNull
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @OneToMany(mappedBy = "cinemaHall", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "cinemaHall", cascade = CascadeType.ALL)
    private List<MovieScreening> screenings = new ArrayList<>();

    // Constructors
    public CinemaHall() {
    }

    // Getters and Setters
    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<MovieScreening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<MovieScreening> screenings) {
        this.screenings = screenings;
    }
}

