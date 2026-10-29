
package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.InventoryDTO;
import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepo inventoryRepo;
    private final UserRepo userRepo;
    private final RecipeService recipeService;

    public InventoryService(
            InventoryRepo inventoryRepo,
            UserRepo userRepo,
            RecipeService recipeService) {
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
        this.recipeService = recipeService;
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));
    }

    @Transactional
    public InventoryItem addInventory(
            InventoryItem inventoryItem,
            String email) {

        User user = getUserByEmail(email);

        var existingItem =
                inventoryRepo.findByItemNameAndUnitAndExpiryDateAndUser_Id(
                        inventoryItem.getItemName(),
                        inventoryItem.getUnit(),
                        inventoryItem.getExpiryDate(),
                        user.getId()
                );

        if (existingItem.isPresent()) {
            InventoryItem item = existingItem.get();

            item.setQuantity(
                    item.getQuantity() + inventoryItem.getQuantity()
            );

            return inventoryRepo.save(item);
        }

        // Always assign ownership on the server.
        inventoryItem.setId(null);
        inventoryItem.setUser(user);

        return inventoryRepo.save(inventoryItem);
    }

    public List<InventoryDTO> getInventoryItems(String email) {

        User user = getUserByEmail(email);

        List<InventoryItem> items =
                inventoryRepo.findAllByUser_Id(user.getId());

        return items.stream()
                .map(item -> new InventoryDTO(
                        item.getId(),
                        item.getItemName(),
                        item.getCategory(),
                        item.getQuantity(),
                        item.getExpiryDate(),
                        recipeService.calculateStatus(item.getExpiryDate())
                ))
                .toList();
    }

    public InventoryItem getItem(Long id, String email) {

        User user = getUserByEmail(email);

        return inventoryRepo.findByIdAndUser_Id(id, user.getId())
                .orElse(null);
    }

    @Transactional
    public InventoryItem updateInventory(
            Long id,
            InventoryItem request,
            String email) {

        User user = getUserByEmail(email);

        InventoryItem existing = inventoryRepo
                .findByIdAndUser_Id(id, user.getId())
                .orElse(null);

        if (existing == null) {
            return null;
        }

        // Update permitted fields only.
        // Never accept ownership from the request body.
        existing.setItemName(request.getItemName());
        existing.setCategory(request.getCategory());
        existing.setPrice(request.getPrice());
        existing.setQuantity(request.getQuantity());
        existing.setUnit(request.getUnit());
        existing.setPurchasedDate(request.getPurchasedDate());
        existing.setExpiryDate(request.getExpiryDate());
        existing.setStorageLocation(request.getStorageLocation());
        existing.setNotes(request.getNotes());

        return inventoryRepo.save(existing);
    }

    @Transactional
    public boolean deleteInventoryItem(Long id, String email) {

        User user = getUserByEmail(email);

        InventoryItem item = inventoryRepo
                .findByIdAndUser_Id(id, user.getId())
                .orElse(null);

        if (item == null) {
            return false;
        }

        inventoryRepo.delete(item);
        return true;
    }

    @Transactional
    public InventoryItem updateQuantity(
            Long id,
            Integer quantity,
            String email) {

        User user = getUserByEmail(email);

        InventoryItem item = inventoryRepo
                .findByIdAndUser_Id(id, user.getId())
                .orElse(null);

        if (item == null) {
            return null;
        }

        if (quantity == null || quantity <= 0) {
            inventoryRepo.delete(item);
            return null;
        }

        item.setQuantity(quantity);

        return inventoryRepo.save(item);
    }
}