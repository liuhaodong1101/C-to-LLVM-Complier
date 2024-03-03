package SyntaxAnalyse.StmtNodes;

import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.BlockNode;
import SyntaxAnalyse.SyntaxType;

public class BlockStmtNode extends Node {
    private BlockNode blockNode;

    public BlockStmtNode(int beginLine, BlockNode blockNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.BlockStmt;
        this.blockNode = blockNode;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    @Override
    public void PR() {
        super.PR();
        blockNode.PR();
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        blockNode.ErrorCheck();
    }
    @Override
    public Value genIR(){
        blockNode.setBreakBranch(breakBranch);
        blockNode.setContinueBranch(continueBranch);
        return blockNode.genIR();
    }
}
