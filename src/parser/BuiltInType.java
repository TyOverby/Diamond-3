package parser;

enum BuiltInType implements ExpressionType {
    BOOLEAN(true, false), SHORT(true, true), INT(true, true), LONG(true, true), VOID(true, false), STRING(false, false), INDETERMINATE(false, false) {
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
    public boolean isArray() {
        return false;
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
    public String toString() {
        return name().toLowerCase();
    }
}
