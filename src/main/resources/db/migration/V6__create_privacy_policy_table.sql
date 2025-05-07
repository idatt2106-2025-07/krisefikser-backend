CREATE TABLE privacy_policy (
      id INT PRIMARY KEY AUTO_INCREMENT,
      registered TEXT NOT NULL,
      unregistered TEXT NOT NULL
);

INSERT INTO privacy_policy (registered, unregistered)
VALUES ('placeholder_registered', 'placeholder_unregistered');