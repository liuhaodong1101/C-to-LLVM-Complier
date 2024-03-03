package SyntaxAnalyse.StmtNodes;

import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.CondNode;
import SyntaxAnalyse.Nodes.ForStmtNode;
import SyntaxAnalyse.Nodes.StmtNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class ForNode extends Node {
    private ForStmtNode forStmtNode1,forStmtNode2;
    private CondNode condNode;
    private StmtNode stmtNode;
    private int type;

    public ForNode(int beginLine, ForStmtNode forStmtNode1, ForStmtNode forStmtNode2, CondNode condNode, StmtNode stmtNode, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.For;
        this.forStmtNode1 = forStmtNode1;
        this.forStmtNode2 = forStmtNode2;
        this.condNode = condNode;
        this.stmtNode = stmtNode;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("for");
        TokenMap.printSingleToken("(");
        if(forStmtNode1!=null) {
            forStmtNode1.PR();
        }
        TokenMap.printSingleToken(";");
        if(condNode!=null) {
            condNode.PR();
        }
        TokenMap.printSingleToken(";");
        if(forStmtNode2!=null){
            forStmtNode2.PR();
        }
        TokenMap.printSingleToken(")");
        stmtNode.PR();
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(forStmtNode1!=null) {
            forStmtNode1.ErrorCheck();
        }
        if(condNode!=null) {
            condNode.ErrorCheck();
        }
        if(forStmtNode2!=null){
            forStmtNode2.ErrorCheck();
        }
        SymbolManager.addForDepth();
        if(stmtNode!=null){
            stmtNode.ErrorCheck();
        }
        SymbolManager.subForDepth();
    }

    @Override
    public Value genIR(){
        if(forStmtNode1!=null) {
            forStmtNode1.genIR();
        }
        BasicBlock cond = IRbuilder.allocBB();
        BasicBlock stmt = IRbuilder.allocBB();
        BasicBlock forStmt2 = IRbuilder.allocBB();
        BasicBlock end = IRbuilder.allocBB();
        Instr instr;
        if(condNode == null) {
            instr = new Instr(InstrType.br,null,stmt,null);
            IRbuilder.addInstrToBB(instr);
            stmtNode.setBreakBranch(end);
            stmtNode.setContinueBranch(forStmt2);
        } else {
            instr = new Instr(InstrType.br,null,cond,null);
            IRbuilder.addInstrToBB(instr);
            condNode.setTrueBranch(stmt);
            condNode.setFalseBranch(end);
            stmtNode.setBreakBranch(end);
            stmtNode.setContinueBranch(forStmt2);
            IRbuilder.activeBB(cond);
            condNode.genIR();
        }

        IRbuilder.activeBB(stmt);

        if(stmtNode.getType() == 3) {
            stmtNode.setJump(forStmt2);
            stmtNode.genIR2();
        } else {
            stmtNode.genIR();
            if(!IRbuilder.getCurBB().isRetEnd()) {
                instr = new Instr(InstrType.br, null, forStmt2, null);
                IRbuilder.addInstrToBB(instr);
            }
        }
        IRbuilder.activeBB(forStmt2);
        if(forStmtNode2!=null) {
            forStmtNode2.genIR();
        }
        if(condNode==null) {
            instr = new Instr(InstrType.br, null, stmt, null);
            IRbuilder.addInstrToBB(instr);
        } else {
            instr = new Instr(InstrType.br, null, cond, null);
            IRbuilder.addInstrToBB(instr);
        }
        IRbuilder.activeBB(end);
        return null;
    }

}
