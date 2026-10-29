package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.RecipeInventoryDTO;
import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class RecipeService {

    private final InventoryRepo inventoryRepo;
    private final UserRepo userRepo;

    public RecipeService(
            InventoryRepo inventoryRepo,
            UserRepo userRepo) {
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
    }

    public String calculateStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return "UNKNOWN";
        }

        long daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(), expiryDate);

        if (daysLeft <= 0) {
            return "EXPIRED";
        } else if (daysLeft <= 7) {
            return "EXPIRING";
        } else {
            return "FRESH";
        }
    }

    public List<RecipeInventoryDTO> getRecipeInventory(String email) {

        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));

        List<InventoryItem> items =
                inventoryRepo.findAllByUser_Id(user.getId());

        return items.stream()
                .map(item -> new RecipeInventoryDTO(
                        item.getId(),
                        item.getItemName(),
                        item.getCategory(),
                        item.getQuantity(),
                        item.getUnit(),
                        item.getExpiryDate(),
                        calculateStatus(item.getExpiryDate())
                ))
                .filter(item ->
                        "FRESH".equals(item.getStatus()) ||
                                "EXPIRING".equals(item.getStatus()))
                .sorted(Comparator.comparingInt(item ->
                        "EXPIRING".equals(item.getStatus()) ? 0 : 1))
                .toList();
    }
}
