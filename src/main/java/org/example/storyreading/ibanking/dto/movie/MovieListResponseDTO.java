package org.example.storyreading.ibanking.dto.movie;

import org.example.storyreading.ibanking.entity.ScreeningType;

import java.time.LocalDate;
import java.util.Set;

public class MovieListResponseDTO {
    private Long movieId;
    private String title;
    private String genre;
    private String genreDisplay;
    private LocalDate releaseDate;
    private Integer durationMinutes;
    private Integer ageRating;
    private String posterUrl;
    private Set<ScreeningType> screeningTypes;

    // Constructors
    public MovieListResponseDTO() {
    }

    public MovieListResponseDTO(Long movieId, String title, String genre, String genreDisplay,
                                LocalDate releaseDate, Integer durationMinutes, Integer ageRating,
                                String posterUrl, Set<ScreeningType> screeningTypes) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.genreDisplay = genreDisplay;
        this.releaseDate = releaseDate;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
        this.posterUrl = posterUrl;
        this.screeningTypes = screeningTypes;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenreDisplay() {
        return genreDisplay;
    }

    public void setGenreDisplay(String genreDisplay) {
        this.genreDisplay = genreDisplay;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(Integer ageRating) {
        this.ageRating = ageRating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Set<ScreeningType> getScreeningTypes() {
        return screeningTypes;
    }

    public void setScreeningTypes(Set<ScreeningType> screeningTypes) {
        this.screeningTypes = screeningTypes;
    }
}

