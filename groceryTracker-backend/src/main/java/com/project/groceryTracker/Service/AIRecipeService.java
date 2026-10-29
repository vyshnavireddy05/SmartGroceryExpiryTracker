package com.project.groceryTracker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.groceryTracker.DTO.RecipeInventoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIRecipeService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Autowired
    private RecipeService recipeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getRecipeRecommendations(String email) {


        List<RecipeInventoryDTO> inventory =
                recipeService.getRecipeInventory(email);


        StringBuilder inventoryText = new StringBuilder();

        for (RecipeInventoryDTO item : inventory) {

            inventoryText.append("""
                    {
                      "itemName": "%s",
                      "quantity": %s,
                      "unit": "%s",
                      "category": "%s",
                      "status": "%s",
                      "expiryDate": "%s"
                    },
                    """.formatted(
                    item.getItemName(),
                    item.getQuantity(),
                    item.getUnit(),
                    item.getCategory(),
                    item.getStatus(),
                    item.getExpiryDate()
            ));
        }

        // Remove the final comma
        if (inventoryText.length() > 0) {
            inventoryText.deleteCharAt(inventoryText.length() - 1);
        }

        // ---------------------------------------------------------
        // 3. Create recipe recommendation prompt
        // ---------------------------------------------------------

        String prompt = """
                You are an intelligent recipe recommendation assistant
                for a Smart Grocery Expiry Tracker.

                Your job is to recommend REALISTIC and COMMON recipes
                using the user's grocery inventory.

                USER INVENTORY:

                [
                %s
                ]

                IMPORTANT RULES:

                1. NEVER use EXPIRED ingredients.

                2. Prioritize ingredients whose status is EXPIRING.

                3. First generate recipes that can be made using ONLY
                   the ingredients available in the inventory.

                4. A recipe belongs in "canMakeNow" ONLY when every
                   required ingredient is available in sufficient quantity.

                5. If an ingredient exists in the inventory but its
                   quantity is insufficient, put the recipe in
                   "needsAdditionalIngredients".

                6. If an ingredient does not exist in the inventory,
                   put the required amount in "missingIngredients".

                7. Do NOT invent available inventory items.

                8. Do NOT assume that basic ingredients such as oil,
                   salt, sugar, spices, onion, tomato, milk, butter,
                   cheese or vegetables are available unless they are
                   explicitly present in the inventory.

                9. Use the EXACT item name from the inventory when
                   referring to an available ingredient.

                10. The quantity of an available ingredient in
                    "ingredients" must not exceed the inventory quantity.

                11. For insufficient quantities, "missingIngredients"
                    must contain ONLY the ADDITIONAL quantity required.

                12. Recommend actual recipes people commonly prepare.
                    Do NOT create artificial recipes simply to produce
                    more results.

                13. Do NOT consider eating a packaged snack by itself
                    as a recipe.

                14. Do NOT create strange combinations just because
                    the ingredients technically exist.

                15. If the inventory contains only one ingredient and
                    no meaningful recipe can be prepared, return an
                    empty "canMakeNow" list rather than inventing a
                    recipe.

                16. Keep the total number of recipes between 3 and 5
                    when enough meaningful recipes are possible.

                17. For each recipe provide a short imageSearchTerm.
                    This must be a simple food name such as
                    "vegetable sandwich" or "banana smoothie".
                    Do not provide fake URLs.

                18. Preparation steps should be practical and ordered.

                19. Safety measures should be relevant to the recipe
                    and ingredients.

                RETURN ONLY VALID JSON.

                DO NOT use Markdown.

                DO NOT use ```json.

                DO NOT add any explanation before or after the JSON.

                USE EXACTLY THIS STRUCTURE:

                {
                  "canMakeNow": [
                    {
                      "name": "Recipe Name",
                      "imageSearchTerm": "food image search term",
                      "ingredients": [
                        {
                          "itemName": "inventory item",
                          "quantity": 1,
                          "unit": "unit"
                        }
                      ],
                      "preparationSteps": [
                        "Step 1",
                        "Step 2"
                      ],
                      "safetyMeasures": [
                        "Safety measure 1",
                        "Safety measure 2"
                      ]
                    }
                  ],
                  "needsAdditionalIngredients": [
                    {
                      "name": "Recipe Name",
                      "imageSearchTerm": "food image search term",
                      "ingredients": [
                        {
                          "itemName": "inventory item",
                          "quantity": 1,
                          "unit": "unit"
                        }
                      ],
                      "missingIngredients": [
                        {
                          "itemName": "additional ingredient",
                          "quantity": 1,
                          "unit": "unit"
                        }
                      ],
                      "preparationSteps": [
                        "Step 1",
                        "Step 2"
                      ],
                      "safetyMeasures": [
                        "Safety measure 1",
                        "Safety measure 2"
                      ]
                    }
                  ]
                }
                """.formatted(inventoryText);

        // ---------------------------------------------------------
        // 4. Create OpenRouter request
        // ---------------------------------------------------------

        try {

            Map<String, Object> requestBodyMap = Map.of(
                    "model", "openrouter/free",
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content",
                                    "You are a practical recipe recommendation assistant. " +
                                            "Always follow the user's inventory exactly."
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            String requestBody =
                    objectMapper.writeValueAsString(requestBodyMap);

            // -----------------------------------------------------
            // 5. OpenRouter URL
            // -----------------------------------------------------

            String url =
                    "https://openrouter.ai/api/v1/chat/completions";

            // -----------------------------------------------------
            // 6. Headers
            // -----------------------------------------------------

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request =
                    new HttpEntity<>(requestBody, headers);

            // -----------------------------------------------------
            // 7. Send request
            // -----------------------------------------------------

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            // -----------------------------------------------------
            // 8. Extract only AI message content
            // -----------------------------------------------------

            JsonNode root =
                    objectMapper.readTree(response.getBody());

            JsonNode content =
                    root.path("choices")
                            .get(0)
                            .path("message")
                            .path("content");

            if (content.isMissingNode()) {
                System.out.println("AI response did not contain content.");
                return "{\"canMakeNow\":[],\"needsAdditionalIngredients\":[]}";
            }

            String aiJson = content.asText();

            // -----------------------------------------------------
            // 9. Clean accidental Markdown if model adds it
            // -----------------------------------------------------

            aiJson = aiJson.trim();

            if (aiJson.startsWith("```json")) {
                aiJson = aiJson.substring(7);
            }

            if (aiJson.startsWith("```")) {
                aiJson = aiJson.substring(3);
            }

            if (aiJson.endsWith("```")) {
                aiJson = aiJson.substring(
                        0,
                        aiJson.length() - 3
                );
            }

            aiJson = aiJson.trim();

            // -----------------------------------------------------
            // 10. Validate JSON
            // -----------------------------------------------------

            objectMapper.readTree(aiJson);

            System.out.println("AI Recipe JSON:");
            System.out.println(aiJson);

            return aiJson;

        } catch (HttpServerErrorException.ServiceUnavailable e) {

            System.out.println(
                    "OpenRouter service is temporarily unavailable."
            );

            return """
                    {
                      "canMakeNow": [],
                      "needsAdditionalIngredients": [],
                      "error": "AI service is temporarily unavailable."
                    }
                    """;

        } catch (HttpClientErrorException e) {

            System.out.println(
                    "Status: " + e.getStatusCode()
            );

            System.out.println(
                    "Response: " + e.getResponseBodyAsString()
            );

            return """
                    {
                      "canMakeNow": [],
                      "needsAdditionalIngredients": [],
                      "error": "AI API request failed."
                    }
                    """;

        } catch (Exception e) {

            e.printStackTrace();

            return """
                    {
                      "canMakeNow": [],
                      "needsAdditionalIngredients": [],
                      "error": "Unexpected error occurred."
                    }
                    """;
        }
    }
}
