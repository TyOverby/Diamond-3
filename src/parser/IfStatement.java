package parser;

import static com.google.common.base.Preconditions.*;

public final class IfStatement extends Statement {
    private final Expression condition;

    public IfStatement(Statement parent, Expression condition) throws ParseException {
        super(parent);
        checkNotNull(condition);
        checkArgument(condition.getType() == BuiltInType.BOOLEAN || condition.getType() == BuiltInType.INDETERMINATE);
        this.condition = condition;
        this.condition.attach(this);
    }
}
