package com.project.groceryTracker.Repository;

import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.Notification;
import com.project.groceryTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.inventoryItem = NULL WHERE n.inventoryItem.id = :inventoryId")
    int clearInventoryItemReference(@Param("inventoryId") Long inventoryId);
}