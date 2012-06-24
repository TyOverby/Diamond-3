package parser;

import com.google.common.collect.*;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;
import parser.ExpressionParser.ParserState.Pointer;

import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.*;

final class ExpressionParser {
    static final class ParserState {
        final class Pointer {
            private int value;

            private boolean invalidated;

            private Pointer(int value) {
                this.value = value;
                invalidated = false;
                checkArgument(isValid());
            }

            public Pointer inc() {
                return inc(1);
            }

            public Pointer inc(int amount) {
                Pointer pointer = new Pointer(value + amount);
                pointers.put(value + amount, pointer);
                return pointer;
            }

            public Pointer dec() {
                return dec(1);
            }

            public Pointer dec(int amount) {
                Pointer pointer = new Pointer(value - amount);
                pointers.put(value - amount, pointer);
                return pointer;
            }

            public boolean isValid() {
                if (invalidated) {
                    throw new AssertionError("invalidated pointer");
                }
                return (value >= 0 && value < stream.size());
            }

            private void invalidate() {
                invalidated = true;
            }
        }

        /**
         * Intermediate expression parse states can consist of a heterogeneous stream of tokens and sub-expressions. As
         * a result, we are forced to store the state in a List&lt;Object&gt;. At the beginning, the list consists
         * entirely of tokens; at the end, if everything goes as planned, it should consist entirely of expressions.
         */
        private final List<Object> stream;

        private final Multimap<Integer, Pointer> pointers;

        private ParserState(List<Token<Lexeme>> tokens) {
            stream = Lists.<Object>newArrayList(tokens);
            pointers = HashMultimap.create();
        }

        public Pointer begin() {
            Pointer pointer = new Pointer(0);
            pointers.put(0, pointer);
            return pointer;
        }

        public Pointer end() {
            Pointer pointer = new Pointer(stream.size() - 1);
            pointers.put(stream.size() - 1, pointer);
            return pointer;
        }

        private Object dereference(Pointer pointer) {
            checkArgument(pointer.isValid());
            return stream.get(pointer.value);
        }

        public void replace(Pointer begin, Pointer end, Token<Lexeme> token) {
            replaceImpl(begin, end, token);
        }

        public void replace(Pointer begin, Pointer end, Expression expression) {
            replaceImpl(begin, end, expression);
        }

        private void replaceImpl(Pointer begin, Pointer end, Object object) {
            int beginIndex = begin.value;
            int endIndex = end.value;

            // perform the actual replacement
            List<Object> subList = stream.subList(beginIndex, endIndex);
            subList.clear();
            subList.add(object);

            // update all the pointers referring to the replaced range
            for (int i = beginIndex; i < endIndex; i++) {
                Iterable<Pointer> displaced = ImmutableSet.copyOf(pointers.get(i));
                pointers.removeAll(i);
                for (Pointer p : displaced) {
                    p.value = beginIndex;
                }
                pointers.putAll(beginIndex, displaced);
            }

            // update all pointers with indices after the replaced range
            int lengthChange = 1 - (endIndex - beginIndex);
            for (int i = endIndex; i < (stream.size() + lengthChange); i++) {
                Iterable<Pointer> displaced = ImmutableSet.copyOf(pointers.get(i));
                pointers.removeAll(i);
                for (Pointer p : displaced) {
                    p.value = i + lengthChange;
                }
                pointers.putAll(i + lengthChange, displaced);
            }
        }

        public void parseExpressionsInRange(Pointer begin, Pointer end) throws ParseException {
            checkArgument(end.value > begin.value);
            List<Token<Lexeme>> tokens = Lists.newArrayListWithCapacity(end.value - begin.value);
            for (int i = begin.value; i < end.value; i++) {
                tokens.add(marshalToken(new Pointer(i)));
            }
            List<Expression> expressions = new ExpressionParser().parseExpression(tokens);
            for (int i = begin.value; i < end.value; i++) {
                stream.set(i, expressions.get(i - begin.value));
            }
        }

        public void delete(Pointer pointer) {
            stream.remove(pointer.value);
            for (Pointer p : pointers.get(pointer.value)) {
                p.invalidate();
            }
            pointers.removeAll(pointer.value);
            for (int i = (pointer.value + 1); i < (stream.size() + 1); i++) {
                Iterable<Pointer> displaced = ImmutableSet.copyOf(pointers.get(i));
                pointers.removeAll(i);
                for (Pointer p : displaced) {
                    p.value = i - 1;
                }
                pointers.putAll(i - 1, displaced);
            }
        }

