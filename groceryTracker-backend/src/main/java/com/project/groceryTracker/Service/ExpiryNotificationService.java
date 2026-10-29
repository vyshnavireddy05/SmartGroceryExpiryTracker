package com.project.groceryTracker.Service;

import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.NotificationRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ExpiryNotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(ExpiryNotificationService.class);

    private final InventoryRepo inventoryRepo;
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final SmsNotificationService smsNotificationService;

    public ExpiryNotificationService(
            InventoryRepo inventoryRepo,
            NotificationRepo notificationRepo,
            UserRepo userRepo,
            NotificationService notificationService,
            SmsNotificationService smsNotificationService) {

        this.inventoryRepo = inventoryRepo;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.smsNotificationService = smsNotificationService;
    }

    @Scheduled(
            fixedDelayString = "${app.notification.check-interval-ms:3600000}",
            initialDelayString = "${app.notification.initial-delay-ms:60000}"
    )
    public void checkAllUsersForExpiry() {
        for (User user : userRepo.findAll()) {
            try {
                checkExpiryNotifications(user.getEmail());
            } catch (RuntimeException exception) {
                logger.error(
                        "Expiry check failed for user id {}",
                        user.getId(),
                        exception
                );
            }
        }
    }

    @Transactional
    public void checkExpiryNotifications(String email) {
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        if (!user.isNotificationsEnabled()) {
            return;
        }

        int reminderDays = Math.max(0, user.getExpiryRemainderDays());
        List<InventoryItem> items =
                inventoryRepo.findAllByUser_Id(user.getId());

        for (InventoryItem item : items) {
            LocalDate expiryDate = item.getExpiryDate();
            if (expiryDate == null) {
                continue;
            }

            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            String type;
            if (daysLeft <= 0) {
                type = "EXPIRED";
            } else if (daysLeft <= reminderDays) {
                type = "EXPIRY";
            } else {
                continue;
            }

            String eventKey = type + ":" + item.getId() + ":" + expiryDate;
            Notification notification = notificationRepo
                    .findByEventKey(eventKey)
                    .orElseGet(() -> createNotification(
                            user,
                            item,
                            expiryDate,
                            daysLeft,
                            type,
                            eventKey
                    ));

            deliverNotification(user, notification);
        }
    }

    private Notification createNotification(
            User user,
            InventoryItem item,
            LocalDate expiryDate,
            long daysLeft,
            String type,
            String eventKey) {

        Notification notification = new Notification();
        notification.setTitle(type.equals("EXPIRED")
                ? item.getItemName() + " has expired"
                : item.getItemName() + " is expiring soon");
        notification.setMessage(type.equals("EXPIRED")
                ? "Your " + item.getItemName() + " expired on " + expiryDate + "."
                : "Your " + item.getItemName()
                        + " expires in " + daysLeft
                        + " day(s). Please use it soon.");
        notification.setType(type);
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(user);
        notification.setInventoryItem(item);
        notification.setExpiryDate(expiryDate);
        notification.setEventKey(eventKey);
        return notificationRepo.saveAndFlush(notification);
    }

    private void deliverNotification(User user, Notification notification) {
        if ("PENDING".equals(notification.getEmailDeliveryStatus())) {
            String email = user.getEmail();
            if (isValidEmail(email)) {
                try {
                    notificationService.sendEmail(email, notification.getMessage());
                    notification.setEmailDeliveryStatus("SENT");
                } catch (MailException exception) {
                    notification.setEmailDeliveryStatus("FAILED");
                    logger.error(
                            "Expiry email delivery failed for notification {}",
                            notification.getId(),
                            exception
                    );
                }
            } else {
                notification.setEmailDeliveryStatus("SKIPPED");
                logger.warn(
                        "Expiry email skipped for user id {} because the email is missing or invalid",
                        user.getId()
                );
            }
        }

        if ("PENDING".equals(notification.getSmsDeliveryStatus())) {
            String phoneNumber = user.getPhoneNum();
            if (!smsNotificationService.isConfigured()) {
                notification.setSmsDeliveryStatus("SKIPPED");
            } else if (!smsNotificationService.isValidPhoneNumber(phoneNumber)) {
                notification.setSmsDeliveryStatus("SKIPPED");
                logger.warn(
                        "Expiry SMS skipped for user id {} because the phone number is missing or invalid",
                        user.getId()
                );
            } else {
                try {
                    notificationService.sendSms(
                            phoneNumber,
                            notification.getMessage()
                    );
                    notification.setSmsDeliveryStatus("SENT");
                } catch (RuntimeException exception) {
                    notification.setSmsDeliveryStatus("FAILED");
                    logger.error(
                            "Expiry SMS delivery failed for notification {}",
                            notification.getId(),
                            exception
                    );
                }
            }
        }

        notificationRepo.save(notification);
        logger.info(
                "Expiry notification {} delivery status: email={}, sms={}",
                notification.getId(),
                notification.getEmailDeliveryStatus(),
                notification.getSmsDeliveryStatus()
        );
    }

    private boolean isValidEmail(String email) {
        return email != null
                && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
}
