package engine.model;

public class DialogueEntry {

    private final String requiredFlag;
    private final String forbiddenFlag;
    private final String text;

    public DialogueEntry(String requiredFlag, String forbiddenFlag, String text) {
        this.requiredFlag = requiredFlag;
        this.forbiddenFlag = forbiddenFlag;
        this.text = text;
    }

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public String getForbiddenFlag() {
        return forbiddenFlag;
    }

    public String getText() {
        return text;
    }
}