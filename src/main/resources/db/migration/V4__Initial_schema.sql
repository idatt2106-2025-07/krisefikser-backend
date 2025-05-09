CREATE TABLE household_invitations (
   id           INT PRIMARY KEY AUTO_INCREMENT,
   household_id BIGINT NOT NULL,
   invited_by_user_id BIGINT NOT NULL,
   invited_email VARCHAR(255) NOT NULL,
   invitation_token VARCHAR(255) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (household_id) REFERENCES households(id),
   FOREIGN KEY (invited_by_user_id) REFERENCES users(id)
);
