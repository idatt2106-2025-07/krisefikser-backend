CREATE TABLE user_position (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT            NOT NULL,
    longitude DOUBLE       NOT NULL,
    latitude  DOUBLE       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX (user_id)
);

ALTER TABLE users
    ADD COLUMN share_position BOOLEAN NOT NULL DEFAULT FALSE;