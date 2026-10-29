package com.project.groceryTracker.Repository;

import com.project.groceryTracker.DTO.NotificationDTO;
import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    Optional<Notification> findByUserAndInventoryItemAndType(
            User user,
            InventoryItem inventoryItem,
            String type
    );

    Optional<Notification> findByEventKey(String eventKey);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
}