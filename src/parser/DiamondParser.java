package parser;

import com.google.common.collect.Lists;
import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.List;

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
                    for (Expression expression : new ExpressionParser().parseExpression(nextNode)) {
                        expression.attach(current);
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

    private Statement parseStatement(List<Token<Lexeme>> statement) {
    }
}
