package com.project.groceryTracker.Service;

import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private InventoryRepo inventoryRepo;
    private UserRepo userRepo;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryRepo = mock(InventoryRepo.class);
        userRepo = mock(UserRepo.class);
        inventoryService = new InventoryService(
                inventoryRepo,
                userRepo,
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
}
