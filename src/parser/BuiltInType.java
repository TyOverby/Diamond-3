package parser;

enum BuiltInType implements ExpressionType {
    BOOLEAN(true, false), SHORT(true, true), INT(true, true), LONG(true, true), VOID(true, false), STRING(false, false), INDETERMINATE(false, false) {
        @Override
        public int getNumberOfLexemes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "<indeterminate>";
        }
    };

    private final boolean primitive;

    private final boolean integral;

    private BuiltInType(boolean primitive, boolean integral) {
        this.primitive = primitive;
        this.integral = integral;
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public boolean isIntegral() {
        return integral;
    }

    @Override
    public boolean isNumeric() {
        return integral; // for now, these are the same
    }

    @Override
    public int getNumberOfLexemes() {
        return 1;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
