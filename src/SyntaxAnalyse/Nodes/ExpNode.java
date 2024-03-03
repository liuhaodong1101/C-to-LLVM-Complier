package SyntaxAnalyse.Nodes;

import LLVM.IRsym;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

public class ExpNode extends Node {
    public ExpNode(int beginLine, AddExpNode addExpNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.Exp;
        this.addExpNode = addExpNode;
    }

    private AddExpNode addExpNode;

    @Override
    public void PR() {
        super.PR();
        addExpNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        addExpNode.ErrorCheck();
    }

    public int getDegree(){
        return addExpNode.getDegree();
    }

    @Override
    public IRsym genIR(){
        return (IRsym) addExpNode.genIR();
    }

    public int eval(){
        return addExpNode.eval();
    }
}
