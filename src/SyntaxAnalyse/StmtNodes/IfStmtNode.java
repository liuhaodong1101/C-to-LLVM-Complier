package SyntaxAnalyse.StmtNodes;

import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.BlockItemNode;
import SyntaxAnalyse.Nodes.BlockNode;
import SyntaxAnalyse.Nodes.CondNode;
import SyntaxAnalyse.Nodes.StmtNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class IfStmtNode extends Node {
    private CondNode condNode;
    private StmtNode stmtNode1,stmtNode2 = null;
    private int type; //1 has else 2 not has else

    public IfStmtNode(int beginLine, CondNode condNode, StmtNode stmtNode1, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.IfStmt;
        this.condNode = condNode;
        this.stmtNode1 = stmtNode1;
        this.type = type;
    }

    public IfStmtNode(int beginLine, CondNode condNode, StmtNode stmtNode1, StmtNode stmtNode2, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.IfStmt;
        this.condNode = condNode;
        this.stmtNode1 = stmtNode1;
        this.stmtNode2 = stmtNode2;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("if");
        TokenMap.printSingleToken("(");
        condNode.PR();
        TokenMap.printSingleToken(")");
        stmtNode1.PR();
        if(type == 1) {
            TokenMap.printKeyToken("else");
            stmtNode2.PR();
        }
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        condNode.ErrorCheck();
        stmtNode1.ErrorCheck();
        if(stmtNode2!=null) {
            stmtNode2.ErrorCheck();
        }
    }

    @Override
    public Value genIR(){
        if(stmtNode1!=null) {
            stmtNode1.setBreakBranch(breakBranch);
            stmtNode1.setContinueBranch(continueBranch);
        }
        if(stmtNode2!=null) {
            stmtNode2.setBreakBranch(breakBranch);
            stmtNode2.setContinueBranch(continueBranch);
        }
        BasicBlock stmt1BB = IRbuilder.allocBB();
        BasicBlock stmt2BB = IRbuilder.allocBB();
        BasicBlock endBB = IRbuilder.allocBB();
        if(type == 1) {
            condNode.setTrueBranch(stmt1BB);
            condNode.setFalseBranch(stmt2BB);
            condNode.genIR();
            stmt1BB.addEmptyInstr();
            IRbuilder.activeBB(stmt1BB);
            if(stmtNode1.getType() == 3) {
                stmtNode1.setJump(endBB);
                stmtNode1.genIR2();
            } else {
                stmtNode1.genIR();
            }
            stmt2BB.addEmptyInstr();
            IRbuilder.activeBB(stmt2BB);
            if(stmtNode2.getType() == 3) {
                stmtNode2.setJump(endBB);
                stmtNode2.genIR2();
            } else {
                stmtNode2.genIR();
            }
            Instr instr = new Instr(InstrType.br, null, endBB, null);
            stmt1BB.removeEmptyInstr();
            stmt2BB.removeEmptyInstr();
            if(!stmt1BB.isRetEnd()) stmt1BB.addInstr(instr);
            if(!stmt2BB.isRetEnd()) stmt2BB.addInstr(instr);
        } else {
            condNode.setTrueBranch(stmt1BB);
            condNode.setFalseBranch(stmt2BB);
            condNode.genIR();
            stmt1BB.addEmptyInstr();
            IRbuilder.activeBB(stmt1BB);
            if(stmtNode1.getType() == 3) {
                stmtNode1.setJump(endBB);
                stmtNode1.genIR2();
            } else {
                stmtNode1.genIR();
            }
            stmt2BB.addEmptyInstr();
            IRbuilder.activeBB(stmt2BB);
            StmtNode stmtNode = new StmtNode(stmtNode1.getBeginLine(),new ExpStmtNode(stmtNode1.getBeginLine()),2);
            stmtNode.genIR();
            Instr instr = new Instr(InstrType.br, null, endBB, null);
            stmt1BB.removeEmptyInstr();
            stmt2BB.removeEmptyInstr();
            if(!stmt1BB.isRetEnd()) stmt1BB.addInstr(instr);
            if(!stmt2BB.isRetEnd()) stmt2BB.addInstr(instr);
        }
        IRbuilder.activeBB(endBB);
        return null;
    }
}
