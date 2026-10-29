package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientDTO {

    private String itemName;

    private double quantity;

    private String unit;
}