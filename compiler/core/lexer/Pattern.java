package compiler.core.lexer;

public class Pattern {

    private String kind;
    private String pattern;

    public Pattern(String kind, String pattern) {
        this.kind = kind;
        this.pattern = pattern;
    }

    public java.util.regex.Pattern compile() {
        return java.util.regex.Pattern.compile(pattern);
    } 

    public String getKind() {
        return kind;
    }
    
}
