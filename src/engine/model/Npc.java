package engine.model;

import java.util.List;

public class Npc {

    private final String id;
    private final String name;
    private final String description;
    private final List<DialogueEntry> dialogues;

    public Npc(String id, String name, String description, List<DialogueEntry> dialogues) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dialogues = dialogues;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<DialogueEntry> getDialogues() {
        return dialogues;
    }
}