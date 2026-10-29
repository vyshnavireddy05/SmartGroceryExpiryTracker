package com.project.groceryTracker.Service;

import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.NotificationRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ExpiryNotificationService {

    private final InventoryRepo inventoryRepo;
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;

    public ExpiryNotificationService(
            InventoryRepo inventoryRepo,
            NotificationRepo notificationRepo,
            UserRepo userRepo) {

        this.inventoryRepo = inventoryRepo;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
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

        List<InventoryItem> items =
                inventoryRepo.findAllByUser_Id(user.getId());

        for (InventoryItem item : items) {

            if (item.getExpiryDate() == null) {
                continue;
            }

            long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    item.getExpiryDate()
            );

            int reminderDays = user.getExpiryRemainderDays();

            if (daysLeft <= reminderDays && daysLeft > 0) {

                boolean alreadyExists =
                        notificationRepo
                                .findByUserAndInventoryItemAndType(
                                        user,
                                        item,
                                        "EXPIRY"
                                )
                                .isPresent();

                if (alreadyExists) {
                    continue;
                }

                Notification notification = new Notification();

                notification.setTitle(
                        item.getItemName() + " is expiring soon");

                notification.setMessage(
                        "Your " + item.getItemName()
                                + " expires in " + daysLeft
                                + " day(s). Please use it soon.");

                notification.setType("EXPIRY");
                notification.setReadStatus(false);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setUser(user);
                notification.setInventoryItem(item);

                notificationRepo.save(notification);
            }
        }
    }
}
