package parser;

import com.google.common.collect.Lists;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class DiamondParser {
    public DiamondParser() {
    }

    public CompilationUnit parse(List<Token<Lexeme>> tokens) throws ParseException {
        CompilationUnit compilationUnit = new CompilationUnit();
        for (int i = 0; i < tokens.size(); i++) {
            i = parse(tokens, i, compilationUnit);
        }
        return compilationUnit;
    }

    private int parse(List<Token<Lexeme>> tokens, int pos, Statement context) throws ParseException {
        List<Token<Lexeme>> buffer = Lists.newArrayList();
        Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        for (int i = pos; i < tokens.size(); i++) {
            Token<Lexeme> token = tokens.get(i);
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
                    Statement statement = new TypeDeclaration(context, tokens.get(++i).contents, modifiers);
                    if (tokens.get(++i).lexeme != Lexeme.LEFT_BRACE) {
                        throw new ParseException("expected '{'");
                    }
                    return parse(tokens, i, statement);
                case RIGHT_BRACE:
                    if (!buffer.isEmpty() || !modifiers.isEmpty()) {
                        throw new ParseException("expected ';' or '{'");
                    }
                    return (i + 1);
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
                    return (i + 1);
                case LEFT_BRACE:
                    throw new ParseException("expected type declaration, if/else, or loop");
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

    private Expression getParentheticalExpression(List<Token<Lexeme>> tokens, int index) throws ParseException {
        if (tokens.get(index).lexeme != Lexeme.LEFT_PAREN) {
            throw new ParseException("expected '('");
        }
        List<Token<Lexeme>> expressionTokens = tokens.subList(index, tokens.size());
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
