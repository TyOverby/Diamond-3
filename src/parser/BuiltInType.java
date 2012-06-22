package parser;

enum BuiltInType implements ExpressionType {
    BOOLEAN(true), SHORT(true), INT(true), LONG(true), VOID(true), STRING(false), INDETERMINATE(false) {
        @Override
        public String toString() {
            return "<indeterminate>";
        }
    };

    private final boolean primitive;

    private BuiltInType(boolean primitive) {
        this.primitive = primitive;
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
    public String toString() {
        return name().toLowerCase();
    }
}
