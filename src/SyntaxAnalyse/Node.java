package SyntaxAnalyse;

import LLVM.BasicBlock;
import LLVM.Instr;
import LLVM.Value;

public class Node {
    protected int beginLine;
    protected SyntaxType syntaxType;

    public Node(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void PR(){

    }
    @Override
    public String toString() {
        return "<" + syntaxType.toString() + ">";
    }

    public void ErrorCheck(){

    }

    public Value genIR(){
        return null;
    }

    public int eval(){
        return -114514;
    }

    protected BasicBlock continueBranch = null;
    protected BasicBlock breakBranch = null;

    public void setContinueBranch(BasicBlock continueBranch) {
        this.continueBranch = continueBranch;
    }

    public void setBreakBranch(BasicBlock breakBranch) {
        this.breakBranch = breakBranch;
    }

}
