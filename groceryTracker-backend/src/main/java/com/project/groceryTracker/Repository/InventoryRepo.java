package com.project.groceryTracker.Repository;

import com.project.groceryTracker.Entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<InventoryItem,Long> {
    List<InventoryItem> findAllByUser_Id(Long userId);

    Optional<InventoryItem> findByIdAndUser_Id(
            Long id,
            Long userId
    );
    Optional<InventoryItem> findByItemNameAndUnitAndExpiryDateAndUser_Id(
            String itemName,
            String unit,
            LocalDate expriryDate,
            Long userId
    );
}
