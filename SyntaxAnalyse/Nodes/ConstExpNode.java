package SyntaxAnalyse.Nodes;

import LLVM.IRsym;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

public class ConstExpNode extends Node {
    private AddExpNode addExpNode;

    public ConstExpNode(int beginLine, AddExpNode addExpNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.ConstExp;
        this.addExpNode = addExpNode;
    }

    @Override
    public void PR() {
        super.PR();
        addExpNode.PR();
        System.out.println(this);
    }

    public int eval(){
        return addExpNode.eval();
    }

    @Override
    public IRsym genIR(){
        return (IRsym) addExpNode.genIR();
    }
}
