package SyntaxAnalyse.StmtNodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.ExpNode;
import SyntaxAnalyse.Nodes.LValNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class AssignStmtNode extends Node {
    private LValNode lValNode;
    private ExpNode expNode;

    public AssignStmtNode(int beginLine, LValNode lValNode, ExpNode expNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.AssignStmt;
        this.lValNode = lValNode;
        this.expNode = expNode;
    }

    @Override
    public void PR() {
        super.PR();
        lValNode.PR();
        TokenMap.printSingleToken("=");
        expNode.PR();
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        lValNode.ErrorCheck();
        if(SymbolManager.isVarConst(lValNode.getIdentNode().getName())) {
            ErrorData.AddError(new Error(beginLine,'h'));
        }
        expNode.ErrorCheck();
    }

    @Override
    public Value genIR(){
        Value op1 = expNode.genIR();
        lValNode.setLeft(true);
        Value value = lValNode.genIR();
        lValNode.setLeft(false);
        IRbuilder.addInstrToBB(new Instr(InstrType.store, (IRsym) value, (IRsym) op1,null));
        return value;
    }
}
