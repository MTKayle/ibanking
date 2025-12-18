-- Create movies table
CREATE TABLE movies (
    movie_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    release_date DATE NOT NULL,
    age_rating INT NOT NULL,
    director VARCHAR(255),
    cast TEXT,
    country VARCHAR(100),
    language VARCHAR(50),
    poster_url VARCHAR(500),
    trailer_url VARCHAR(500),
    is_showing BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_is_showing (is_showing),
    INDEX idx_release_date (release_date),
    INDEX idx_genre (genre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

