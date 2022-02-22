package compiler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import compiler.core.lexer.Lexer;

public class Main {
    
    public static void main(String[] args) {
        try {
            Scanner f = new Scanner(new File("examples/test.cc"));

            StringBuilder sb = new StringBuilder();

            while(f.hasNext()) {
                sb.append(f.nextLine());
            }

            Lexer lexer = new Lexer(sb.toString());

            System.out.println(lexer.tokenize());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
