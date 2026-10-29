package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean readStatus;
    private LocalDateTime createdAt;
}