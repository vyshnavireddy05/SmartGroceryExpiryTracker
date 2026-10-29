package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.RecipeInventoryDTO;
import com.project.groceryTracker.Service.AIRecipeService;
import com.project.groceryTracker.Service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final AIRecipeService aiRecipeService;

    public RecipeController(
            RecipeService recipeService,
            AIRecipeService aiRecipeService) {
        this.recipeService = recipeService;
        this.aiRecipeService = aiRecipeService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<RecipeInventoryDTO>> getInventoryItems(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                recipeService.getRecipeInventory(
                        userDetails.getUsername()));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<String> getRecipeRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                aiRecipeService.getRecipeRecommendations(
                        userDetails.getUsername()));
    }
}