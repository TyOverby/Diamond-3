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
        // Start off listing the keywords
        WHILE, FOR, DO, LOOP, FUNCTION, CLASS,

        EQUALS, EQUALSEQUALS,

        // MATH
        UNSIGNED, SIGNED,
        PLUS, PLUSPLUS, PLUSEQUALS,
        MINUS, MINUSMINUS, MINUSEQUALS,
        TIMES, TIMESEQUALS,
        DIVIDE, DIVIDEQUALS,
        MODULO, MODULOEQUALS,

        // Other
        LEFTPAREN, RIGHTPAREN, LEFTBRACKET, RIGHTBRACKET, LEFTBRACE, RIGHTBRACE,
        STRING,
        COMMENT,
        IDENTIFIER,
        WHITESPACE,
        SEMICOLON,
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

        if(ruleGroups.size()>0){
            throw new RuntimeException("The ruleGroups are already populated.  Can not setup the rules again");
        }

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.WHILE,"(while)[^\\w]"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.FOR,"(for)[^\\w]"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.DO,"(do)[^\\w]"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.LOOP,"(loop)[^\\w]"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.FUNCTION,"(function)[^\\w]"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.CLASS,"(class)[^\\w]"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.COMMENT,"//[^\\n]*"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.SIGNED,"\\d+s"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.UNSIGNED,"\\d+u?"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.PLUSEQUALS,"\\+="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.PLUSPLUS,"\\+\\+"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.PLUS,"\\+"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.MINUSEQUALS,"-="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.MINUSMINUS,"--"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.MINUS,"-"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.TIMESEQUALS,"\\*="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.TIMES,"\\*"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.DIVIDEQUALS,"/="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.DIVIDE,"/"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.MODULOEQUALS,"%="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.MODULO,"%"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.LEFTPAREN,"\\("));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.RIGHTPAREN,"\\)"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.LEFTBRACE,"\\["));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.RIGHTBRACE,"\\]"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.LEFTBRACKET,"\\{"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.RIGHTBRACKET,"\\}"));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.STRING,"\"[^\"]+\""));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.EQUALSEQUALS,"=="));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.EQUALS,"="));

        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.IDENTIFIER,"[a-zA-Z]+\\w*"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.WHITESPACE," +"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.WHITESPACE,"\n+"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.WHITESPACE,"\t+"));
        //ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.WHITESPACE," +"));
        ruleGroups.add(new RuleGroup<Lexeme>(Lexeme.SEMICOLON,";"));

        this.lexer.setupRules(ruleGroups);
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
