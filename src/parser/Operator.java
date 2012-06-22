package parser;

public enum Operator {
    ;

    abstract TypeSymbol validate(TypeSymbol leftOperand, TypeSymbol rightOperand);
}
