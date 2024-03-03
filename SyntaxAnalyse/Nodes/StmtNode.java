package SyntaxAnalyse.Nodes;

import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.StmtNodes.BlockStmtNode;
import SyntaxAnalyse.SyntaxType;

import java.util.ArrayList;

public class StmtNode extends Node {
    private Node node;
    private BasicBlock jump;
    private int type;   //1AssignStmt,

    public BasicBlock getJump() {
        return jump;
    }

    public void setJump(BasicBlock jump) {
        this.jump = jump;
    }

    public Node getNode() {
        return node;
    }

    //2ExpStmt,
                        //3BlockStmt,
                        //4IfStmt,
                        //5For,
                        //6BreakStmt,
                        //7ContinueStmt,
                        //8ReturnStmt,
                        //9GetIntStmt,
                        //10PrintfStmt
    public StmtNode(int beginLine,Node node,int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.Stmt;
        this.node = node;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        node.PR();
        System.out.println(this);
    }

    public int getType() {
        return type;
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        node.ErrorCheck();
    }
    @Override
    public Value genIR(){
        node.setBreakBranch(breakBranch);
        node.setContinueBranch(continueBranch);
        node.genIR();
        return null;
    }

    public void genIR2(){
        BlockNode blockNode = ((BlockStmtNode) node).getBlockNode();
        ArrayList<BlockItemNode> blockItemNodes = blockNode.getBlockItemNodes();
        SymbolManager.enterSymbolTable();
        for(BlockItemNode blockItemNode:blockItemNodes){
            blockItemNode.setBreakBranch(breakBranch);
            blockItemNode.setContinueBranch(continueBranch);
            blockItemNode.genIR();
        }
        SymbolManager.backSymbolTable();
        if(!IRbuilder.getCurBB().isRetEnd()) {
            IRbuilder.addInstrToBB(new Instr(InstrType.br,null,jump,jump));
        }
    }
}
