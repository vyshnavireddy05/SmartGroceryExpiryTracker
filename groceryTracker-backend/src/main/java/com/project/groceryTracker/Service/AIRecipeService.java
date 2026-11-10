package com.project.groceryTracker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.project.groceryTracker.DTO.RecipeInventoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AIRecipeService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RecipeService recipeService;

    private final ObjectMapper objectMapper = createObjectMapper();

    public AIRecipeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    private static ObjectMapper createObjectMapper() {
        SimpleModule javaTimeModule = new SimpleModule();
        javaTimeModule.addSerializer(LocalDate.class, new JsonSerializer<>() {
            @Override
            public void serialize(
                    LocalDate value,
                    JsonGenerator generator,
                    SerializerProvider serializers) throws IOException {
                generator.writeString(value.toString());
            }
        });

        return new ObjectMapper().registerModule(javaTimeModule);
    }

    public JsonNode getRecipeRecommendations(String email) {
        List<RecipeInventoryDTO> inventory =
                recipeService.getRecipeInventory(email);

        String inventoryText;
        try {
            inventoryText = objectMapper.writeValueAsString(inventory);
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to prepare inventory for recipe recommendations",
                    exception
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Recipe recommendations are not configured"
            );
        }

        // ---------------------------------------------------------
        // 3. Create recipe recommendation prompt
        // ---------------------------------------------------------

        String prompt = buildPrompt(inventoryText);

        // ---------------------------------------------------------
        // 4. Create OpenRouter request
        // ---------------------------------------------------------

        try {

            Map<String, Object> requestBodyMap = Map.of(
                    "model", "openrouter/free",
                    "max_tokens", 2500,
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
            headers.set("HTTP-Referer", "http://localhost:5173");
            headers.set("X-Title", "Smart Grocery Expiry Tracker");

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

            String aiJson = extractAssistantContent(root);

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

            try {
                JsonNode recipeData = objectMapper.readTree(aiJson);
                if (!recipeData.isObject()
                        || !isValidRecipeList(recipeData.path("canMakeNow"))
                        || !isValidRecipeList(
                                recipeData.path("needsAdditionalIngredients"))) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_GATEWAY,
                            "Recipe provider returned incomplete recipe data"
                    );
                }
                if (!inventory.isEmpty()
                        && recipeData.path("canMakeNow").isEmpty()
                        && recipeData.path("needsAdditionalIngredients").isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_GATEWAY,
                            "Recipe provider could not generate suggestions from the available inventory"
                    );
                }
                return recipeData;
            } catch (JsonProcessingException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "OpenRouter returned incomplete or invalid recipe JSON. Please retry.",
                        exception
                );
            }
        } catch (HttpStatusCodeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    providerFailureMessage(
                            exception.getStatusCode().value(),
                            exception.getResponseBodyAsString()
                    ),
                    exception
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Recipe provider is temporarily unavailable",
                    exception
            );
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Recipe provider returned an invalid response",
                    exception
            );
        }
    }

    String extractAssistantContent(JsonNode root) {
        JsonNode providerError = root.path("error");
        if (!providerError.isMissingNode() && !providerError.isNull()) {
            String message = providerError.path("message").asText();
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    message.isBlank()
                            ? "OpenRouter returned an error with the recipe response."
                            : "OpenRouter error: "
                                    + message.substring(0, Math.min(message.length(), 240))
            );
        }

        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenRouter returned no recipe response. Check the API key's model access and credits."
            );
        }

        JsonNode choice = choices.get(0);
        JsonNode message = choice.path("message");
        JsonNode content = message.path("content");
        if (content.isTextual() && !content.asText().isBlank()) {
            return content.asText();
        }

        if (content.isArray()) {
            StringBuilder text = new StringBuilder();
            for (JsonNode part : content) {
                if (part.path("type").asText().equals("text")
                        && part.path("text").isTextual()) {
                    if (!text.isEmpty()) {
                        text.append('\n');
                    }
                    text.append(part.path("text").asText());
                }
            }
            if (!text.isEmpty()) {
                return text.toString();
            }
        }

        String refusal = message.path("refusal").asText();
        if (!refusal.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenRouter could not generate recipe suggestions: "
                            + refusal.substring(0, Math.min(refusal.length(), 240))
            );
        }

        String finishReason = choice.path("finish_reason").asText();
        if (finishReason.equals("length")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenRouter stopped before returning the recipes. Please retry."
            );
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "OpenRouter returned no recipe text"
                        + (finishReason.isBlank() ? "." : " (finish reason: " + finishReason + ").")
        );
    }

    String providerFailureMessage(int status, String responseBody) {
        if (status == 401 || status == 403) {
            return "OpenRouter rejected the API key. Check OPENROUTER_API_KEY in the backend run configuration and restart the backend.";
        }
        if (status == 402) {
            return "OpenRouter reports that the account or API key has reached its credit limit.";
        }
        if (status == 429) {
            return "OpenRouter is rate-limiting recipe requests. Wait a moment and try again.";
        }
        if (status >= 500) {
            return "OpenRouter is temporarily unavailable. Please try again later.";
        }

        if (responseBody != null && !responseBody.isBlank()) {
            try {
                JsonNode body = objectMapper.readTree(responseBody);
                String message = body.path("error").path("message").asText();
                if (message.isBlank()) {
                    message = body.path("message").asText();
                }
                if (!message.isBlank()) {
                    return "OpenRouter rejected the recipe request: "
                            + message.substring(0, Math.min(message.length(), 240));
                }
            } catch (JsonProcessingException ignored) {
                // Fall through to the generic message for non-JSON provider errors.
            }
        }

        return "OpenRouter rejected the recipe request (HTTP " + status + ").";
    }

    String buildPrompt(String inventoryJson) {
        return """
                You are an intelligent recipe recommendation assistant
                for a Smart Grocery Expiry Tracker.

                Your job is to recommend REALISTIC and COMMON recipes
                using the user's grocery inventory.

                USER INVENTORY (a JSON array; use these exact available items):
                %s

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
                    no meaningful recipe can be prepared from it alone,
                    leave "canMakeNow" empty and suggest meaningful
                    recipes under "needsAdditionalIngredients" that
                    use it and list the missing ingredients.

                16. Keep the total number of recipes between 3 and 5
                    when enough meaningful recipes are possible.
                    If the inventory is not empty, do not return both
                    recipe lists empty; suggest realistic recipes that
                    use available ingredients and list what's missing.

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
                """.formatted(inventoryJson);
    }

    private boolean isValidRecipeList(JsonNode recipes) {
        if (!recipes.isArray()) {
            return false;
        }

        for (JsonNode recipe : recipes) {
            if (recipe.path("name").asText().isBlank()
                    || !recipe.path("ingredients").isArray()
                    || !recipe.path("preparationSteps").isArray()) {
                return false;
            }
        }

        return true;
    }
}
