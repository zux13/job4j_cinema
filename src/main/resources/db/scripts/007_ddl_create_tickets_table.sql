CREATE TABLE tickets (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    session_id   INT NOT NULL,
    `row_number`   INT NOT NULL,
    place_number INT NOT NULL,
    user_id      INT NOT NULL,
    UNIQUE (session_id, `row_number`, place_number),
    FOREIGN KEY (session_id) REFERENCES film_sessions(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
