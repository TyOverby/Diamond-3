package lexer;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer<E extends Enum<E>>{
    /**
     * Each RuleGroup contains a lexeme, the basic regular expression and a compiled
     * regular expression.
     * You can think of the lexeme as the Key, with both regexes being mapped to that key.
     *
     * When the regex is compiled, it checks to see if it is already part of a regex group.
     * If it is not, a regex group is added to it wrapping the whole thing.
     */
    public static class RuleGroup<E extends Enum<E>>{
        public final E lexeme;
        // Keep track of the regex for testing purposes
        private final String regex;
        public final Pattern compiledRegex;
        
        public RuleGroup(E lexeme, String regex){
            this.lexeme = lexeme;
            if(regex.contains("(")&& !regex.contains("\\(")){
                this.regex = regex;
            }
            else{
                this.regex = "("+regex+")";
            }

            this.compiledRegex  = Pattern.compile(this.regex);
        }
    }

    private List<RuleGroup<E>> ruleGroups = new ArrayList<RuleGroup<E>>();

    /**
     * Pass in your own RuleGroups in here.
     * @param rules The rules to be compiled and lexed.
     */
    public void setupRules(LinkedHashMap<E,String> rules){
        if(ruleGroups.size()>0){
            throw new RuntimeException("The ruleGroups are already populated.  Can not setup the rules again");
        }
        for(E lexeme: rules.keySet()){
            ruleGroups.add(new RuleGroup<E>(lexeme,rules.get(lexeme)));

        }
    }

    public void setupRules(List<RuleGroup<E>> ruleGroups){
        this.ruleGroups = ruleGroups;
    }

    /**
     * Reads the contents of the specified file as an ASCII string, which is then passed to {@link #lex(String)}.
     *
     * @param filePath the file to lex
     * @return the list of tokens that were lexed
     * @throws IOException if the file could not be found or read
     * @see #lex(String)
     */
    public List<Token<E>> lex(File filePath) throws IOException {
        Readable reader = new InputStreamReader(new FileInputStream(filePath), Charsets.US_ASCII);
        return lex(CharStreams.toString(reader));
    }

    /**
     * Given an input string, the lexer goes through each regex finding ones that start at the
     * current head of the string.  If one matches, it trims the string from that point and starts again
     * If no regexes match, an error is printed, and the already processed ones are returned.
     *
     * @param input the string to lex
     * @return the list of tokens that were lexed
     */
    public List<Token<E>> lex(String input){
        // The original input is preserved for error checking.
        String origInput = input;

        input = input+" ";
        List<Token<E>> tokens = new ArrayList<Token<E>>();
        
        int globalPos=0;
        //int curPos=0;
        boolean working = true;
        
        while (working){
            boolean couldBeMatched = false;
            // Go through every regex in the rule groups
            for (RuleGroup<E> rg : ruleGroups){
                Matcher matcher = rg.compiledRegex.matcher(input);

                // If a match has been found
                if(matcher.find()){
                    // Check to see if the start position is at the head of the file.
                    int start = matcher.start(1);
                    int end = matcher.end(1);
                    if(start==0){
                        // If it is, make a new token with the provided lexeme and the text
                        tokens.add(new Token<E>(rg.lexeme,matcher.group(1)));
                        // Shift the head of the string to the end of the group.
                        input = shiftString(input,end);
                        // And advance the global position to the end for use in debugging.
                        globalPos+=end;
                        couldBeMatched = true;
                        break;
                    }
                }
                else{
                    if(input.length()==0){
                        return tokens;
                    }
                }
            }
            if(!couldBeMatched){
                System.out.println("["+ origInput.charAt(globalPos)+"]");
                System.err.println(getHumanPosition(origInput,globalPos));
                return tokens;
            }
        }

        return tokens;
    }

    private static String getHumanPosition(String sourceString, int globalCharPosition){
        char[] chars = sourceString.toCharArray();
        int lineNumber=1;
        int charPosition=1;
        for(int i=0;i<globalCharPosition;i++){
            char c = chars[i];
            if(c=='\n'){
                lineNumber++;
                charPosition=1;
            }
            charPosition++;
        }

        return "Unknown symbol or pattern on line: "+lineNumber+", character: "+charPosition+" near symbol: '"+chars[globalCharPosition]+"'.";
    }
    
    private static String shiftString(String input, int shiftLength){
        return input.subSequence(shiftLength,input.length()).toString();
    }
}
