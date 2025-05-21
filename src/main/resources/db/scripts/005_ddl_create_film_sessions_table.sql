CREATE TABLE film_sessions (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    film_id    INT NOT NULL,
    halls_id   INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time   DATETIME NOT NULL,
    price      INT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (halls_id) REFERENCES halls(id)
);
