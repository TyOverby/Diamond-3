package parser;

import lexer.DiamondLexer.Lexeme;
import lexer.Token;

import java.util.List;

public final class DiamondParser {
    private Node current;

    public DiamondParser() {
    }

    public CompilationUnit parse(List<Token<Lexeme>> tokens) {
        CompilationUnit compilationUnit = new CompilationUnit();
        current = compilationUnit;

        for (Token<Lexeme> token : tokens) {
            switch (token.lexeme) {
                // do stuff here
                default:
                    throw new UnsupportedOperationException("unknown token");
            }
        }

        return compilationUnit;
    }
}
