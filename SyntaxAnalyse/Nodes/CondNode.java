package SyntaxAnalyse.Nodes;

import LLVM.BasicBlock;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

public class CondNode extends Node {
    private LOrExpNode lOrExpNode;

    private BasicBlock trueBranch;
    private BasicBlock falseBranch;

    public void setTrueBranch(BasicBlock trueBranch) {
        this.trueBranch = trueBranch;
    }

    public void setFalseBranch(BasicBlock falseBranch) {
        this.falseBranch = falseBranch;
    }
    public CondNode(int beginLine, LOrExpNode lOrExpNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.Cond;
        this.lOrExpNode = lOrExpNode;
    }

    @Override
    public void PR() {
        super.PR();
        lOrExpNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
    }

    @Override
    public Value genIR(){
        lOrExpNode.setTrueBranch(trueBranch);
        lOrExpNode.setFalseBranch(falseBranch);
        return lOrExpNode.genIR();
    }
}
