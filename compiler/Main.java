package compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import compiler.core.lexer.Lexer;
import compiler.core.lexer.Pattern;
import compiler.core.lexer.Token;
import compiler.core.parser.Grammar;
import compiler.core.parser.Parser;

public class Main {
    
    public static void main(String[] args) {

        List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern("number", "[0-9]+(\\.[0-9]*)?"));
            patterns.add(new Pattern("keyword", "(int|float|if|for|while|else)\s"));
            patterns.add(new Pattern("identifier", "[a-zA-Z_][a-zA-Z0-9_]*"));
            patterns.add(new Pattern("operator", "=|\\+|-|\\*|/|<=|>=|<|>|!="));
            patterns.add(new Pattern("separator", ";|,|\\(|\\)|\\{|\\}"));

        String[] rules = new String[]{
            "Function -> Type identifier ( ArgList ) CompoundStmt",
            "ArgList -> Type identifier ArgList'",
            "ArgList' -> , Arg ArgList'|epsilon",
            "Arg -> Type identifier",
            "Declaration -> Type IdentList ;",
            "Type -> int|float",
            "IdentList -> identifier IdentList'",
            "IdentList' -> , IdentList|epsilon",
            "Stmt -> ForStmt|WhileStmt|Expr ;|CompoundStmt|Declaration|IfStmt|;",
            "StmtList -> Stmt StmtList|epsilon",
            "ForStmt -> for ( Expr ; OptExpr ; OptExpr ) Stmt",
            "OptExpr -> Expr|epsilon",
            "WhileStmt -> while ( Expr ) Stmt",
            "IfStmt -> if ( Expr ) Stmt ElsePart",
            "ElsePart -> else Stmt|epsilon",
            "CompoundStmt -> { StmtList }",
            "Expr -> identifier = Expr|Rvalue",
            "Rvalue -> Mag Rvalue'",
            "Rvalue' -> Compare Mag Rvalue'|epsilon",
            "Compare -> =|<|>|<=|>=|!=",
            "Mag -> Term Mag'",
            "Mag' -> + Term Mag'|- Term Mag'|epsilon",
            "Term -> Factor Term'",
            "Term' -> * Factor Term'|/ Factor Term'|epsilon",
            "Factor -> ( Expr )|- Factor|+ Factor|identifier|number"
        };

        try {
            Scanner f = new Scanner(new File("examples/test.cc"));

            StringBuilder sb = new StringBuilder();

            while(f.hasNext()) {
                sb.append(f.nextLine());
            }

            Lexer lexer = new Lexer(sb.toString(), patterns);
            
            Grammar g = new Grammar(String.join("\n", rules), patterns);
            
            Parser p = new Parser(g);
            
            List<Token> tokens = lexer.tokenize();

            // System.out.println(tokens);

            System.out.println(p.correct(tokens));
            System.out.println(p.getOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
    }
}
