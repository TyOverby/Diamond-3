package parser;

public final class OperatorExpression extends Expression {
    private final Expression leftOperand;

    private final Expression rightOperand;

    private final Operator operator;

    public OperatorExpression(Node parent, Expression leftOperand, Operator operator) {
        super(parent, operator.validate(leftOperand.getType(), null));
        this.leftOperand = leftOperand;
        this.rightOperand = null;
        this.operator = operator;
    }

    public OperatorExpression(Node parent, Operator operator, Expression rightOperand) {
        super(parent, operator.validate(null, rightOperand.getType()));
        this.leftOperand = null;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    public OperatorExpression(Node parent, Expression leftOperand, Expression rightOperand, Operator operator) {
        super(parent, operator.validate(leftOperand.getType(), rightOperand.getType()));
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }
}
