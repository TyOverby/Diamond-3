package parser;

import static com.google.common.base.Preconditions.*;

public final class DeleteStatement extends Statement {
    private final IdentifierReference variable;

    public DeleteStatement(Statement parent, IdentifierReference variable) throws ParseException {
        super(parent);
        checkNotNull(variable);
        this.variable = variable;
    }
}
