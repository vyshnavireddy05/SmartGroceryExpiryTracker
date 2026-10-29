package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.NotificationDTO;
import com.project.groceryTracker.Service.NotificationManager;
import com.project.groceryTracker.Service.ExpiryNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationManager notificationManager;
    private final ExpiryNotificationService expiryNotificationService;

    public NotificationController(
            NotificationManager notificationManager,
            ExpiryNotificationService expiryNotificationService) {
        this.notificationManager = notificationManager;
        this.expiryNotificationService = expiryNotificationService;
    }

    // Fetch notifications belonging to the authenticated user
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NotificationDTO> notifications =
                notificationManager.getUserNotifications(
                        userDetails.getUsername());

        return ResponseEntity.ok(notifications);
    }

    // Check expiry for the authenticated user's inventory only
    @GetMapping("/check-expiry")
    public ResponseEntity<String> checkExpiryNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        expiryNotificationService.checkExpiryNotifications(
                userDetails.getUsername());

        return ResponseEntity.ok("Expiry notifications checked");
    }
}
