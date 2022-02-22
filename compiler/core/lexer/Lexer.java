package compiler.core.lexer;

import java.util.ArrayList;
import java.util.List;

import compiler.core.lexer.Token.TokenKind;

public class Lexer {

    private String input;
    private int id;

    public Lexer(String input) {
        this.input = input;
        id = 0;
    }

    private boolean isEOT() {
        return id >= input.length();
    }

    private char c() throws Exception {
        if (isEOT()) {
            throw new Exception("End of input reached");
        }

        return input.charAt(id);
    }

    private char next() throws Exception {
        char c = c();
        id++;

        return c;
    }

    private void skipSpace() throws Exception {
        while (!isEOT() && Character.isWhitespace(c())) {
            next();
        }
    }

    private boolean isSeparator(char c) {
        return c == ',' || c == ';' || c == '{' || c == '}' || c == '(' || c == ')';
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isOperator(String c) {
        return c.equals("=") 
            || c.equals("+") 
            || c.equals("-") 
            || c.equals("*") 
            || c.equals("/") 
            || c.equals("<") 
            || c.equals("<=") 
            || c.equals(">") 
            || c.equals(">=")
            || c.equals("!="); 
    }

    private boolean isIdent(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean isKeyword(String word) {
        return word.equals("int") || word.equals("float") || word.equals("if") || word.equals("for") || word.equals("while") || word.equals("else");
    }

    private Token identifier() throws Exception {
        if (!(Character.isLetter(c()) && c() != '_'))
            throw new Exception("identifiers cannot start with is character: " + c());

        String s = "";
        while (!isEOT() && !Character.isWhitespace(c()) && isIdent(c())) {
            s += next();
        }

        return new Token(TokenKind.Identifier, s);
    }

    private Token keyword() throws Exception {
        String s = "";
        int rollback = id;
        while (!isEOT() && !Character.isWhitespace(c())) {
            s += next();
        }

        if (isKeyword(s))
        {
            return new Token(TokenKind.Keyword, s);
        } else {
            id = rollback;
            return null;
        }
    }

    private Token separator() throws Exception {
        if (isEOT() || !isSeparator(c()))
            throw new Exception(c() + " is not a separator");

        return new Token(TokenKind.Separator, String.valueOf(next()));
    }

    private Token operator() throws Exception {
        String s = String.valueOf(next());
        
        if (!isOperator(s))
            throw new Exception(s + " is not an operator");

        try {
            String combined = s+next();
            if (isOperator(combined)) {
                return new Token(TokenKind.Operator, combined);
            }
        } finally {
            id--;
        }

        return new Token(TokenKind.Operator, s);

    }

    private Token number() throws Exception {
        String s = "";

        while (!isEOT() && isDigit(c())) {
            s += next();
        }
        return new Token(TokenKind.Number, s);
    }

    private Token nextToken() throws Exception {
        skipSpace();

        if (isEOT()) {
            return null;
        } else if (isDigit(c())) {
            return number();
        } else if (isOperator(String.valueOf(c()))) {
            return operator();
        } else if (isSeparator(c())) {
            return separator();
        } else if (Character.isLetter(c())) {
            Token keyword = keyword();
            if (keyword != null) {
                return keyword;
            } else {
                return identifier();
            }
        } else if (c() == '_') {
            return identifier();
        } else {
            throw new Exception("not a character for token: " + c() + " at index: " + id);
        }
    }
    
    public List<Token> tokenize() throws Exception {
        List<Token> tokens = new ArrayList<>();

        Token t;
        while ((t = nextToken()) != null) {
            tokens.add(t);
        }

        return tokens;
    }
    
}