        public List<Expression> marshal() throws ParseException {
            List<Expression> toReturn = Lists.newArrayListWithCapacity(stream.size());
            Pointer pointer = begin();
            while (pointer.isValid()) {
                Expression expression = marshalExpression(pointer);
                toReturn.add(expression);
                pointer.inc();
            }
            return toReturn;
        }

        public boolean isToken(Pointer pointer) {
            return (dereference(pointer) instanceof Token);
        }

        public Token<Lexeme> marshalToken(Pointer pointer) throws ParseException {
            if (isToken(pointer)) {
                @SuppressWarnings("unchecked")
                Token<Lexeme> token = (Token<Lexeme>) dereference(pointer);
                return token;
            } else {
                throw new ParseException("expected free token");
            }
        }

        public boolean isExpression(Pointer pointer) {
            return (dereference(pointer) instanceof Expression);
        }

        public Expression marshalExpression(Pointer pointer) throws ParseException {
            if (isExpression(pointer)) {
                return (Expression) dereference(pointer);
            } else {
                Token<Lexeme> token = marshalToken(pointer);
                switch (token.lexeme) {
                    case IDENTIFIER:
                        return new IdentifierReference(token.contents);
                    case INTEGRAL_LITERAL:
                        BigInteger value;
                        if (token.contents.startsWith("0x")) {
                            value = new BigInteger(token.contents.substring(2), 16);
                        } else {
                            value = new BigInteger(token.contents);
                        }
                        return new IntegralLiteral(value);
                    case STRING_LITERAL:
                        return new StringLiteral(token.contents);
                    default:
                        throw new ParseException("expected expression");
                }
            }
        }
    }

    private ParserState state;

    public ExpressionParser() {
    }

    public List<Expression> parseExpression(List<Token<Lexeme>> tokens) throws ParseException {
        state = new ParserState(tokens);
        parseGroupedExpressions(); // recursively parse parenthetical and bracketed expressions first
        parseGroupingOperators();
        for (int precedence = 14; precedence > 0; precedence--) {
            parseOperators(precedence);
        }
        parseVariableDeclarations();
        parseLists();
        return state.marshal();
    }

    private static enum GroupingSymbol {
        PARENTHESIS, BRACKET
    }

    private void parseGroupedExpressions() throws ParseException {
        Deque<GroupingSymbol> groupingStack = Lists.newLinkedList(); // stack to handle nested grouping symbols
        Pointer start = state.begin().dec(); // the starting index of the current outermost group
        for (Pointer p = state.begin(); p.isValid(); p = p.inc()) {
            Token<Lexeme> token = state.marshalToken(p);
            GroupingSymbol symbol;
            switch (token.lexeme) {
                case LEFT_PAREN: case RIGHT_PAREN: symbol = GroupingSymbol.PARENTHESIS; break;
                case LEFT_BRACKET: case RIGHT_BRACKET: symbol = GroupingSymbol.BRACKET; break;
                default: symbol = null;
            }
            if (symbol == null) {
                continue;
            }
            switch (token.lexeme) {
                case LEFT_PAREN: case LEFT_BRACKET:
                    if (!start.isValid()) {
                        start = p.inc();
                    }
                    groupingStack.push(symbol);
                    break;
                case RIGHT_PAREN: case RIGHT_BRACKET:
                    if (groupingStack.pop() != symbol) {
                        throw new ParseException("mismatched grouping symbols");
                    } else if (groupingStack.isEmpty()) {
                        state.parseExpressionsInRange(start, p);
                        start = state.begin().dec();
                    }
                    break;
            }
        }

        // if the stack has anything left, syntax error
        if (!groupingStack.isEmpty()) {
            switch (groupingStack.pop()) {
                case PARENTHESIS:
                    throw new ParseException("expected ')'");
                case BRACKET:
                    throw new ParseException("expected ']'");
            }
        }
    }

    private ExpressionType getTypeEndingAt(Pointer p) throws ParseException {
        Token<Lexeme> token = state.marshalToken(p);
        switch (token.lexeme) {
            case IDENTIFIER:
                return new UserDefinedType(token.contents);
            case RIGHT_BRACKET:
                ExpressionType elementType = getTypeEndingAt(p.dec(2));
                if (state.marshalToken(p.dec()).lexeme == Lexeme.LEFT_BRACKET) {
                    return new ArrayType(elementType);
                } else {
                    throw new ParseException("expected '['");
                }
            case BOOLEAN:
                return BuiltInType.BOOLEAN;
            case SHORT:
                return BuiltInType.SHORT;
            case INT:
                return BuiltInType.INT;
            case LONG:
                return BuiltInType.LONG;
            default:
                throw new ParseException("expected identifier, primitive type, or ']'");
        }
    }

