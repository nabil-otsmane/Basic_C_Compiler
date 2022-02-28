package compiler.core.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import compiler.core.lexer.Token;

public class Parser {

    private Grammar grammar;
    private Map<String, Map<String, Rule>> parsingTable;
    private List<Rule> output;
    

    public Parser(Grammar g) throws Exception {
        output = new ArrayList<>();
        grammar = g;

        // if (!isLL1(g))
        //     System.out.println("grammar is not LL1");

        createParsingTable();
    }

    private boolean isLL1(Grammar g) {
        Map<String, List<Rule>> rules = g.getRulesByVariable();
        boolean isLL = true;

        for (String variable: rules.keySet()) {
            List<Set<String>> firsts = new ArrayList<>();
            for (Rule r: rules.get(variable)) {
                firsts.add(first(r));
            }

            for (int i = 0; i < firsts.size() - 1; i++) {
                for (int j = i+1; j < firsts.size(); j++) {
                    // first check: For no terminal a do both i and j derive strings beginning with a.
                    for (String a: firsts.get(i)) {
                        if (firsts.get(j).contains(a)) {

                            // System.out.println("error in these rules: " + rules.get(variable));
                            isLL = false;
                        }
                    }

                    // second check: At most one of i and j can derive the empty string
                    if (firsts.get(i).contains("epsilon") && firsts.get(j).contains("epsilon")) {
                        // System.out.println("error in these rules: " + rules.get(variable));

                        isLL = false;
                    }

                    // third check: if i derives epsilon, then j does not derive any string beginning with a terminal in FOLLOW(variable).
                    if (firsts.get(i).contains("epsilon")) {
                        for (String t: g.getFollowSets().get(variable)) {
                            if (firsts.get(j).contains(t)) {
                            // System.out.println("error in these rules: " + rules.get(variable));


                            isLL = false;
                            }
                        }
                    }

                    // forth check: if j derives epsilon, then i2 does not derive any string beginning with a terminal in FOLLOW(variable).
                    if (firsts.get(j).contains("epsilon")) {
                        for (String t: g.getFollowSets().get(variable)) {
                            if (firsts.get(i).contains(t)) {
                            // System.out.println("error in these rules: " + rules.get(variable));
                                
                            isLL = false;
                            }
                        }
                    }
                    
                }
            }
        }

        return isLL;
    }

    private void createParsingTable() {
        parsingTable = new HashMap<>();

        for (String variable: grammar.getVariables()) {
            parsingTable.put(variable, new HashMap<>());
        }

        for (Rule r: grammar.getRules()) {

            Set<String> firstSet = first(r);

            if (firstSet.contains("epsilon")) {
                for (String s: grammar.getFollowSets().get(r.getLeftSide())) {
                    if (!parsingTable.get(r.getLeftSide()).containsKey(s)) {
                        parsingTable.get(r.getLeftSide()).put(s, r);
                    } else {
                        System.out.println(r);
                    }
                }

                firstSet.remove("epsilon");
            }
            for (String a: firstSet) {
                if (!parsingTable.get(r.getLeftSide()).containsKey(a))
                    parsingTable.get(r.getLeftSide()).put(a, r);
                else {
                    System.out.println(r);
                }
            }
        }
    }

    private Set<String> first(Rule r) {
        Set<String> firstSet = new HashSet<>();
        
        for (int i = 0; i < r.getRightSide().length; i++) {
            String s = r.getRightSide()[i];
            
            if (rightInTokens(grammar.getTerminals(), s) || s.equals("epsilon")) {
                firstSet.add(s);
                break;
            }

            // System.out.println("firstSet: " + grammar.getFirstSets());
            // System.out.println("sign: " + s);
            Set<String> first = grammar.getFirstSets().get(s);
            // System.out.println(first);
            firstSet.addAll(first);
            if (!first.contains("epsilon")) {
                break;
            } else if (i != r.getRightSide().length - 1) {
                firstSet.remove("epsilon");
            }
        }

        return firstSet;
    }

    public Map<String, Map<String, Rule>> getParsingTable() {
        return parsingTable;
    }

    public boolean correct(List<Token> tokens) {
        Stack<String> stack = new Stack<>();
        int id = 0;

        stack.push("$");
        stack.push(grammar.getStartVariable());

        while (!stack.peek().equals("$")) {

            if (stack.peek().equals(tokens.get(id).getKind()) || stack.peek().equals(tokens.get(id).getValue())) {
                stack.pop();
                id++;
            } else if (rightInTokens(grammar.getTerminals(), stack.peek())) {
                System.out.println("1.Stack: " + stack);
                System.out.println("1.current index is: " + id);
                return false;
            } else if (!parsingTable.get(stack.peek()).containsKey(tokens.get(id).getValue()) && !parsingTable.get(stack.peek()).containsKey(tokens.get(id).getKind())) {
                System.out.println("2.Stack: " + stack);
                System.out.println("2.current index is: " + id);
                return false;
            } else {
                Rule r;
                
                if (!tokens.get(id).getKind().equals("identifier") && !tokens.get(id).getKind().equals("number")) {
                    r = parsingTable.get(stack.peek()).get(tokens.get(id).getValue());
                } else {
                    r = parsingTable.get(stack.peek()).get(tokens.get(id).getKind());
                }

                if (r == null) {
                    return false;
                }

                output.add(r);
                stack.pop();
                String[] right = r.getRightSide();
                for (int j = right.length - 1; j >= 0; j--) {
                    if (!right[j].equals("epsilon"))
                        stack.push(right[j]);
                }
            }
        }

        return true;
    }

    public List<Rule> getOutput() {
        return output;
    }

    private boolean rightInTokens(Collection<Token> tokens, String right) {

        for (Token t: tokens) {
            if (t.getKind().equals(right) || t.getValue().equals(right)) {
                return true;
            }
        }

        return false;
    }
    
}
