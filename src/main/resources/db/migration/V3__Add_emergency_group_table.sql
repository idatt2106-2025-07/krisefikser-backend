CREATE TABLE emergency_groups (
    id int PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE households
add column emergency_group_id INT DEFAULT NULL;

ALTER TABLE households
ADD CONSTRAINT fk_emergency_group FOREIGN KEY (emergency_group_id) REFERENCES emergency_groups(id);

ALTER TABLE storage_items
add column is_shared BOOLEAN DEFAULT FALSE;

CREATE TABLE emergency_group_invitations (
    id int PRIMARY KEY AUTO_INCREMENT,
    household_id INT NOT NULL,
    emergency_group_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (emergency_group_id) REFERENCES emergency_groups(id)
);