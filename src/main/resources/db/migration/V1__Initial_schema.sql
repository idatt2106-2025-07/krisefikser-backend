CREATE TABLE households
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(255) NOT NULL UNIQUE,
    longitude DOUBLE       NOT NULL,
    latitude  DOUBLE       NOT NULL
);

CREATE TABLE users
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    email        VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    household_id INT          NOT NULL,
    password     VARCHAR(255) NOT NULL,
    role         ENUM('role_normal',
    'role_admin',
    'role_super_admin'
) NOT NULL DEFAULT 'normal',
    verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (household_id) REFERENCES households(id),
    INDEX (household_id)
);

CREATE TABLE non_user_members
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type ENUM('animal',
    'child',
    'other'
) NOT NULL,
    household_id INT NOT NULL,
    FOREIGN KEY (household_id) REFERENCES households(id) ON DELETE CASCADE,
    INDEX (household_id)
);

CREATE TABLE items
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL UNIQUE,
    unit     VARCHAR(50)  NOT NULL,
    calories INT          NOT NULL CHECK (calories >= 0),
    type     ENUM('drink',
    'food',
    'accessories'
) NOT NULL
);

CREATE TABLE storage_items
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    expiration_date TIMESTAMP NOT NULL,
    quantity        DOUBLE       NOT NULL CHECK (quantity >= 0),
    household_id    INT       NOT NULL,
    item_id         INT       NOT NULL,
    FOREIGN KEY (household_id) REFERENCES households (id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    INDEX (household_id),
    INDEX (item_id)
);

CREATE TABLE points_of_interest
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    longitude DOUBLE NOT NULL,
    latitude  DOUBLE NOT NULL,
    type      ENUM('shelter',
    'food_central',
    'water_station',
    'defibrillator',
    'hospital',
    'meeting_place'
) NOT NULL,
    opens_at TIME DEFAULT NULL,
    closes_at TIME DEFAULT NULL,
    contact_number VARCHAR(50) DEFAULT NULL,
    description TEXT DEFAULT NULL);

CREATE TABLE affected_areas
(
    id                      INT PRIMARY KEY AUTO_INCREMENT,
    name                    VARCHAR(255) NOT NULL,
    longitude               DOUBLE NOT NULL,
    latitude                DOUBLE NOT NULL,
    high_danger_radius_km          DOUBLE NOT NULL,
    medium_danger_radius_km        DOUBLE NOT NULL,
    low_danger_radius_km           DOUBLE NOT NULL,
    severity_level        INT NOT NULL CHECK (severity_level >= 1 AND severity_level <= 3),
    description    TEXT   NOT NULL,
    start_time TIMESTAMP NOT NULL
);

CREATE TABLE join_household_requests
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    user_id      INT NOT NULL,
    household_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (household_id) REFERENCES households (id),
    INDEX (user_id),
    INDEX (household_id)
);
