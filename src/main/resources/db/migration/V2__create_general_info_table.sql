CREATE TABLE general_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    theme ENUM('before_crisis',
        'during_crisis',
        'after_crisis'
) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);