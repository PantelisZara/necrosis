package engine.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.core.CurrentGameState;
import engine.model.Item;

import java.util.ArrayList;
import java.util.List;

public class CombineCommand implements InterfaceCommand, ConfigurableCommand {

    private static final Gson GSON = new Gson();

    private CombineConfig config = new CombineConfig();

    @Override
    public void configure(JsonObject configJson) {
        if (configJson == null) {
            return;
        }

        CombineConfig parsedConfig = GSON.fromJson(configJson, CombineConfig.class);
        if (parsedConfig != null) {
            config = parsedConfig;
        }
    }

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.size() < 3) {
            System.out.println(config.getUsageMessage());
            return;
        }

        int withIndex = args.indexOf("with");

        if (withIndex == -1 || withIndex == 0 || withIndex == args.size() - 1) {
            System.out.println(config.getUsageMessage());
            return;
        }

        String firstItemName = String.join(" ", args.subList(0, withIndex)).toLowerCase();
        String secondItemName = String.join(" ", args.subList(withIndex + 1, args.size())).toLowerCase();

        RecipeConfig recipe = findMatchingRecipe(gameState, firstItemName, secondItemName);

        if (recipe == null) {
            System.out.println(config.getNoRecipeMessage());
            return;
        }

        List<Item> ingredients = new ArrayList<>();
        for (String ingredientId : recipe.getIngredientIds()) {
            Item ingredient = gameState.getPlayer().findItemById(ingredientId);

            if (ingredient == null) {
                System.out.println(recipe.getMissingIngredientsMessage());
                return;
            }

            ingredients.add(ingredient);
        }

        Item result = gameState.createItemById(recipe.getResultItemId());
        if (result == null) {
            System.out.println(recipe.getMissingResultMessage());
            return;
        }

        for (Item ingredient : ingredients) {
            gameState.getPlayer().removeItem(ingredient);
        }

        gameState.getPlayer().addItem(result);

        if (recipe.getSuccessMessages().isEmpty()) {
            System.out.println("You created: " + result.getName() + ".");
            return;
        }

        for (String message : recipe.getSuccessMessages()) {
            System.out.println(message);
        }
    }

    private RecipeConfig findMatchingRecipe(CurrentGameState gameState, String firstItemName, String secondItemName) {
        for (RecipeConfig recipe : config.getRecipes()) {
            List<String> ingredientIds = recipe.getIngredientIds();

            if (ingredientIds.size() != 2) {
                continue;
            }

            String firstId = ingredientIds.get(0);
            String secondId = ingredientIds.get(1);

            boolean sameOrder = matchesItem(gameState, firstItemName, firstId)
                    && matchesItem(gameState, secondItemName, secondId);
            boolean reversedOrder = matchesItem(gameState, firstItemName, secondId)
                    && matchesItem(gameState, secondItemName, firstId);

            if (sameOrder || reversedOrder) {
                return recipe;
            }
        }

        return null;
    }

    private boolean matchesItem(CurrentGameState gameState, String input, String itemId) {
        if (itemId == null) {
            return false;
        }

        String normalizedInput = input.toLowerCase();
        String normalizedId = itemId.toLowerCase();
        String readableId = normalizedId.replace("_", " ");

        if (normalizedInput.equals(normalizedId) || normalizedInput.equals(readableId)) {
            return true;
        }

        Item template = gameState.createItemById(itemId);
        return template != null && normalizedInput.equals(template.getName().toLowerCase());
    }

    private static class CombineConfig {
        private String usageMessage;
        private String noRecipeMessage;
        private List<RecipeConfig> recipes;

        private String getUsageMessage() {
            return usageMessage != null ? usageMessage : "Usage: combine <item1> with <item2>";
        }

        private String getNoRecipeMessage() {
            return noRecipeMessage != null ? noRecipeMessage : "Those items cannot be combined.";
        }

        private List<RecipeConfig> getRecipes() {
            if (recipes == null) {
                recipes = new ArrayList<>();
            }

            return recipes;
        }
    }

    private static class RecipeConfig {
        private List<String> ingredientIds;
        private String resultItemId;
        private String missingIngredientsMessage;
        private String missingResultMessage;
        private List<String> successMessages;

        private List<String> getIngredientIds() {
            if (ingredientIds == null) {
                ingredientIds = new ArrayList<>();
            }

            return ingredientIds;
        }

        private String getResultItemId() {
            return resultItemId;
        }

        private String getMissingIngredientsMessage() {
            return missingIngredientsMessage != null ? missingIngredientsMessage : "You do not have the required items.";
        }

        private String getMissingResultMessage() {
            return missingResultMessage != null ? missingResultMessage : "The crafted item is missing from the game data.";
        }

        private List<String> getSuccessMessages() {
            if (successMessages == null) {
                successMessages = new ArrayList<>();
            }

            return successMessages;
        }
    }
}
