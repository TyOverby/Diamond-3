package lexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lexer.Lexer.RuleGroup;



/**
 * User: Ty
 * Date: 5/11/12
 * Time: 7:48 PM
 */
public class DiamondLexer {
    private static enum Lexeme{
        // Keywords
        REPEAT,
        ABSTRACT,
        BOOLEAN,
        BREAK,
        BYTE,
        CASE,
        CATCH,
        CLASS,
        CONTINUE,
        DEFAULT,
        DELETE,
        DOUBLE,
        DO,
        ELSE,
        ENUM,
        EXTENDS,
        FINAL,
        FINALLY,
        FLOAT,
        FOR,
        GOTO,
        IF,
        IMPLEMENTS,
        INSTANCEOF,
        INT,
        INTERFACE,
        LONG,
        NEW,
        PRIVATE,
        PROTECTED,
        RAISE,
        RETURN,
        SHORT,
        STATIC,
        SUPER,
        SWITCH,
        THIS,
        THROW,
        THROWS,
        TRY,
        UNSAFE,
        VOID,
        WHILE,
        YIELD,

        // Delimiters
        LEFT_BRACE,
        RIGHT_BRACE,

        LEFT_BRACKET,
        RIGHT_BRACKET,

        LEFT_PAREN,
        RIGHT_PAREN,

        SEMICOLON,
        PERIOD,
        COMMA,

        // Operators
        ASSIGNMENT,
        GREATER_THAN,
        LESS_THAN,
        NOT,
        CONDITIONAL,
        EQUALITY,
        LESS_THAN_EQUALS,
        GREATER_THAN_EQUALS,
        NOT_EQUALS,
        LOGICAL_AND,
        LOGICAL_OR,
        PLUS_PLUS,
        MINUS_MINUS,
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
        BITWISE_AND,
        BITWISE_OR,
        BITWISE_XOR,
        MODULO,
        SHIFT_LEFT,
        SHIFT_RIGHT,
        PLUS_EQUALS,
        MINUS_EQUALS,
        TIMES_EQUALS,
        DIVIDE_EQUALS,
        BITWISE_AND_EQUALS,
        BITWISE_OR_EQUALS,
        BITWISE_XOR_EQUALS,
        MODULO_EQUALS,
        SHIFT_LEFT_EQUALS,
        SHIFT_RIGHT_EQUALS,

        TERNARY_QUESTION,
        TERNARY_COLON,

        // OTHER
        NUMBER,
        COMMENT,
        IDENTIFIER,
        WHITESPACE,

    }

    private Lexer<Lexeme> lexer = new Lexer<Lexeme>();

    public DiamondLexer(){
        this.setupRules();
    }

    public List<Token<Lexeme>> lex(File file) throws IOException {
        List<Token<Lexeme>> toReturn = lexer.lex(file);

        Iterator<Token<Lexeme>> i = toReturn.iterator();
        for(Token<Lexeme>token=i.next();i.hasNext();token = i.next()){
            if(token.lexeme==Lexeme.WHITESPACE){
                i.remove();

            }
            System.out.println("looping.");
        }

        return toReturn;
    }
    public List<Token<Lexeme>> lex(String input) throws IOException {
        List<Token<Lexeme>> toReturn = lexer.lex(input);

        Iterator<Token<Lexeme>> i = toReturn.iterator();
        for(Token<Lexeme>token=i.next();i.hasNext();token = i.next()){
            if(token.lexeme==Lexeme.WHITESPACE){
                i.remove();
            }
        }
        return toReturn;
    }

