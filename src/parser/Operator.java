package parser;

public enum Operator {
    ;

    abstract ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType);
}
