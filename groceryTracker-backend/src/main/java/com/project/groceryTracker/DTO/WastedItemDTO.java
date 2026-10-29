package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WastedItemDTO {
    private Long id;
    private String itemName;
    private String category;
    private Integer quantity;
    private double wastedValue;
}
