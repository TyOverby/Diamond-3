package parser;

import com.google.common.collect.Lists;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class DiamondParser {
    private Statement current;

    public DiamondParser() {
    }

    public CompilationUnit parse(List<Token<Lexeme>> tokens) throws ParseException {
        CompilationUnit compilationUnit = new CompilationUnit();
        current = compilationUnit;

        List<Token<Lexeme>> nextNode = Lists.newArrayList();
        for (Token<Lexeme> token : tokens) {
            switch (token.lexeme) {
                // TODO: do-while needs to be handled in this switch statement
                case LEFT_BRACE:
                    current = parseStatement(nextNode);
                    nextNode.clear();
                    break;
                case RIGHT_BRACE:
                    if (!nextNode.isEmpty()) {
                        throw new ParseException("expected ';' or '{'");
                    }
                    current = current.getParent();
                    nextNode.clear();
                    break;
                case SEMICOLON:
                    // TODO: return and delete have to be dealt with here, since they aren't expressions
                    List<Expression> expressions = new ExpressionParser().parseExpression(nextNode);
                    if (expressions.size() > 1) {
                        throw new ParseException("expected ';'");
                    } else if (!expressions.isEmpty()) {
                        expressions.get(0).attach(current);
                    }
                    nextNode.clear();
                    break;
            }
        }

        if (current != compilationUnit) {
            throw new ParseException("expected '}'");
        }

        return compilationUnit;
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
