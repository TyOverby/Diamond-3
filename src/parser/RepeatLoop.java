package parser;

import static com.google.common.base.Preconditions.*;

public final class RepeatLoop extends Statement {
    private final Expression repeatCount;

    public RepeatLoop(Statement parent, Expression repeatCount) throws ParseException {
        super(parent);
        checkNotNull(repeatCount);
        checkArgument(repeatCount.getType().isIntegral() || repeatCount.getType() == BuiltInType.INDETERMINATE);
        this.repeatCount = repeatCount;
        this.repeatCount.attach(this);
    }
}
