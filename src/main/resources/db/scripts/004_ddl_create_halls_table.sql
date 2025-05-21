CREATE TABLE halls (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    row_count   INT NOT NULL,
    place_count INT NOT NULL,
    description VARCHAR(1024) NOT NULL
);
