package parser;

import static com.google.common.base.Preconditions.*;

public final class ArrayAccess extends Expression {
    private final Expression array;

    private final Expression index;

    public ArrayAccess(Expression array, Expression index) {
        super((array.getType() == BuiltInType.INDETERMINATE) ? BuiltInType.INDETERMINATE : ((ArrayType) array.getType()).getElementType());
        checkNotNull(array);
        checkNotNull(index);
        checkArgument(index.getType().isIntegral() || index.getType() == BuiltInType.INDETERMINATE);
        this.array = array;
        this.array.attach(this);
        this.index = index;
        this.index.attach(this);
    }
}
