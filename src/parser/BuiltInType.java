package parser;

enum BuiltInType implements ExpressionType {
    BOOLEAN, SHORT, INT, LONG, VOID, STRING, INDETERMINATE {
        @Override
        public String toString() {
            return "<indeterminate>";
        }
    };

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
