package parser;

public final class ContinueStatement extends Statement {
    public ContinueStatement(Statement parent) throws ParseException {
        super(parent);
        // some ancestor of this statement must be a loop
        Statement ancestor = parent;
        while (ancestor.getParent() != null) {
            if (ancestor instanceof DoLoop || ancestor instanceof ForLoop || ancestor instanceof RepeatLoop
                    || ancestor instanceof WhileLoop) {
                return;
            }
            ancestor = ancestor.getParent();
        }
        throw new ParseException("\"continue\" can only appear within a loop");
    }
}
