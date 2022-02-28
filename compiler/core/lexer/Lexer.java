package compiler.core.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private List<Pattern> matcher;
    private String input;
    private int ind;

    public Lexer(String input, List<Pattern> matcher) {
        this.matcher = matcher;

        this.input = input;
        this.ind = 0;
    }

    private boolean isEOT() {
        return ind >= input.length();
    }

    private Token nextToken() throws Exception {
        for (Pattern pattern: matcher) {
            Matcher match = pattern.compile().matcher(input);

            if (match.find(ind) && match.start() == ind) {
                int tmp = ind;
                ind = match.end();

                return new Token(pattern.getKind(), input.substring(tmp, ind).trim());
            }
        }

        throw new Exception("no match found for the string: " + input + " at index: " + ind);
        
    }

    private void skipWhitespaces() {

        while (!isEOT()) {
            if (input.charAt(ind) == ' ' || input.charAt(ind) == '\t' || input.charAt(ind) == '\n') {
                ind++;
            } else {
                break;
            }
        }
    }

    public List<Token> tokenize() throws Exception {

        List<Token> tokens = new ArrayList<>();

        while (!isEOT()) {
            skipWhitespaces();

            tokens.add(nextToken());
        }

        return tokens;
    }
}
