package parser;

import static com.google.common.base.Preconditions.*;

public final class ForLoop extends WhileLoop {
    private final Expression initializer;

    private final Expression updater;

    public ForLoop(Statement parent, Expression initializer, Expression condition, Expression updater) throws ParseException {
        super(parent, condition);
        checkNotNull(initializer);
        checkNotNull(updater);
        this.initializer = initializer;
        this.updater = updater;
    }
}
