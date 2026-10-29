package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {

    private String name;

    private List<RecipeIngredientDTO> ingredients;

    private List<RecipeIngredientDTO> missingIngredients;

    private List<String> preparationSteps;

    private List<String> safetyMeasures;
}