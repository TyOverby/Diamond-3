package parser;

import static com.google.common.base.Preconditions.*;

final class ArrayType implements ExpressionType {
    private final ExpressionType elementType;

    public ArrayType(ExpressionType elementType) {
        checkNotNull(elementType);
        checkArgument(elementType != BuiltInType.VOID);
        checkArgument(elementType != BuiltInType.INDETERMINATE);
        this.elementType = elementType;
    }

    public ExpressionType getElementType() {
        return elementType;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isIntegral() {
        return false;
    }

    @Override
    public int getNumberOfLexemes() {
        return elementType.getNumberOfLexemes() + 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayType arrayType = (ArrayType) o;

        if (!elementType.equals(arrayType.elementType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return elementType.hashCode();
    }

    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}
