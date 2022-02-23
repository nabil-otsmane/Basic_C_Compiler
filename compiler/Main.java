package compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import compiler.core.lexer.Lexer;
import compiler.core.lexer.Pattern;

public class Main {
    
    public static void main(String[] args) {
        try {
            Scanner f = new Scanner(new File("examples/test.cc"));

            StringBuilder sb = new StringBuilder();

            while(f.hasNext()) {
                sb.append(f.nextLine());
            }

            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern("number", "[0-9]+(\\.[0-9]*)?"));
            patterns.add(new Pattern("keyword", "(int|float|if|for|while|else)$"));
            patterns.add(new Pattern("identifier", "[a-zA-Z_][a-zA-Z0-9_]*"));
            patterns.add(new Pattern("operator", "=|\\+|-|\\*|/|<=|>=|<|>|!="));
            patterns.add(new Pattern("separator", ";|,|\\(|\\)|\\{|\\}"));

            Lexer lexer = new Lexer(sb.toString(), patterns);

            System.out.println(lexer.tokenize());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
