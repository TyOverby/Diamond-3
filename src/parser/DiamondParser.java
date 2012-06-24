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
     * The index of the LAST token read within the tokens list.
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
                case UNSAFE:
                    modifiers.add(Modifier.UNSAFE);
                    break;
                // statements preceded by modifiers and followed by a block
                case CLASS:
                    Token<Lexeme> typeName = tokens.get(++pos);
                    if (typeName.lexeme != Lexeme.IDENTIFIER) {
                        throw new ParseException("expected identifier");
                    }
                    Statement typeDeclaration = new TypeDeclaration(context, typeName.contents, modifiers);
                    parseBlock(typeDeclaration);
                    break;
                // statements followed by a block, but without modifiers
                case DO:
                    int doIndex = pos; // index of the "do" keyword
                    int whileIndex = findDoWhile(); // index of the "while" keyword
                    int endIndex; // index of the semicolon at the end
                    pos = whileIndex;
                    condition = getParentheticalExpression();
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
                    condition = getParentheticalExpression();
                    Statement ifStatement = new IfStatement(context, condition);
                    parseBlock(ifStatement);
                    break;
                case REPEAT:
                    Expression repeatCount = getParentheticalExpression();
                    Statement repeatLoop = new RepeatLoop(context, repeatCount);
                    parseBlock(repeatLoop);
                    break;
                case SWITCH:
                    Expression value = getParentheticalExpression();
                    Statement switchStatement = new SwitchStatement(context, value);
                    parseBlock(switchStatement);
                    break;
                case WHILE:
                    condition = getParentheticalExpression();
                    Statement whileLoop = new WhileLoop(context, condition);
                    parseBlock(whileLoop);
                    break;
                // TODO: statements not followed by a block
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
                            case UNSAFE: buffer.add(0, new Token<>(Lexeme.UNSAFE, "unsafe")); break;
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
     * Returns the expression found in parenthesis at the current position, with the left parenthesis immediately
     * following {@code pos}. In this context, the parentheses are mandatory, and a {@code ParseException} will be
     * thrown if there is no left parenthesis immediately following {@code pos}. Expressions of this type are found as
     * part of the {@code if}, {@code do}, {@code while}, {@code repeat}, and {@code switch} statements.
     *
     * @return the parenthetical expression immediately following {@code pos}
     * @throws ParseException if either the starting or ending parenthesis is missing; or if multiple or no expressions
     *                        are found
     */
    private Expression getParentheticalExpression() throws ParseException {
        if (tokens.get(++pos).lexeme != Lexeme.LEFT_PAREN) {
            throw new ParseException("expected '('");
        }
        int beginIndex = (pos + 1);
        int depth = 1;
        while (pos < tokens.size()) {
            Token<Lexeme> token = tokens.get(++pos);
            switch (token.lexeme) {
                case LEFT_PAREN:
                    depth++;
                    break;
                case RIGHT_PAREN:
                    depth--;
                    break;
            }
            if (depth == 0) {
                List<Token<Lexeme>> expressionTokens = tokens.subList(beginIndex, pos);
                List<Expression> expressions = new ExpressionParser().parseExpression(expressionTokens);
                if (expressions.size() != 1) {
                    throw new ParseException("expected a single expression; saw " + expressions.size());
                } else {
                    return expressions.get(0);
                }
            }
        }
        throw new ParseException("expected ')'");
    }
}
