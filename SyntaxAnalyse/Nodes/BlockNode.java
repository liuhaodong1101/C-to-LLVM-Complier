package SyntaxAnalyse.Nodes;

import ErrorCheck.SymbolManager;
import LLVM.IRbuilder;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.StmtNodes.BlockStmtNode;
import SyntaxAnalyse.StmtNodes.ReturnStmtNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class BlockNode extends Node {
    private ArrayList<BlockItemNode> blockItemNodes;
    public BlockNode(int beginLine,ArrayList<BlockItemNode> blockItemNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.Block;
        this.blockItemNodes = blockItemNodes;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printSingleToken("{");
        for(int i = 0;i<blockItemNodes.size();i++){
            blockItemNodes.get(i).PR();
        }
        TokenMap.printSingleToken("}");
        System.out.println(this);
    }

    public ArrayList<BlockItemNode> getBlockItemNodes() {
        return blockItemNodes;
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        SymbolManager.enterSymbolTable();
        for(BlockItemNode blockStmtNode:blockItemNodes){
            blockStmtNode.ErrorCheck();
        }
        SymbolManager.backSymbolTable();
    }
    public boolean IsReturnEnd(){
        int i = blockItemNodes.size() - 1;
        if(i>=0) {
            BlockItemNode blockItemNode = blockItemNodes.get(i);
            if(blockItemNode.getNode() instanceof StmtNode) {
                if(((StmtNode) blockItemNode.getNode()).getType() == 8) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public Value genIR(){
        SymbolManager.enterSymbolTable();
        for(BlockItemNode blockItemNode:blockItemNodes){
            blockItemNode.setBreakBranch(breakBranch);
            blockItemNode.setContinueBranch(continueBranch);
            blockItemNode.genIR();
        }
        SymbolManager.backSymbolTable();
        return IRbuilder.getCurBB();//todo
    }

}
