
package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.InventoryDTO;
import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getInventoryItems(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                inventoryService.getInventoryItems(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        InventoryItem item = inventoryService.getItem(
                id,
                userDetails.getUsername()
        );

        return item != null
                ? ResponseEntity.ok(item)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/addItem")
    public ResponseEntity<InventoryItem> addInventoryItems(
            @RequestBody InventoryItem inventoryItem,
            @AuthenticationPrincipal UserDetails userDetails) {

        InventoryItem savedItem = inventoryService.addInventory(
                inventoryItem,
                userDetails.getUsername()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateInventoryItems(
            @RequestBody InventoryItem inventoryItem,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        InventoryItem updatedItem = inventoryService.updateInventory(
                id,
                inventoryItem,
                userDetails.getUsername()
        );

        return updatedItem != null
                ? ResponseEntity.ok(updatedItem)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<InventoryItem> updateQuantity(
            @RequestBody InventoryItem inventoryItem,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (inventoryItem.getQuantity() == null
                || inventoryItem.getQuantity() <= 0) {

            // A quantity <= 0 removes the item, as in the original logic.
            // A null quantity is invalid input.
            if (inventoryItem.getQuantity() == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        InventoryItem updatedItem = inventoryService.updateQuantity(
                id,
                inventoryItem.getQuantity(),
                userDetails.getUsername()
        );

        if (updatedItem == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/deleteItem/{id}")
    public ResponseEntity<Void> deleteInventoryItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean deleted = inventoryService.deleteInventoryItem(
                id,
                userDetails.getUsername()
        );

        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}