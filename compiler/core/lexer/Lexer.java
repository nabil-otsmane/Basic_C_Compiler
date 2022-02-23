package compiler.core.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private List<Pattern> matcher;
    private String[] input;

    public Lexer(String input, List<Pattern> matcher) {

        this.matcher = matcher;

        this.input = input.split("\s");
    }

    public List<Token> tokenize() throws Exception {

        List<Token> tokens = new ArrayList<>();

        for (String word: input) {
            if (word.isEmpty())
                continue;

            while (!word.isEmpty()) {
                boolean found = false;
                for (Pattern pattern: matcher) {
                    Matcher match = pattern.compile().matcher(word);
                    
                    if (match.find() && match.start() == 0) {
                        tokens.add(new Token(pattern.getKind(), word.substring(0, match.end())));

                        found = true;
                        word = word.substring(match.end());
                    }
                }
                if (!found) {
                    throw new Exception("no match found for the string: " + word);
                }
            }
        }

        return tokens;
    }
}
