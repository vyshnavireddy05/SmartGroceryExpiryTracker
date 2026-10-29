package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RecipeInventoryDTO {

    private Long id;
    private String itemName;
    private String category;
    private Integer quantity;
    private String unit;
    private LocalDate expiryDate;
    private String status;
}