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
                        streamOffset += group.size();
                        startIndex = -1;
                    }
                    break;
            }
        }
    }
}
