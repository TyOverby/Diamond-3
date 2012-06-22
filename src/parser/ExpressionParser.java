package parser;

import com.google.common.collect.Lists;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.Deque;
import java.util.List;

final class ExpressionParser {
    /*
     * Intermediate expression parse states can consist of a heterogeneous stream of tokens and sub-expressions. As a
     * result, we are forced to store the state in a List<?>. At the beginning, the list consists entirely of tokens; at
     * the end, if everything goes as planned, it should consist entirely of expressions.
     */
    private List<Object> stream;

    public ExpressionParser() {
    }

    public List<Expression> parseExpression(List<Token<Lexeme>> tokens) throws ParseException {
        stream = Lists.<Object>newArrayList(tokens);
        parseGroupedExpressions(tokens); // recursively parse parenthetical and bracketed expressions first
        // parse operators of each precedence type, probably with separate methods
        // XXX: REMEMBER TO DO COMMA AT THE END
        parseGroupingOperators();
        // verify that the stream now consists entirely of expressions
        for (Object obj : stream) {
            assert (obj instanceof Expression);
        }
        @SuppressWarnings("unchecked")
        List<Expression> toReturn = (List<Expression>) stream;
        return toReturn;
    }

    private static enum GroupingSymbol {
        PARENTHESIS, BRACKET
    }

    private void parseGroupedExpressions(List<Token<Lexeme>> tokens) throws ParseException {
        // use the TOKEN stream, since the state stream will change if we encounter grouping symbols
        Deque<GroupingSymbol> groupingStack = Lists.newLinkedList(); // stack to handle nested grouping symbols
        int startIndex = -1; // the starting index of the current outermost group
        int streamOffset = 0; // offset between the state and token streams, increases as we replace tokens with expressions in the state stream
        for (int i = 0; i < tokens.size(); i++) {
            Token<Lexeme> token = tokens.get(i);
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
                    if (startIndex < 0) {
                        startIndex = i + 1;
                    }
                    groupingStack.push(symbol);
                    break;
                case RIGHT_PAREN: case RIGHT_BRACKET:
                    if (groupingStack.pop() != symbol) {
                        throw new ParseException("mismatched grouping symbols");
                    } else if (groupingStack.isEmpty()) {
                        List<Token<Lexeme>> group = tokens.subList(startIndex, i);
                        List<Expression> subExpressions = parseExpression(group);
                        stream.subList(startIndex - streamOffset, i - streamOffset).clear();
                        stream.addAll(startIndex - streamOffset, subExpressions);
                        streamOffset += (group.size() - subExpressions.size());
                        startIndex = -1;
                    }
                    break;
            }
        }
    }

    private void parseGroupingOperators() throws ParseException {
        for (int i = 0; i < stream.size(); i++) {
            if (stream.get(i) instanceof Token) {
                @SuppressWarnings("unchecked")
                Token<Lexeme> token = (Token<Lexeme>) stream.get(i);
                List<Object> subList;
                switch (token.lexeme) {
                    case PERIOD:
                        // has to be a member access, right?
                        String identifier = marshalToken(i - 1).contents + marshalToken(i + 1).contents;
                        subList = stream.subList(i - 1, i + 2);
                        subList.clear();
                        subList.add(new Token<Lexeme>(Lexeme.IDENTIFIER, identifier));
                        i -= 1; // to account for what we just did
                        break;
                    case LEFT_PAREN:
                        Token<Lexeme> leftToken = marshalToken(i - 1);
                        if (leftToken.lexeme == Lexeme.IDENTIFIER) {
                            // method invocation
                            List<Expression> parameters = Lists.newArrayList();
                            int j = i + 1;
                            while (true) {
                                Object obj = stream.get(j++);
                                if (obj instanceof Expression) {
                                    parameters.add((Expression) obj);
                                } else {
                                    break;
                                }
                            }
                            if (marshalToken(j).lexeme != Lexeme.RIGHT_PAREN) {
                                throw new ParseException("expected ')'");
                            }
                            Expression expression = new MethodInvocation(leftToken.contents, parameters);
                            subList = stream.subList(i - 1, j + 1);
                            subList.clear();
                            subList.add(expression);
                            i -= 1; // to account for what we just did
                        } else {
                            // simple parenthetical expression
                            Expression expression = marshalExpression(i + 1);
                            if (marshalToken(i + 2).lexeme != Lexeme.RIGHT_PAREN) {
                                throw new ParseException("expected ')'");
                            }
                            subList = stream.subList(i, i + 3);
                            subList.clear();
                            subList.add(expression);
                        }
                        break;
                }
            }
        }
    }

    private Token<Lexeme> marshalToken(int index) throws ParseException {
        Object obj = stream.get(index);
        if (obj instanceof Token) {
            @SuppressWarnings("unchecked")
            Token<Lexeme> token = (Token<Lexeme>) stream.get(index);
            return token;
        } else {
            throw new ParseException("expected free token");
        }
    }

    private Expression marshalExpression(int index) throws ParseException {
        Object obj = stream.get(index);
        if (obj instanceof Expression) {
            return (Expression) obj;
        } else {
            throw new ParseException("expected exception");
        }
    }
}
