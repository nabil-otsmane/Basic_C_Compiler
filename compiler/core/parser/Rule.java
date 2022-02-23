package compiler.core.parser;

public class Rule {

    private String leftSide;
    private String[] rightSide;

    public Rule(String leftSide, String[] rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public String[] getRightSide() {
        return rightSide;
    }

    @Override
    public String toString() {
        String s = leftSide + " -> ";
        for (String r: rightSide) {
            s += " " + r;
        }

        return s;
    }
    
}
