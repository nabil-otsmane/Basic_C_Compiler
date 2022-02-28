package compiler.core.lexer;

public class Token {

    private String kind;
    private String value;

    public Token(String kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    public String getKind() {
        return kind;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        Token t = (Token)obj;
        return kind == t.kind && value == t.value;
    }

    @Override
    public String toString() {
        return "( "+ kind + ", " + value + " )";
    }
    
}
