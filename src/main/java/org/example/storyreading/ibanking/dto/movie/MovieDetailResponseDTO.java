package org.example.storyreading.ibanking.dto.movie;

import org.example.storyreading.ibanking.entity.ScreeningType;

import java.time.LocalDate;
import java.util.Set;

public class MovieDetailResponseDTO {
    private Long movieId;
    private String title;
    private String genre;
    private String genreDisplay;
    private Integer durationMinutes;
    private Set<ScreeningType> screeningTypes;
    private String trailerUrl;
    private String posterUrl;
    private String description;
    private String director;
    private String cast;
    private Integer ageRating;
    private LocalDate releaseDate;
    private String language;
    private String languageDisplay;
    private String country;

    // Constructors
    public MovieDetailResponseDTO() {
    }

    public MovieDetailResponseDTO(Long movieId, String title, String genre, String genreDisplay,
                                  Integer durationMinutes, Set<ScreeningType> screeningTypes,
                                  String trailerUrl, String posterUrl, String description,
                                  String director, String cast, Integer ageRating,
                                  LocalDate releaseDate, String language, String languageDisplay,
                                  String country) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.genreDisplay = genreDisplay;
        this.durationMinutes = durationMinutes;
        this.screeningTypes = screeningTypes;
        this.trailerUrl = trailerUrl;
        this.posterUrl = posterUrl;
        this.description = description;
        this.director = director;
        this.cast = cast;
        this.ageRating = ageRating;
        this.releaseDate = releaseDate;
        this.language = language;
        this.languageDisplay = languageDisplay;
        this.country = country;
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Set<ScreeningType> getScreeningTypes() {
        return screeningTypes;
    }

    public void setScreeningTypes(Set<ScreeningType> screeningTypes) {
        this.screeningTypes = screeningTypes;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(Integer ageRating) {
        this.ageRating = ageRating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageDisplay() {
        return languageDisplay;
    }

    public void setLanguageDisplay(String languageDisplay) {
        this.languageDisplay = languageDisplay;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

