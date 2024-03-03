package SyntaxAnalyse.Nodes;

import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

public class NumberNode extends Node {
    private IntConstNode intConstNode;

    public NumberNode(int beginLine, IntConstNode intConstNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.Number;
        this.intConstNode = intConstNode;
    }

    @Override
    public void PR() {
        super.PR();
        intConstNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
    }
    @Override
    public Value genIR(){
        return intConstNode.genIR();
    }
    public int eval(){
        return intConstNode.eval();
    }
}
