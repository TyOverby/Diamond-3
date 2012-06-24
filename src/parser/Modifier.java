package parser;

public enum Modifier {
    PRIVATE(false, true, true), STATIC(false, true, true);

    private final boolean modifiesTypes;

    private final boolean modifiesMethods;

    private final boolean modifiesVariables;

    private Modifier(boolean modifiesTypes, boolean modifiesMethods, boolean modifiesVariables) {
        this.modifiesTypes = modifiesTypes;
        this.modifiesMethods = modifiesMethods;
        this.modifiesVariables = modifiesVariables;
    }

    public boolean modifiesTypes() {
        return modifiesTypes;
    }

    public boolean modifiesMethods() {
        return modifiesMethods;
    }

    public boolean modifiesVariables() {
        return modifiesVariables;
    }
}
