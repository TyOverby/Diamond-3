package parser;

import lexer.DiamondLexer.Lexeme;

import static com.google.common.base.Preconditions.*;

public enum Operator {
    POSTFIX_INCREMENT(14, Type.POSTFIX, Lexeme.PLUS_PLUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(rightOperandType == null);
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return leftOperandType;
        }
    },

    POSTFIX_DECREMENT(14, Type.POSTFIX, Lexeme.MINUS_MINUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(rightOperandType == null);
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return leftOperandType;
        }
    },

    UNARY_INCREMENT(13, Type.UNARY, Lexeme.PLUS_PLUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == null);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            return rightOperandType;
        }
    },

    UNARY_DECREMENT(13, Type.UNARY, Lexeme.MINUS_MINUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == null);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            return rightOperandType;
        }
    },

    UNARY_PLUS(13, Type.UNARY, Lexeme.PLUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == null);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            return rightOperandType;
        }
    },

    UNARY_MINUS(13, Type.UNARY, Lexeme.MINUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == null);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            return rightOperandType;
        }
    },

    LOGICAL_NOT(13, Type.UNARY, Lexeme.NOT) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == null);
            checkArgument(rightOperandType == BuiltInType.BOOLEAN || rightOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    MULTIPLICATION(12, Type.BINARY, Lexeme.TIMES) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isNumeric() && rightOperandType.isNumeric()) {
                if (leftOperandType == BuiltInType.LONG || rightOperandType == BuiltInType.LONG) {
                    return BuiltInType.LONG;
                } else if (leftOperandType == BuiltInType.INT || rightOperandType == BuiltInType.INT) {
                    return BuiltInType.INT;
                } else {
                    return BuiltInType.SHORT;
                }
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    DIVISION(12, Type.BINARY, Lexeme.DIVIDE) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isNumeric() && rightOperandType.isNumeric()) {
                if (leftOperandType == BuiltInType.LONG || rightOperandType == BuiltInType.LONG) {
                    return BuiltInType.LONG;
                } else if (leftOperandType == BuiltInType.INT || rightOperandType == BuiltInType.INT) {
                    return BuiltInType.INT;
                } else {
                    return BuiltInType.SHORT;
                }
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    MODULUS(12, Type.BINARY, Lexeme.MODULO) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isNumeric() && rightOperandType.isNumeric()) {
                if (leftOperandType == BuiltInType.LONG || rightOperandType == BuiltInType.LONG) {
                    return BuiltInType.LONG;
                } else if (leftOperandType == BuiltInType.INT || rightOperandType == BuiltInType.INT) {
                    return BuiltInType.INT;
                } else {
                    return BuiltInType.SHORT;
                }
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    ADDITION_OR_CONCATENATION(11, Type.BINARY, Lexeme.PLUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            if (leftOperandType == BuiltInType.STRING || rightOperandType == BuiltInType.STRING) {
                return BuiltInType.STRING;
            } else {
                checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
                checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
                if (leftOperandType.isNumeric() && rightOperandType.isNumeric()) {
                    if (leftOperandType == BuiltInType.LONG || rightOperandType == BuiltInType.LONG) {
                        return BuiltInType.LONG;
                    } else if (leftOperandType == BuiltInType.INT || rightOperandType == BuiltInType.INT) {
                        return BuiltInType.INT;
                    } else {
                        return BuiltInType.SHORT;
                    }
                } else {
                    return BuiltInType.INDETERMINATE;
                }
            }
        }
    },

    SUBTRACTION(11, Type.BINARY, Lexeme.MINUS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isNumeric() && rightOperandType.isNumeric()) {
                if (leftOperandType == BuiltInType.LONG || rightOperandType == BuiltInType.LONG) {
                    return BuiltInType.LONG;
                } else if (leftOperandType == BuiltInType.INT || rightOperandType == BuiltInType.INT) {
                    return BuiltInType.INT;
                } else {
                    return BuiltInType.SHORT;
                }
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    BITWISE_SHIFT_LEFT(10, Type.BINARY, Lexeme.SHIFT_LEFT) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isIntegral() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isIntegral() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isIntegral() && rightOperandType.isIntegral()) {
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    BITWISE_SHIFT_RIGHT(10, Type.BINARY, Lexeme.SHIFT_RIGHT) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isIntegral() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isIntegral() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isIntegral() && rightOperandType.isIntegral()) {
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    LESS_THAN(9, Type.BINARY, Lexeme.LESS_THAN) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    LEQ(9, Type.BINARY, Lexeme.LESS_THAN_EQUALS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    GREATER_THAN(9, Type.BINARY, Lexeme.GREATER_THAN) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    GEQ(9, Type.BINARY, Lexeme.GREATER_THAN_EQUALS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isNumeric() || leftOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    EQUALS(8, Type.BINARY, Lexeme.EQUALITY) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkNotNull(leftOperandType);
            checkNotNull(rightOperandType);
            return BuiltInType.BOOLEAN;
        }
    },

    NOT_EQUALS(8, Type.BINARY, Lexeme.NOT_EQUALS) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkNotNull(leftOperandType);
            checkNotNull(rightOperandType);
            return BuiltInType.BOOLEAN;
        }
    },

    BITWISE_AND(7, Type.BINARY, Lexeme.BITWISE_AND) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isIntegral() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isIntegral() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isIntegral() && rightOperandType.isIntegral()) {
                checkArgument(leftOperandType == rightOperandType);
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    BITWISE_XOR(6, Type.BINARY, Lexeme.BITWISE_XOR) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isIntegral() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isIntegral() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isIntegral() && rightOperandType.isIntegral()) {
                checkArgument(leftOperandType == rightOperandType);
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    BITWISE_OR(5, Type.BINARY, Lexeme.BITWISE_OR) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType.isIntegral() || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType.isIntegral() || rightOperandType == BuiltInType.INDETERMINATE);
            if (leftOperandType.isIntegral() && rightOperandType.isIntegral()) {
                checkArgument(leftOperandType == rightOperandType);
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    LOGICAL_AND(4, Type.BINARY, Lexeme.LOGICAL_AND) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == BuiltInType.BOOLEAN || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType == BuiltInType.BOOLEAN || rightOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    LOGICAL_OR(3, Type.BINARY, Lexeme.LOGICAL_OR) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == BuiltInType.BOOLEAN || leftOperandType == BuiltInType.INDETERMINATE);
            checkArgument(rightOperandType == BuiltInType.BOOLEAN || rightOperandType == BuiltInType.INDETERMINATE);
            return BuiltInType.BOOLEAN;
        }
    },

    CONDITIONAL_COLON(2, Type.BINARY, Lexeme.TERNARY_COLON) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkNotNull(leftOperandType);
            checkNotNull(rightOperandType);
            if (leftOperandType.equals(rightOperandType)) {
                return leftOperandType;
            } else {
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    CONDITIONAL_QUESTION(2, Type.BINARY, Lexeme.TERNARY_QUESTION) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkArgument(leftOperandType == BuiltInType.BOOLEAN || leftOperandType == BuiltInType.INDETERMINATE);
            checkNotNull(rightOperandType);
            return BuiltInType.INDETERMINATE;
        }
    },

    ASSIGNMENT(1, Type.BINARY, Lexeme.ASSIGNMENT) {
        @Override
        ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
            checkNotNull(leftOperandType);
            checkNotNull(rightOperandType);
            if (leftOperandType.equals(rightOperandType)) {
                return leftOperandType;
            } else if (leftOperandType == BuiltInType.LONG && rightOperandType.isIntegral()) {
                return BuiltInType.LONG;
            } else if (leftOperandType == BuiltInType.INT && rightOperandType == BuiltInType.SHORT) {
                return BuiltInType.INT;
            } else {
                checkArgument(leftOperandType == BuiltInType.INDETERMINATE || rightOperandType == BuiltInType.INDETERMINATE);
                return BuiltInType.INDETERMINATE;
            }
        }
    },

    ADDITIVE_ASSIGNMENT(1, Type.BINARY, Lexeme.PLUS_EQUALS, Operator.ADDITION_OR_CONCATENATION),

    DIFFERENTIAL_ASSIGNMENT(1, Type.BINARY, Lexeme.MINUS_EQUALS, Operator.SUBTRACTION),

    MULTIPLICATIVE_ASSIGNMENT(1, Type.BINARY, Lexeme.TIMES_EQUALS, Operator.MULTIPLICATION),

    DIVISIONAL_ASSIGNMENT(1, Type.BINARY, Lexeme.DIVIDE_EQUALS, Operator.DIVISION),

    MODULO_ASSIGNMENT(1, Type.BINARY, Lexeme.MODULO_EQUALS, Operator.MODULUS),

    BITWISE_AND_ASSIGNMENT(1, Type.BINARY, Lexeme.BITWISE_AND_EQUALS, Operator.BITWISE_AND),

    BITWISE_XOR_ASSIGNMENT(1, Type.BINARY, Lexeme.BITWISE_XOR_EQUALS, Operator.BITWISE_XOR),

    BITWISE_OR_ASSIGNMENT(1, Type.BINARY, Lexeme.BITWISE_OR_EQUALS, Operator.BITWISE_OR),

    LEFT_SHIFT_ASSIGNMENT(1, Type.BINARY, Lexeme.SHIFT_LEFT_EQUALS, Operator.BITWISE_SHIFT_LEFT),

    RIGHT_SHIFT_ASSIGNMENT(1, Type.BINARY, Lexeme.SHIFT_RIGHT_EQUALS, Operator.BITWISE_SHIFT_RIGHT);

    public static enum Type {
        POSTFIX, UNARY, BINARY
    }

    private final int precedence;

    private final Type type;

    private final Lexeme lexeme;

    private final Operator baseOperator;

    private Operator(int precedence, Type type, Lexeme lexeme) {
        this(precedence, type, lexeme, null);
    }

    private Operator(int precedence, Type type, Lexeme lexeme, Operator baseOperator) {
        this.precedence = precedence;
        this.type = type;
        this.lexeme = lexeme;
        this.baseOperator = baseOperator;
    }

    public int getPrecedence() {
        return precedence;
    }

    public Type getType() {
        return type;
    }

    ExpressionType validate(ExpressionType leftOperandType, ExpressionType rightOperandType) {
        if (baseOperator != null) {
            return baseOperator.validate(leftOperandType, rightOperandType);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Operator getForLexeme(Lexeme lexeme) {
        for (Operator operator : values()) {
            if (operator.lexeme == lexeme) {
                return operator;
            }
        }
        return null;
    }
}
