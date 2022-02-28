package compiler.core.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import compiler.core.lexer.Lexer;
import compiler.core.lexer.Pattern;
import compiler.core.lexer.Token;

public class Grammar {

    private Set<Token> terminals;
    private Set<String> variables;
    private List<Rule> rules;
    private String startVariable;
    private List<Pattern> patterns;

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    
    public Grammar(String grammar, List<Pattern> patterns) throws Exception {
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        variables = new HashSet<>();
        int line = 0;
        String[] lines = grammar.split("\n");
        this.patterns = patterns;

        Set<String> tmpTerminals = new HashSet<>();

        for (String s: lines) {
            String[] sides = s.split("->");

            String left = sides[0].trim();
            variables.add(left);

            String[] rightRules = sides[1].split("\\|");
            for (String rightRule: rightRules) {
                String[] terms = rightRule.trim().split("\\s+");

                for (String term: terms) {
                    if (!term.equals("epsilon"))
                    tmpTerminals.add(term);
                }

                if (line == 0) {
                    startVariable = left;
                }
                rules.add(new Rule(left, terms));
                line++;
            }
        }

        tmpTerminals.removeAll(variables);

        for (String terminal: tmpTerminals) {

            
            Lexer l = new Lexer(terminal, patterns);
            List<Token> tokens = l.tokenize();

            if (tokens.size() != 1) {
                throw new Exception("invalid terminal \""+ terminal +"\"");
            }

            terminals.add(tokens.get(0));
        }

        firstSets();
        computeFollowSet();

    }

    private void firstSets() {
        firstSets = new HashMap<>();

        for (String s : variables) {

            Set<String> temp = new HashSet<>();
            firstSets.put(s, temp);
        }
        while (true) {
            boolean isChanged = false;
            for (String variable : variables) {
                Set<String> firstSet = new HashSet<>();
                for (Rule rule : rules) {
                    if (rule.getLeftSide().equals(variable)) {
                        Set<String> addAll = computeFirst(rule.getRightSide(), 0);
                        firstSet.addAll(addAll);
                    }
                }
                if (!firstSets.get(variable).containsAll(firstSet)) {
                    isChanged = true;
                    firstSets.get(variable).addAll(firstSet);
                }

            }
            if (!isChanged) {
                break;
            }
        }
    }

    public Set<String> computeFirst(String[] string, int index) {
        Set<String> first = new HashSet<>();
        if (index == string.length) {
            return first;
        }


        for (Token t: terminals) {
            if (t.getValue().equals(string[index]) || t.getKind().equals(string[index])) {
                first.add(string[index]);
                return first;
            }
        }

        if (string[index].equals("epsilon")) {
            first.add(string[index]);
            return first;
        }

        if (variables.contains(string[index])) {
            for (String str : firstSets.get(string[index])) {
                first.add(str);
            }
        }

        if (first.contains("epsilon")) {
            if (index != string.length - 1) {
                first.remove("epsilon");
                first.addAll(computeFirst(string, index + 1));
            }
        }
        return first;
    }

    private void computeFollowSet() {
        followSets = new HashMap<>();
        for (String s : variables) {
            Set<String> temp = new HashSet<>();
            followSets.put(s, temp);
        }
        Set<String> start = new HashSet<>();
        start.add("$");
        followSets.put("S'", start);

        while (true) {
            boolean isChange = false;
            for (String variable : variables) {
                for (Rule rule : rules) {
                    for (int i = 0; i < rule.getRightSide().length; i++) {
                        if (rule.getRightSide()[i].equals(variable)) {
                            Set<String> first;
                            if (i == rule.getRightSide().length - 1) {
                                first = followSets.get(rule.getLeftSide());
                            } else {
                                first = computeFirst(rule.getRightSide(), i + 1);
                                if (first.contains("epsilon")) {
                                    first.remove("epsilon");
                                    first.addAll(followSets.get(rule.getLeftSide()));
                                }
                            }
                            if (!followSets.get(variable).containsAll(first)) {
                                isChange = true;
                                followSets.get(variable).addAll(first);
                            }
                        }
                    }
                }
            }
            if (!isChange) {
                break;
            }
        }
    }


    public String getStartVariable() {
        return startVariable;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Map<String, List<Rule>> getRulesByVariable() {
        Map<String, List<Rule>> rules = new HashMap<>();

        for (String var: variables) {
            rules.put(var, new ArrayList<>());
        }

        for (Rule r : this.rules) {
            rules.get(r.getLeftSide()).add(r);
        }

        return rules;
    }

    public Set<Token> getTerminals() {
        return terminals;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }

    public Map<String, Set<String>> getFollowSets() {
        return followSets;
    }

    @Override
    public String toString() {
        String s = "";

        for (Rule r: rules) {
            s += r + "\n";
        }

        return s;
    }
    
}
