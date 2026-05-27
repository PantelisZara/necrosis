package engine.model;

public class Interactable {

    private final String id;
    private final String name;
    private final String description;
    private final String requiredItemId;
    private final String requiredFlag;
    private final String forbiddenFlag;
    private final String setsFlag;
    private final String successMessage;
    private final String failureMessage;
    private final String forbiddenMessage;

    public Interactable(String id,
                        String name,
                        String description,
                        String requiredItemId,
                        String requiredFlag,
                        String forbiddenFlag,
                        String setsFlag,
                        String successMessage,
                        String failureMessage,
                        String forbiddenMessage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredItemId = requiredItemId;
        this.requiredFlag = requiredFlag;
        this.forbiddenFlag = forbiddenFlag;
        this.setsFlag = setsFlag;
        this.successMessage = successMessage;
        this.failureMessage = failureMessage;
        this.forbiddenMessage = forbiddenMessage;
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

    public String getRequiredItemId() {
        return requiredItemId;
    }

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public String getForbiddenFlag() {
        return forbiddenFlag;
    }

    public String getSetsFlag() {
        return setsFlag;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getForbiddenMessage() {
        return forbiddenMessage;
    }
}
