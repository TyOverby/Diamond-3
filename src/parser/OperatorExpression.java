package parser;

public final class OperatorExpression extends Expression {
    private final Expression leftOperand;

    private final Expression rightOperand;

    private final Operator operator;

    public OperatorExpression(Expression leftOperand, Operator operator) {
        super(operator.validate(leftOperand.getType(), null));
        this.leftOperand = leftOperand;
        this.leftOperand.attach(this);
        this.rightOperand = null;
        this.operator = operator;
    }

    public OperatorExpression(Operator operator, Expression rightOperand) {
        super(operator.validate(null, rightOperand.getType()));
        this.leftOperand = null;
        this.rightOperand = rightOperand;
        this.rightOperand.attach(this);
        this.operator = operator;
    }

    public OperatorExpression(Expression leftOperand, Expression rightOperand, Operator operator) {
        super(operator.validate(leftOperand.getType(), rightOperand.getType()));
        this.leftOperand = leftOperand;
        this.leftOperand.attach(this);
        this.rightOperand = rightOperand;
        this.rightOperand.attach(this);
        this.operator = operator;
    }
}
