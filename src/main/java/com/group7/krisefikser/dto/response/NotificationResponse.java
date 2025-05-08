package com.group7.krisefikser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the response sent to the client after a notification is triggered.
 * It contains a message indicating the result of the notification.
 */
@Data
@AllArgsConstructor
public class NotificationResponse {
  private String message;
}
