package parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class DiamondParser {
    private List<Token<Lexeme>> tokens;

    /**
     * The index of the <i>last</i> token read within the tokens list.
     */
    private int pos;

    public DiamondParser() {
    }

    public CompilationUnit parse(List<Token<Lexeme>> tokens) throws ParseException {
        this.tokens = ImmutableList.copyOf(tokens);
        this.pos = -1;

        CompilationUnit compilationUnit = new CompilationUnit();
        while (pos < tokens.size()) {
            parse(compilationUnit);
        }
        return compilationUnit;
    }

    private void parse(Statement context) throws ParseException {
        List<Token<Lexeme>> buffer = Lists.newArrayList();
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        while (pos < tokens.size()) {
            Token<Lexeme> token = tokens.get(++pos);
            Expression condition; // since we need it in multiple cases
            switch (token.lexeme) {
                // modifiers
                case PRIVATE:
                    modifiers.add(Modifier.PRIVATE);
                    break;
                case STATIC:
                    modifiers.add(Modifier.STATIC);
                    break;
                // statements preceded by modifiers and followed by a block
                case CLASS:
                    Token<Lexeme> typeName = tokens.get(++pos);
                    if (typeName.lexeme != Lexeme.IDENTIFIER) {
                        throw new ParseException("expected identifier");
                    } else if (!(context instanceof CompilationUnit)) {
                        throw new ParseException("nested types are not yet supported");
                    }
                    Statement typeDeclaration = new TypeDeclaration((CompilationUnit) context, typeName.contents, modifiers);
                    parseBlock(typeDeclaration);
                    break;
                // statements followed by a block, but without modifiers
                case DO:
                    int doIndex = pos; // index of the "do" keyword
                    int whileIndex = findDoWhile(); // index of the "while" keyword
                    int endIndex; // index of the semicolon at the end
                    pos = whileIndex;
                    condition = getAdjacentExpression(true);
                    if (tokens.get(++pos).lexeme != Lexeme.SEMICOLON) {
                        throw new ParseException("expected ';'");
                    }
                    endIndex = pos;
                    Statement doLoop = new DoLoop(context, condition);
                    pos = doIndex;
                    parseBlock(doLoop);
                    pos = endIndex;
                    break;
                case ELSE:
                    Statement elseStatement = new ElseStatement(context);
                    parseBlock(elseStatement);
                    break;
                case FOR:
                    // TODO: this one is trickier, so do it later
                    break;
                case IF:
                    condition = getAdjacentExpression(true);
                    Statement ifStatement = new IfStatement(context, condition);
                    parseBlock(ifStatement);
                    break;
                case REPEAT:
                    Expression repeatCount = getAdjacentExpression(true);
                    Statement repeatLoop = new RepeatLoop(context, repeatCount);
                    parseBlock(repeatLoop);
                    break;
                case SWITCH:
                    Expression value = getAdjacentExpression(true);
                    Statement switchStatement = new SwitchStatement(context, value);
                    parseBlock(switchStatement);
                    break;
                case WHILE:
                    condition = getAdjacentExpression(true);
                    Statement whileLoop = new WhileLoop(context, condition);
                    parseBlock(whileLoop);
                    break;
                // statements not followed by a block
                case BREAK:
                    if (tokens.get(++pos).lexeme != Lexeme.SEMICOLON) {
                        throw new ParseException("expected ';'");
                    }
                    new BreakStatement(context);
                    break;
                case CONTINUE:
                    if (tokens.get(++pos).lexeme != Lexeme.SEMICOLON) {
                        throw new ParseException("expected ';'");
                    }
                    new ContinueStatement(context);
                    break;
                case DELETE:
                    // there must be an expression, specifically a variable reference, immediately to the right
                    Expression identifierReference = getAdjacentExpression(false);
                    if (!(identifierReference instanceof IdentifierReference)) {
                        throw new ParseException("expected variable reference");
                    }
                    new DeleteStatement(context, (IdentifierReference) identifierReference);
                    break;
                case RETURN:
                    // there are two variants, with and without a value
                    if (tokens.get(pos + 1).lexeme == Lexeme.SEMICOLON) {
                        // no value
                        new ReturnStatement(context);
                        pos += 1;
                    } else {
                        // with value
                        Expression returnValue = getAdjacentExpression(false);
                        new ReturnStatement(context, returnValue);
                    }
                    break;
                // control characters
                case RIGHT_BRACE:
                    if (!buffer.isEmpty() || !modifiers.isEmpty()) {
                        throw new ParseException("expected statement or ';'");
                    }
                    return;
                case SEMICOLON:
                    for (Modifier modifier : modifiers) {
                        switch (modifier) {
                            case PRIVATE: buffer.add(0, new Token<>(Lexeme.PRIVATE, "private")); break;
                            case STATIC: buffer.add(0, new Token<>(Lexeme.STATIC, "static")); break;
                            default: throw new UnsupportedOperationException("unknown modifier");
                        }
                    }
                    List<Expression> expressions = new ExpressionParser().parseExpression(buffer);
                    if (expressions.size() > 1) {
                        throw new ParseException("expected ';'");
                    } else if (!expressions.isEmpty()) {
                        expressions.get(0).attach(context);
                    }
                    break;
                case LEFT_BRACE:
                    throw new ParseException("expected type declaration, if/else, or loop");
            }
        }
        throw new ParseException("expected '}'");
    }

    private void parseBlock(Statement context) throws ParseException {
        switch (tokens.get(++pos).lexeme) {
            case LEFT_BRACE:
                parse(context);
            case SEMICOLON:
                break;
            // TODO: needs to support else-if chaining
            default:
                throw new ParseException("expected '{' or ';'");
        }
    }

    /**
     * Searches for the {@code while} keyword following a {@code do} block, and returns the index of the {@code while}
     * token within {@code tokens}. It is expected that {@code pos} is set the the index of the {@code do} keyword which
     * triggered this search. There are two valid locations for the keyword:
     * <ul>
     * <li>Immediately following a curly-brace-delimited block, which itself immediately follows {@code do}</li>
     * <li>Immediately following a semicolon, which itself immediately follows {@code do}</li>
     * </ul>
     *
     * @return the index for the {@code while} keyword corresponding to the {@code do} token at {@code pos}
     * @throws ParseException if the {@code while} keyword is missing; if neither {@code &#123;} nor {@code ;}
     *                        immediately follows {@code do}; or if the token stream ends without the appropriate number
     *                        of {@code &#125;} tokens
     */
    private int findDoWhile() throws ParseException {
        int depth = 0;
        boolean expectingWhile = false; // whether the next token should be the "while" we are looking for
        // note that we need our own index here, because we don't want to modify the global one
        // i has the same meaning as pos in this context, namely, the index of the last token read
        int i = pos;
        while (i < tokens.size()) {
            Token<Lexeme> token = tokens.get(++i);
            if (expectingWhile) {
                if (token.lexeme != Lexeme.WHILE) {
                    throw new ParseException("expected \"while\"");
                } else {
                    return i;
                }
            }
            switch (token.lexeme) {
                case LEFT_BRACE:
                    depth++;
                    break;
                case RIGHT_BRACE:
                    depth--;
                    break;
                case SEMICOLON:
                    break;
                default:
                    if ((i - 1) == pos) {
                        // the first token following "do" must be either a brace or semicolon
                        throw new ParseException("expected '{' or ';'");
                    }
            }
            if (depth == 0) {
                expectingWhile = true;
            }
        }
        throw new ParseException("expected '}'");
    }

    /**
     * <p>
     *     Returns the single expression adjacent to the current position, beginning immediately following {@code pos}
     *     (since {@code pos} is the index of the <i>last</i> token read). If no expression is found, or if there are
     *     multiple comma-separated expressions, {@code ParseException} will be thrown. This method has two variants,
     *     which are distinguished by the value of the {@code boolean} parameter.
     * </p>
     * <p>
     *     If {@code requiresParentheses} is {@code true}, the method will check that the expression is surrounded by a
     *     pair of parentheses; a {@code ParseException} is thrown if they are missing. This variant is used to parse
     *     the {@code if}, {@code do}, {@code while}, {@code repeat}, and {@code switch} statements, all of which
     *     require parentheses around a condition or value. The expression is deemed to have ended when the final
     *     enclosing right parenthesis is reached.
     * </p>
     * <p>
     *     If {@code requiresParentheses} is {@code false}, the method will not check for parentheses around the
     *     expression, though they may still be used, as in any other expression. This variant is used to parse the
     *     {@code delete} and {@code return} statements. The expression is deemed to have ended when a semicolon is
     *     reached.
     * </p>
     *
     * @param requiresParentheses whether the adjacent expression must be contained in a pair of parentheses
     * @return the single expression adjacent to the current position
     * @throws ParseException if multiple or no adjacent expressions are found; or, if {@code requiresParentheses} is
     *                        {@code true} and either the starting or ending parenthesis around the expression is
     *                        missing
     */
    private Expression getAdjacentExpression(boolean requiresParentheses) throws ParseException {
        List<Token<Lexeme>> expressionTokens = null;
        int beginIndex = (pos + 1);
        boolean done = false;
        if (requiresParentheses) {
            if (tokens.get(++pos).lexeme != Lexeme.LEFT_PAREN) {
                throw new ParseException("expected '('");
            }
            int depth = 1;
            while (pos < tokens.size() && !done) {
                switch (tokens.get(++pos).lexeme) {
                    case LEFT_PAREN:
                        depth++;
                        break;
                    case RIGHT_PAREN:
                        depth--;
                        break;
                }
                if (depth == 0) {
                    expressionTokens = tokens.subList(beginIndex, pos + 1);
                    done = true;
                }
            }
        } else {
            while (pos < tokens.size() && !done) {
                switch (tokens.get(++pos).lexeme) {
                    case SEMICOLON:
                        expressionTokens = tokens.subList(beginIndex, pos);
                        done = true;
                        break;
                }
            }
        }
        if (expressionTokens == null) {
            char expected = (requiresParentheses ? ')' : ';');
            throw new ParseException(String.format("expected '%c'", expected));
        }
        List<Expression> expressions = new ExpressionParser().parseExpression(expressionTokens);
        if (expressions.size() != 1) {
            throw new ParseException("expected a single expression; saw " + expressions.size());
        } else {
            return expressions.get(0);
        }
    }
}
