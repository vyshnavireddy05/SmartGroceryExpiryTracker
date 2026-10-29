package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.NotificationDTO;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.NotificationRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationManager implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final EmailNotificationService emailNotificationService;
    private final SmsNotificationService smsNotificationService;

    public NotificationManager(
            NotificationRepo notificationRepo,
            UserRepo userRepo,
            EmailNotificationService emailNotificationService,
            SmsNotificationService smsNotificationService) {

        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.emailNotificationService = emailNotificationService;
        this.smsNotificationService = smsNotificationService;
    }

    @Override
    public void sendEmail(String email, String message) {
        emailNotificationService.sendEmail(email, message);
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        smsNotificationService.sendSms(phoneNumber, message);
    }

    public List<NotificationDTO> getUserNotifications(String email) {

        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        return notificationRepo
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getType(),
                        notification.isReadStatus(),
                        notification.getCreatedAt()
                ))
                .toList();
    }
}