    /**
     * Sets up the default ruleset and prepares the regular expressions for DiamondLang
     */
    private void setupRules(){
        List<Lexer.RuleGroup<Lexeme>> ruleGroups = new ArrayList<Lexer.RuleGroup<Lexeme>>();
        List<Lexer.RuleGroup<Lexeme>> rg = ruleGroups;

        add(rg,Lexeme.REPEAT,       "repeat",true);
        add(rg,Lexeme.ABSTRACT,     "abstract", true);
        add(rg,Lexeme.BOOLEAN,      "boolean", true);
        add(rg,Lexeme.BREAK,        "break", true);
        add(rg,Lexeme.BYTE,         "byte", true);
        add(rg,Lexeme.CASE,         "case:", true);
        add(rg,Lexeme.CATCH,        "catch", true);
        add(rg,Lexeme.CLASS,        "class", true);
        add(rg,Lexeme.CONTINUE,     "continue", true);
        add(rg,Lexeme.DEFAULT,      "default", true);
        add(rg,Lexeme.DELETE,       "delete", true);
        add(rg,Lexeme.DOUBLE,       "double", true);
        add(rg,Lexeme.DO,           "do", true);
        add(rg,Lexeme.ELSE,         "else", true);
        add(rg,Lexeme.ENUM,         "enum", true);
        add(rg,Lexeme.EXTENDS,      "extends", true);
        add(rg,Lexeme.FINAL,        "final", true);
        add(rg,Lexeme.FINALLY,      "finally", true);
        add(rg,Lexeme.FLOAT,        "float", true);
        add(rg,Lexeme.FOR,          "for", true);
        add(rg,Lexeme.GOTO,         "goto", true);
        add(rg,Lexeme.IF,           "if", true);
        add(rg,Lexeme.IMPLEMENTS,   "implements", true);
        add(rg,Lexeme.INSTANCEOF,   "instanceof", true);
        add(rg,Lexeme.INT,          "int", true);
        add(rg,Lexeme.INTERFACE,    "interface", true);
        add(rg,Lexeme.LONG,         "long", true);
        add(rg,Lexeme.NEW,          "new", true);
        add(rg,Lexeme.PRIVATE,      "private", true);
        add(rg,Lexeme.PROTECTED,    "protected", true);
        add(rg,Lexeme.RAISE,        "raise", true);
        add(rg,Lexeme.RETURN,       "return", true);
        add(rg,Lexeme.SHORT,        "short", true);
        add(rg,Lexeme.STATIC,       "static", true);
        add(rg,Lexeme.SUPER,        "super", true);
        add(rg,Lexeme.SWITCH,       "switch", true);
        add(rg,Lexeme.THIS,         "this", true);
        add(rg,Lexeme.THROW,        "throw", true);
        add(rg,Lexeme.THROWS,       "throws", true);
        add(rg,Lexeme.TRY,          "try", true);
        add(rg,Lexeme.UNSAFE,       "unsafe", true);
        add(rg,Lexeme.VOID,         "void", true);
        add(rg,Lexeme.WHILE,        "while", true);
        add(rg,Lexeme.YIELD,        "yield",true);

        add(rg,Lexeme.LEFT_BRACE,   "\\{");
        add(rg,Lexeme.RIGHT_BRACE,  "\\}");

        add(rg,Lexeme.LEFT_BRACKET, "\\[");
        add(rg,Lexeme.RIGHT_BRACKET,"\\]");

        add(rg,Lexeme.LEFT_PAREN,   "\\(");
        add(rg,Lexeme.RIGHT_PAREN,  "\\)");

        add(rg,Lexeme.SEMICOLON,    ";");
        add(rg,Lexeme.PERIOD,       "\\.");
        add(rg,Lexeme.COMMA,        ",");

        add(rg,Lexeme.ASSIGNMENT,   "=");
        add(rg,Lexeme.GREATER_THAN, ">");
        add(rg,Lexeme.LESS_THAN,    "<");
        add(rg,Lexeme.NOT,          "!");
        add(rg,Lexeme.TERNARY_QUESTION, "\\?");
        add(rg,Lexeme.TERNARY_COLON,    ":");
        add(rg,Lexeme.EQUALITY, "==");
        add(rg,Lexeme.LESS_THAN_EQUALS, "<=");
        add(rg,Lexeme.GREATER_THAN_EQUALS, ">=");
        add(rg,Lexeme.NOT_EQUALS, "!=");
        add(rg,Lexeme.LOGICAL_AND, "&&");
        add(rg,Lexeme.LOGICAL_OR, "\\|\\|");
        add(rg,Lexeme.PLUS_PLUS, "\\+\\+");
        add(rg,Lexeme.MINUS_MINUS, "\\-\\-");
        add(rg,Lexeme.PLUS, "\\+");
        add(rg,Lexeme.MINUS, "\\-");
        add(rg,Lexeme.TIMES, "\\*");
        add(rg,Lexeme.DIVIDE, "(/)[^/]");
        add(rg,Lexeme.BITWISE_AND, "&");
        add(rg,Lexeme.BITWISE_OR, "\\|");
        add(rg,Lexeme.BITWISE_XOR, "\\^");
        add(rg,Lexeme.MODULO, "%");
        add(rg,Lexeme.SHIFT_LEFT, "<<");
        add(rg,Lexeme.SHIFT_RIGHT, ">>");
        add(rg,Lexeme.PLUS_EQUALS, "\\+=");
        add(rg,Lexeme.MINUS_EQUALS, "\\-=");
        add(rg,Lexeme.TIMES_EQUALS, "\\*=");
        add(rg,Lexeme.DIVIDE_EQUALS, "/=");
        add(rg,Lexeme.BITWISE_AND_EQUALS, "&=");
        add(rg,Lexeme.BITWISE_OR_EQUALS, "\\|=");
        add(rg,Lexeme.BITWISE_XOR_EQUALS, "\\^=");
        add(rg,Lexeme.MODULO_EQUALS, "%=");
        add(rg,Lexeme.SHIFT_LEFT_EQUALS, "<<=");
        add(rg,Lexeme.SHIFT_RIGHT_EQUALS, ">>=");

        add(rg,Lexeme.COMMENT,    "//[^\\n]*");
        add(rg,Lexeme.IDENTIFIER, "[a-zA-Z]+\\w*");
        add(rg,Lexeme.NUMBER,     "\\d+");
        add(rg,Lexeme.WHITESPACE, " +");
        add(rg,Lexeme.WHITESPACE, "\n+");
        add(rg,Lexeme.WHITESPACE, "\t+");

        this.lexer.setupRules(ruleGroups);
    }

    private static void add(List<Lexer.RuleGroup<Lexeme>> ruleGroups,Lexeme lexeme, String pattern){
        add(ruleGroups,lexeme,pattern,false);
    }
    // Just an alias for a previously long-to-type expression.  Also handles the adding of
    private static void add(List<Lexer.RuleGroup<Lexeme>> ruleGroups,Lexeme lexeme, String pattern, boolean isWord){
        // If the pattern is a word, it adds the correct parens and adds the
        // "cannot be followed by a letter or number" qualifier
        if(isWord){
           // Chill the fuck out.  That + sign will be optimized away
           pattern = "("+pattern+")"+"[^\\w]";
        }
        ruleGroups.add(new RuleGroup<Lexeme>(lexeme,pattern));
    }


    public static void main(String... args) throws IOException {
        File input = new File("test.dmd");

        DiamondLexer lexer = new DiamondLexer();
        lexer.setupRules();
        List<Token<Lexeme>> tokens = lexer.lex(input);

        for(Token t: tokens){
            System.out.println(t.lexeme+"  "+(t.contents.equals("\n")?"\\n":t.contents));
        }
    }
}
