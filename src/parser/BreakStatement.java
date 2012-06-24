package parser;

public final class BreakStatement extends Statement {
    public BreakStatement(Statement parent) throws ParseException {
        super(parent);
        // some ancestor of this statement must be a loop or switch statement
        Statement ancestor = parent;
        while (ancestor.getParent() != null) {
            if (ancestor instanceof DoLoop || ancestor instanceof ForLoop || ancestor instanceof RepeatLoop
                    || ancestor instanceof WhileLoop || ancestor instanceof SwitchStatement) {
                return;
            }
            ancestor = ancestor.getParent();
        }
        throw new ParseException("\"break\" can only appear within a loop or switch statement");
    }
}
