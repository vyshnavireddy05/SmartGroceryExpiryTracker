package com.project.groceryTracker.Service;

import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RecipeServiceTest {

    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(
                mock(InventoryRepo.class),
                mock(UserRepo.class)
        );
    }

    @Test
    void calculatesExpiryStatusesUsingCalendarDays() {
        LocalDate today = LocalDate.of(2026, 10, 29);

        assertEquals("EXPIRED", recipeService.calculateStatus(today.minusDays(1), today));
        assertEquals("EXPIRED", recipeService.calculateStatus(today, today));
        assertEquals("EXPIRING", recipeService.calculateStatus(today.plusDays(1), today));
        assertEquals("EXPIRING", recipeService.calculateStatus(today.plusDays(7), today));
        assertEquals("FRESH", recipeService.calculateStatus(today.plusDays(8), today));
    }

    @Test
    void returnsUnknownForMissingExpiryDate() {
        assertEquals("UNKNOWN", recipeService.calculateStatus(null, LocalDate.now()));
    }
}
