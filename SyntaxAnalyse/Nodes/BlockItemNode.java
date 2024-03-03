package SyntaxAnalyse.Nodes;

import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.StmtNodes.BreakStmtNode;
import SyntaxAnalyse.SyntaxType;

public class BlockItemNode extends Node {
    private Node node;
    private boolean isStmt;

    public BlockItemNode(int beginLine, Node node,boolean isStmt) {
        super(beginLine);
        this.syntaxType = SyntaxType.BlockItem;
        this.node = node;
        this.isStmt = isStmt;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void PR() {
        super.PR();
        node.PR();
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        node.ErrorCheck();
    }

    @Override
    public Value genIR(){
        node.setContinueBranch(continueBranch);
        node.setBreakBranch(breakBranch);
        return node.genIR();
    }

    public boolean isBreakOrContinue(){
        if(isStmt) {
            if(((StmtNode) node).getType() == 6 || ((StmtNode) node).getType() == 7) {
                return true;
            }
        }
        return false;
    }
}