    private Set<Modifier> getModifiersEndingAt(Pointer p) throws ParseException {
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        boolean flag = true;
        while (flag) {
            if (state.isToken(p)) {
                Token<Lexeme> token = state.marshalToken(p);
                p = p.dec();
                if (Modifier.isModifier(token.lexeme)) {
                    modifiers.add(Modifier.fromLexeme(token.lexeme));
                    continue;
                }
            }
            flag = false;
        }
        return modifiers;
    }

    private void parseGroupingOperators() throws ParseException {
        for (Pointer p = state.begin(); p.isValid(); p = p.inc()) {
            if (state.isToken(p)) {
                Token<Lexeme> token = state.marshalToken(p);
                Expression target = null;
                switch (token.lexeme) {
                    case LEFT_BRACKET:
                        if (state.isToken(p.inc()) && state.marshalToken(p.inc()).lexeme == Lexeme.RIGHT_BRACKET) {
                            Token<Lexeme> farRightToken = state.marshalToken(p.inc().inc());
                            if (farRightToken.lexeme == Lexeme.IDENTIFIER) {
                                // variable declaration with an array type
                                ExpressionType type = getTypeEndingAt(p.inc());
                                int typeLength = type.getNumberOfLexemes();
                                Set<Modifier> modifiers = getModifiersEndingAt(p.inc().dec(typeLength));
                                Expression expression = new VariableDeclaration(type, farRightToken.contents, modifiers);
                                state.replace(p.dec(typeLength).dec(modifiers.size()), p.inc(3), expression);
                                break;
                            } else if (farRightToken.lexeme == Lexeme.LEFT_BRACKET) {
                                // well, we're in the middle of a multi-dimensional array type token
                                // get out of here and let the next iteration do it
                                p = p.inc();
                                break;
                            } else if (farRightToken.lexeme == Lexeme.PERIOD) {
                                // member access on our array type
                                // turn our (compound) array type into an identifier and bounce off of PERIOD
                                ExpressionType type = getTypeEndingAt(p.inc());
                                int typeLength = type.getNumberOfLexemes();
                                state.replace(p.inc(2).dec(typeLength), p.inc(2), new Token<>(Lexeme.IDENTIFIER, type.toString()));
                            } else {
                                // not sure what else could be here?
                                throw new ParseException("expected identifier, '[', or '.'");
                            }
                        } else {
                            // array access
                            if (state.marshalToken(p.inc(2)).lexeme != Lexeme.RIGHT_BRACKET) {
                                throw new ParseException("expected ']'");
                            }
                            Expression array = state.marshalExpression(p.dec());
                            Expression index = state.marshalExpression(p.inc());
                            Expression arrayAccess = new ArrayAccess(array, index);
                            state.replace(p.dec(), p.inc(3), arrayAccess);
                            break;
                        }
                    case PERIOD:
                        // has to be a member access, let's figure out what kind
                        if (state.marshalToken(p.dec()).lexeme != Lexeme.IDENTIFIER) {
                            throw new ParseException("expected identifier");
                        } else if (state.marshalToken(p.inc()).lexeme != Lexeme.IDENTIFIER && state.marshalToken(p.inc()).lexeme != Lexeme.NEW) {
                            throw new ParseException("expected identifier or \"new\"");
                        }
                        Token<Lexeme> farRightToken = state.marshalToken(p.inc(2));
                        if (farRightToken.lexeme == Lexeme.LEFT_PAREN) {
                            // method member access
                            // deliberately no break; let it be handled as a method invocation below
                            target = state.marshalExpression(p.dec());
                            p = p.inc(2);
                        } else {
                            // field member access
                            // the field had better not be called new!
                            Token<Lexeme> fieldName = state.marshalToken(p.inc());
                            if (fieldName.lexeme != Lexeme.IDENTIFIER) {
                                throw new ParseException("expected identifier");
                            }
                            Expression reference = new FieldReference(state.marshalExpression(p.dec()), fieldName.contents);
                            state.replace(p.dec(), p.inc(2), reference);
                            break;
                        }
                    case LEFT_PAREN:
                        Token<Lexeme> leftToken = state.marshalToken(p.dec());
                        if (leftToken.lexeme == Lexeme.IDENTIFIER || leftToken.lexeme == Lexeme.NEW) {
                            // method or constructor invocation
                            List<Expression> parameters = Lists.newArrayList();
                            Pointer q = p.inc();
                            while (true) {
                                if (state.isExpression(q)) {
                                    parameters.add(state.marshalExpression(q));
                                    q = q.inc();
                                } else {
                                    break;
                                }
                            }
                            if (state.marshalToken(q).lexeme != Lexeme.RIGHT_PAREN) {
                                throw new ParseException("expected ')'");
                            }
                            Expression invocation;
                            Pointer begin;
                            if (target == null) {
                                target = new ThisExpression();
                                begin = p.dec();
                            } else {
                                begin = p.dec(3);
                            }
                            if (leftToken.lexeme == Lexeme.IDENTIFIER) {
                                invocation = new MethodInvocation(leftToken.contents, target, parameters);
                            } else if (leftToken.lexeme == Lexeme.NEW) {
                                invocation = new ConstructorInvocation(target, parameters);
                            } else {
                                throw new AssertionError("Left token MUST either be an identifier or \"new\"!");
                            }
                            state.replace(begin, q.inc(), invocation);
                        } else {
                            // simple parenthetical expression
                            Expression expression = state.marshalExpression(p.inc());
                            if (state.marshalToken(p.inc(2)).lexeme != Lexeme.RIGHT_PAREN) {
                                throw new ParseException("expected ')'");
                            }
                            state.replace(p, p.inc(3), expression);
                        }
                        break;
                }
            }
        }
    }

