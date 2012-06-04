package lexer;

/**
 * User: Ty
 * Date: 5/6/12
 * Time: 8:05 PM
 */
public class Token<E extends Enum<E>> {
    public final E lexeme;
    public final String contents;
    
    public Token(E lexeme, String contents){
        this.lexeme = lexeme;
        this.contents = contents;
    }
}
