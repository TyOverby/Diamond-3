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
            switch (token.lexeme) {
                case PRIVATE:
                    modifiers.add(Modifier.PRIVATE);
                    break;
                case STATIC:
                    modifiers.add(Modifier.STATIC);
                    break;
                case UNSAFE:
                    modifiers.add(Modifier.UNSAFE);
                    break;
                case CLASS:
                    Token<Lexeme> typeName = tokens.get(++pos);
                    if (typeName.lexeme != Lexeme.IDENTIFIER) {
                        throw new ParseException("expected identifier");
                    }
                    Statement typeDeclaration = new TypeDeclaration(context, typeName.contents, modifiers);
                    parseBlock(typeDeclaration);
                    break;
                case DO:
                    int doIndex = pos; // index of the "do" keyword
                    int whileIndex = findDoWhile(); // index of the "while" keyword
                    int endIndex; // index of the semicolon at the end
                    pos = whileIndex;
                    Expression condition = getParentheticalExpression();
                    if (tokens.get(++pos).lexeme != Lexeme.SEMICOLON) {
                        throw new ParseException("expected ';'");
                    }
                    endIndex = pos;
                    Statement doLoop = new DoLoop(context, condition);
                    pos = doIndex;
                    parseBlock(doLoop);
                    pos = endIndex;
                    break;
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
        for (int i = pos; i < tokens.size(); i++) {
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

    private Statement parseStatement(List<Token<Lexeme>> tokens) throws ParseException {
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        for (int i = 0; i < tokens.size(); i++) {
            switch (tokens.get(i).lexeme) {
                case PRIVATE:
                    modifiers.add(Modifier.PRIVATE);
                    break;
                case STATIC:
                    modifiers.add(Modifier.STATIC);
                    break;
                case UNSAFE:
                    modifiers.add(Modifier.UNSAFE);
                    break;
                case CLASS:
                    return new TypeDeclaration(current, tokens.get(i + 1).contents, modifiers);
                case ELSE:
                    Statement elseStatement = new ElseStatement(current);
                    if (tokens.get(i + 1).lexeme == Lexeme.IF) {
                        Statement oldCurrent = current;
                        current = elseStatement;
                        parseStatement(tokens.subList(i + 1, tokens.size()));
                        current = oldCurrent;
                    }
                    return elseStatement;
                case IF:
                    return new IfStatement(current, getParentheticalExpression(tokens, i + 1));
                case REPEAT:
                    return new RepeatLoop(current, getParentheticalExpression(tokens, i + 1));
                case SWITCH:
                    return new SwitchStatement(current, getParentheticalExpression(tokens, i + 1));
                case WHILE:
                    return new WhileLoop(current, getParentheticalExpression(tokens, i + 1));
                case FOR:
                    // this one is a bit trickier, since there are three expressions
            }
        }
    }

    private Expression getParentheticalExpression() throws ParseException {
        if (tokens.get(++pos).lexeme != Lexeme.LEFT_PAREN) {
            throw new ParseException("expected '('");
        }
        List<Token<Lexeme>> expressionTokens = tokens.subList(pos, tokens.size());
        List<Expression> expressions = new ExpressionParser().parseExpression(expressionTokens);
        if (expressions.size() > 1) {
            throw new ParseException("unexpected ','");
        } else if (expressions.isEmpty()) {
            throw new ParseException("expected expression");
        } else {
            return expressions.get(0);
        }
    }
}
