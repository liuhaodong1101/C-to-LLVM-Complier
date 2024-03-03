import SyntaxAnalyse.Parser;
import TokenAnalyse.Lexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PushbackInputStream;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        try (FileInputStream fis = new FileInputStream("testfile.txt");
             PushbackInputStream pbin = new PushbackInputStream(fis)) {
            while (lexer.doSys(pbin) != - 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Parser.test();
    }
}