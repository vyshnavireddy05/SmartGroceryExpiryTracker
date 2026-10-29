package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRecommendationDTO {

    private List<RecipeDTO> canMakeNow;

    private List<RecipeDTO> needsAdditionalIngredients;
}