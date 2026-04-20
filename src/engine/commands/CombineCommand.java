package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;

import java.util.List;

public class CombineCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.size() < 3) {
            System.out.println("Usage: combine <item1> with <item2>");
            return;
        }

        int withIndex = args.indexOf("with");

        if (withIndex == -1 || withIndex == 0 || withIndex == args.size() - 1) {
            System.out.println("Usage: combine <item1> with <item2>");
            return;
        }

        String firstItemName = String.join(" ", args.subList(0, withIndex)).toLowerCase();
        String secondItemName = String.join(" ", args.subList(withIndex + 1, args.size())).toLowerCase();

        boolean isBottleAndScrap =
                (firstItemName.equals("broken bottle") && secondItemName.equals("scrap metal")) ||
                        (firstItemName.equals("scrap metal") && secondItemName.equals("broken bottle"));

        if (!isBottleAndScrap) {
            System.out.println("Those items cannot be combined.");
            return;
        }

        Item brokenBottle = gameState.getPlayer().findItemById("broken_bottle");
        Item scrapMetal = gameState.getPlayer().findItemById("scrap_metal");

        if (brokenBottle == null || scrapMetal == null) {
            System.out.println("You do not have the required items.");
            return;
        }

        gameState.getPlayer().removeItem(brokenBottle);
        gameState.getPlayer().removeItem(scrapMetal);

        Item improvisedBlade = new Item(
                "improvised_blade",
                "improvised blade",
                "A crude but deadly improvised blade made from broken glass and sharpened scrap metal.",
                true
        );

        gameState.getPlayer().addItem(improvisedBlade);

        System.out.println("You combine the broken bottle with the scrap metal.");
        System.out.println("You created: improvised blade.");
    }
}