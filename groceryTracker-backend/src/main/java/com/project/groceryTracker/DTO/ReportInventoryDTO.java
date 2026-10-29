package com.project.groceryTracker.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReportInventoryDTO {
    private Long id;
    private String itemName;
    private String category;
    private Integer price;
    private Integer quantity;
    private LocalDate purchasedDate;
    private LocalDate expiryDate;
    private String storageLocation;
    private String notes;
    private String status;
}
