package parser;

import static com.google.common.base.Preconditions.*;

public final class SwitchStatement extends Statement {
    private final Expression value;

    public SwitchStatement(Statement parent, Expression value) throws ParseException {
        super(parent);
        checkNotNull(value);
        checkArgument(value.getType().isIntegral() || value.getType() == BuiltInType.INDETERMINATE);
        this.value = value;
        this.value.attach(this);
    }
}
