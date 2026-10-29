package com.project.groceryTracker.Service;

import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.NotificationRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpiryNotificationServiceTest {

    private InventoryRepo inventoryRepo;
    private NotificationRepo notificationRepo;
    private UserRepo userRepo;
    private NotificationService notificationService;
    private SmsNotificationService smsNotificationService;
    private ExpiryNotificationService expiryNotificationService;

    @BeforeEach
    void setUp() {
        inventoryRepo = mock(InventoryRepo.class);
        notificationRepo = mock(NotificationRepo.class);
        userRepo = mock(UserRepo.class);
        notificationService = mock(NotificationService.class);
        smsNotificationService = mock(SmsNotificationService.class);
        expiryNotificationService = new ExpiryNotificationService(
                inventoryRepo,
                notificationRepo,
                userRepo,
                notificationService,
                smsNotificationService
        );

        when(notificationRepo.saveAndFlush(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepo.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsOneNotificationAtConfiguredReminderBoundary() {
        User user = createUser(true);
        InventoryItem item = createItem(user, LocalDate.now().plusDays(7));
        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findAllByUser_Id(user.getId()))
                .thenReturn(List.of(item));
        when(notificationRepo.findByEventKey(
                "EXPIRY:" + item.getId() + ":" + item.getExpiryDate()
        )).thenReturn(Optional.empty());

        expiryNotificationService.checkExpiryNotifications(user.getEmail());

        verify(notificationRepo).saveAndFlush(any(Notification.class));
        verify(notificationService).sendEmail(
                eq(user.getEmail()),
                contains("expires in 7 day(s)")
        );
        verify(notificationService, never()).sendSms(anyString(), anyString());
    }

    @Test
    void doesNotCheckInventoryWhenNotificationsAreDisabled() {
        User user = createUser(false);
        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        expiryNotificationService.checkExpiryNotifications(user.getEmail());

        verifyNoInteractions(inventoryRepo, notificationRepo, notificationService);
    }

    @Test
    void doesNotRetryFailedProviderDeliveryOnEveryScheduledCheck() {
        User user = createUser(true);
        InventoryItem item = createItem(user, LocalDate.now().minusDays(1));
        Notification existing = new Notification();
        existing.setId(41L);
        existing.setEventKey(
                "EXPIRED:" + item.getId() + ":" + item.getExpiryDate()
        );
        existing.setType("EXPIRED");
        existing.setMessage("Your milk expired yesterday.");
        existing.setEmailDeliveryStatus("FAILED");
        existing.setSmsDeliveryStatus("SENT");

        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findAllByUser_Id(user.getId()))
                .thenReturn(List.of(item));
        when(notificationRepo.findByEventKey(existing.getEventKey()))
                .thenReturn(Optional.of(existing));
        expiryNotificationService.checkExpiryNotifications(user.getEmail());

        verify(notificationRepo, never()).saveAndFlush(any(Notification.class));
        verifyNoInteractions(notificationService);
        assertEquals("FAILED", existing.getEmailDeliveryStatus());
    }

    @Test
    void persistsProviderFailureAndLogsStatusInsteadOfClaimingDelivery() {
        User user = createUser(true);
        InventoryItem item = createItem(user, LocalDate.now().plusDays(1));
        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findAllByUser_Id(user.getId()))
                .thenReturn(List.of(item));
        when(notificationRepo.findByEventKey(anyString()))
                .thenReturn(Optional.empty());
        doThrow(new MailException("mail unavailable") {})
                .when(notificationService)
                .sendEmail(eq(user.getEmail()), anyString());

        expiryNotificationService.checkExpiryNotifications(user.getEmail());

        verify(notificationRepo).save(argThat(notification ->
                "FAILED".equals(notification.getEmailDeliveryStatus())
        ));
    }

    private User createUser(boolean notificationsEnabled) {
        User user = new User();
        user.setId(7L);
        user.setEmail("user@example.com");
        user.setPhoneNum("+15551234567");
        user.setNotificationsEnabled(notificationsEnabled);
        user.setExpiryRemainderDays(7);
        return user;
    }

    private InventoryItem createItem(User user, LocalDate expiryDate) {
        InventoryItem item = new InventoryItem();
        item.setId(12L);
        item.setItemName("Milk");
        item.setUser(user);
        item.setExpiryDate(expiryDate);
        return item;
    }
}
