package parser;

import static com.google.common.base.Preconditions.*;

public class WhileLoop extends Statement {
    private final Expression condition;

    public WhileLoop(Statement parent, Expression condition) throws ParseException {
        super(parent);
        checkNotNull(condition);
        checkArgument(condition.getType() == BuiltInType.BOOLEAN || condition.getType() == BuiltInType.INDETERMINATE);
        this.condition = condition;
    }
}
