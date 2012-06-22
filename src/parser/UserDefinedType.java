package parser;

import static com.google.common.base.Preconditions.*;

final class UserDefinedType implements ExpressionType {
    private final String name;

    public UserDefinedType(String name) {
        checkNotNull(name);
        this.name = name;
    }

    @Override
    public boolean isArray() {
        return false;
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
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDefinedType that = (UserDefinedType) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
