package compiler.core.lexer;

public class Token {
    public enum TokenKind { Identifier, Keyword, Separator, Operator, Number };

    private TokenKind kind;
    private String value;

    public Token(TokenKind kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public String toString() {
        return "( "+ kind + ", " + value + " )";
    }
    
}
