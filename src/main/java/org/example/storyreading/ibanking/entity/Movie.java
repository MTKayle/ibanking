package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 50)
    private MovieGenre genre;

    @NotNull
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @NotNull
    @Column(name = "age_rating", nullable = false)
    private Integer ageRating; // Độ tuổi giới hạn (0, 13, 16, 18)

    @Size(max = 255)
    @Column(name = "director", length = 255)
    private String director;

    @Column(name = "movie_cast", columnDefinition = "TEXT")
    private String cast; // Các diễn viên chính, cách nhau bằng dấu phẩy

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country; // Quốc gia sản xuất

    @Enumerated(EnumType.STRING)
    @Column(name = "movie_language", length = 50)
    private Language language;

    @Column(name = "poster_url", length = 500)
    private String posterUrl; // URL ảnh poster

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl; // URL trailer

    @NotNull
    @Column(name = "is_showing", nullable = false)
    private Boolean isShowing = true; // Đang chiếu hay không

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MovieScreening> screenings = new ArrayList<>();

    // Constructors
    public Movie() {
    }

    // Getters and Setters
    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(Integer ageRating) {
        this.ageRating = ageRating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public Boolean getIsShowing() {
        return isShowing;
    }

    public void setIsShowing(Boolean isShowing) {
        this.isShowing = isShowing;
    }

    public List<MovieScreening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<MovieScreening> screenings) {
        this.screenings = screenings;
    }
}
