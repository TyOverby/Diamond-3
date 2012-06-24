package parser;

import lexer.DiamondLexer.Lexeme;

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

    public static boolean isModifier(Lexeme lexeme) {
        switch (lexeme) {
            case PRIVATE:
            case STATIC:
                return true;
            default:
                return false;
        }
    }

    public static Modifier fromLexeme(Lexeme lexeme) throws ParseException {
        switch (lexeme) {
            case PRIVATE:
                return Modifier.PRIVATE;
            case STATIC:
                return Modifier.STATIC;
            default:
                throw new ParseException("expected modifier");
        }
    }
}
