package parser;

import static com.google.common.base.Preconditions.*;

public final class DoLoop extends Statement {
    private final Expression condition;

    public DoLoop(Statement parent, Expression condition) throws ParseException {
        super(parent);
        checkNotNull(condition);
        checkArgument(condition.getType() == BuiltInType.BOOLEAN || condition.getType() == BuiltInType.INDETERMINATE);
        this.condition = condition;
        this.condition.attach(this);
    }
}