    private void parseOperators(int precedence) throws ParseException {
        if (precedence == 1 || precedence == 2 || precedence == 13 || precedence == 14) {
            // right associative
            for (Pointer p = state.end(); p.isValid(); p = p.dec()) {
                parseOperatorAtIndex(precedence, p);
            }
        } else {
            // left associative
            for (Pointer p = state.begin(); p.isValid(); p = p.inc()) {
                parseOperatorAtIndex(precedence, p);
            }
        }
    }

    private void parseOperatorAtIndex(int precedence, Pointer p) throws ParseException {
        if (state.isToken(p)) {
            Token<Lexeme> token = state.marshalToken(p);
            Operator operator = Operator.getForLexeme(token.lexeme);
            if (operator != null && operator.getPrecedence() == precedence) {
                Pointer begin;
                Pointer end;
                Expression expression;
                switch (operator.getType()) {
                    case POSTFIX:
                        begin = p.dec();
                        end = p.inc();
                        expression = new OperatorExpression(state.marshalExpression(begin), operator);
                        break;
                    case UNARY:
                        begin = p;
                        end = p.inc(2);
                        expression = new OperatorExpression(operator, state.marshalExpression(end.dec()));
                        break;
                    case BINARY:
                        begin = p.dec();
                        end = p.inc(2);
                        expression = new OperatorExpression(state.marshalExpression(begin), state.marshalExpression(end.dec()), operator);
                        break;
                    default:
                        throw new AssertionError();
                }
                state.replace(begin, end, expression);
            }
        }
    }

    /*
     * TODO: this really isn't correct, it needs to be handled in marshalExpression()
     */
    private void parseVariableDeclarations() throws ParseException {
        for (Pointer p = state.begin(); p.isValid(); p = p.inc()) {
            if (state.isToken(p)) {
                Token<Lexeme> token = state.marshalToken(p);
                if (token.lexeme == Lexeme.IDENTIFIER) {
                    // there should be a type immediately before us
                    ExpressionType type = getTypeEndingAt(p.dec());
                    int typeLength = type.getNumberOfLexemes();
                    Set<Modifier> modifiers = getModifiersEndingAt(p.dec().dec(typeLength));
                    Expression expression = new VariableDeclaration(type, token.contents, modifiers);
                    state.replace(p.dec(typeLength).dec(modifiers.size()), p.inc(), expression);
                }
            }
        }
    }

    private void parseLists() throws ParseException {
        for (Pointer p = state.begin(); p.isValid(); p = p.inc()) {
            if (state.isToken(p)) {
                Token<Lexeme> token = state.marshalToken(p);
                if (token.lexeme == Lexeme.COMMA) {
                    // ensure that we're between two expressions
                    // XXX: marshalling expressions needs to modify the stream as was planned
                    state.marshalExpression(p.dec());
                    state.marshalExpression(p.inc());
                    // then we can delete this token so that the main parsing succeeds
                    state.delete(p);
                }
            }
        }
    }
}
