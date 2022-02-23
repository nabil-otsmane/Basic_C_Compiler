package compiler.core.lexer;

public class Token {

    private String kind;
    private String value;

    public Token(String kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public String toString() {
        return "( "+ kind + ", " + value + " )";
    }
    
}
