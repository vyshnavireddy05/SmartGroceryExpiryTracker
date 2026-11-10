package com.project.groceryTracker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.groceryTracker.DTO.RecipeInventoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AIRecipeServiceTest {

    @Test
    void promptUsesInventoryAsSingleJsonArray() {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));

        String prompt = service.buildPrompt(
                "[{\"itemName\":\"Milk\",\"status\":\"FRESH\"}]"
        );

        assertTrue(prompt.contains(
                "USER INVENTORY (a JSON array; use these exact available items):\n" +
                        "[{\"itemName\":\"Milk\",\"status\":\"FRESH\"}]"
        ));
        assertFalse(prompt.contains(
                "[\n[{\"itemName\":\"Milk\",\"status\":\"FRESH\"}]\n]"
        ));
    }

    @Test
    void reportsProviderAuthenticationAndCreditFailuresClearly() {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));

        assertTrue(service.providerFailureMessage(401, "")
                .contains("rejected the API key"));
        assertTrue(service.providerFailureMessage(402, "")
                .contains("credit limit"));
        assertTrue(service.providerFailureMessage(429, "")
                .contains("rate-limiting"));
    }

    @Test
    void includesSafeProviderErrorDetailsForOtherRequestFailures() {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));

        assertEquals(
                "OpenRouter rejected the recipe request: No free model available",
                service.providerFailureMessage(
                        400,
                        "{\"error\":{\"message\":\"No free model available\"}}"
                )
        );
    }

    @Test
    void extractsTextFromOpenRouterChatCompletion() throws Exception {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));
        JsonNode response = new ObjectMapper().readTree("""
                {"choices":[{"message":{"content":"{\\"canMakeNow\\":[],\\"needsAdditionalIngredients\\":[]}"}}]}
                """);

        assertEquals(
                "{\"canMakeNow\":[],\"needsAdditionalIngredients\":[]}",
                service.extractAssistantContent(response)
        );
    }

    @Test
    void extractsTextFromOpenRouterContentBlocks() throws Exception {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));
        JsonNode response = new ObjectMapper().readTree("""
                {"choices":[{"message":{"content":[{"type":"text","text":"recipe json"}]}}]}
                """);

        assertEquals("recipe json", service.extractAssistantContent(response));
    }

    @Test
    void reportsProviderErrorsEmbeddedInSuccessfulHttpResponses() throws Exception {
        AIRecipeService service = new AIRecipeService(mock(RecipeService.class));
        JsonNode response = new ObjectMapper().readTree("""
                {"error":{"message":"No free model available"}}
                """);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.extractAssistantContent(response)
        );

        assertTrue(exception.getReason().contains("No free model available"));
    }

    @Test
    void serializesInventoryExpiryDatesBeforeCheckingProviderConfiguration() {
        RecipeService recipeService = mock(RecipeService.class);
        when(recipeService.getRecipeInventory("user@example.com"))
                .thenReturn(List.of(new RecipeInventoryDTO(
                        1L,
                        "Milk",
                        "Dairy",
                        1,
                        "L",
                        LocalDate.of(2026, 11, 5),
                        "FRESH"
                )));

        AIRecipeService service = new AIRecipeService(recipeService);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getRecipeRecommendations("user@example.com")
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }
}
