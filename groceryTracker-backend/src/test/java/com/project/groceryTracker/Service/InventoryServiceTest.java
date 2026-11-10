package com.project.groceryTracker.Service;

import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.NotificationRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private NotificationRepo notificationRepo;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryRepo = mock(InventoryRepo.class);
        userRepo = mock(UserRepo.class);
        notificationRepo = mock(NotificationRepo.class);
        inventoryService = new InventoryService(
                inventoryRepo,
                userRepo,
                notificationRepo,
                mock(RecipeService.class)
        );
    }

    @Test
    void looksUpItemsOnlyWithinTheAuthenticatedUsersInventory() {
        User user = new User();
        user.setId(9L);
        user.setEmail("owner@example.com");
        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findByIdAndUser_Id(18L, user.getId()))
                .thenReturn(Optional.empty());

        assertNull(inventoryService.getItem(18L, user.getEmail()));

        verify(inventoryRepo).findByIdAndUser_Id(18L, 9L);
        verify(inventoryRepo, never()).findById(18L);
    }

    @Test
    void decrementingToZeroClearsNotificationReferencesThenDeletesTheItem() {
        User user = ownedUser();
        InventoryItem item = ownedItem(user, 1);

        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findByIdAndUser_Id(item.getId(), user.getId()))
                .thenReturn(Optional.of(item));

        assertNull(inventoryService.updateQuantity(item.getId(), 0, user.getEmail()));

        verify(notificationRepo).clearInventoryItemReference(item.getId());
        verify(inventoryRepo).delete(item);
        verify(inventoryRepo, never()).save(item);
    }

    @Test
    void deletingAnItemClearsNotificationReferencesWithoutDeletingNotifications() {
        User user = ownedUser();
        InventoryItem item = ownedItem(user, 1);

        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findByIdAndUser_Id(item.getId(), user.getId()))
                .thenReturn(Optional.of(item));

        assertTrue(inventoryService.deleteInventoryItem(item.getId(), user.getEmail()));

        verify(notificationRepo).clearInventoryItemReference(item.getId());
        verify(inventoryRepo).delete(item);
        verify(notificationRepo, never()).deleteAll();
        verify(notificationRepo, never()).deleteById(any());
    }

    @Test
    void doesNotDeleteAnotherUsersInventoryItem() {
        User user = ownedUser();
        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findByIdAndUser_Id(99L, user.getId()))
                .thenReturn(Optional.empty());

        assertFalse(inventoryService.deleteInventoryItem(99L, user.getEmail()));

        verify(inventoryRepo, never()).delete(any());
        verify(notificationRepo, never()).clearInventoryItemReference(any());
    }

    @Test
    void updateQuantityDecrementsWithoutDeletingWhenQuantityRemainsPositive() {
        User user = ownedUser();
        InventoryItem item = ownedItem(user, 5);

        when(userRepo.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(inventoryRepo.findByIdAndUser_Id(item.getId(), user.getId()))
                .thenReturn(Optional.of(item));
        when(inventoryRepo.save(item)).thenReturn(item);

        InventoryItem updated = inventoryService.updateQuantity(
                item.getId(),
                4,
                user.getEmail()
        );

        org.junit.jupiter.api.Assertions.assertEquals(4, updated.getQuantity());
        verify(inventoryRepo).save(item);
        verify(inventoryRepo, never()).delete(item);
        verify(notificationRepo, never()).clearInventoryItemReference(any());
    }

    private static User ownedUser() {
        User user = new User();
        user.setId(9L);
        user.setEmail("owner@example.com");
        return user;
    }

    private static InventoryItem ownedItem(User user, int quantity) {
        InventoryItem item = new InventoryItem();
        item.setId(18L);
        item.setQuantity(quantity);
        item.setUser(user);
        return item;
    }
}
