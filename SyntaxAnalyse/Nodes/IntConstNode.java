package SyntaxAnalyse.Nodes;

import LLVM.IRbuilder;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenType;

public class IntConstNode extends Node {
    private String value;

    public IntConstNode(int beginLine, String value) {
        super(beginLine);
        this.syntaxType = SyntaxType.IntConst;
        this.value = value;
    }

    @Override
    public void PR() {
        super.PR();
        System.out.println(TokenType.INTCON + " " + value);
    }
    @Override
    public Value genIR(){
        return IRbuilder.allocConstSym(value);
    }
    public int eval(){
        return Integer.parseInt(value);
    }
}
