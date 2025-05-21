CREATE TABLE films (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    description         VARCHAR(1024) NOT NULL,
    `year`              INT NOT NULL,
    genre_id            INT NOT NULL,
    minimal_age         INT NOT NULL,
    duration_in_minutes INT NOT NULL,
    file_id             INT NOT NULL,
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    FOREIGN KEY (file_id) REFERENCES files(id)
);
