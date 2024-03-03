package SyntaxAnalyse.Nodes;

import ErrorCheck.SymbolManager;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class ForStmtNode extends Node {
    private LValNode lValNode;
    private ExpNode expNode;

    public ForStmtNode(int beginLine, LValNode lValNode, ExpNode expNode) {
        super(beginLine);
        this.lValNode = lValNode;
        this.expNode = expNode;
        this.syntaxType = SyntaxType.ForStmt;
    }

    @Override
    public void PR() {
        super.PR();
        lValNode.PR();
        TokenMap.printSingleToken("=");
        expNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
    }

    @Override
    public Value genIR(){
        Value op1 = expNode.genIR();
        String distinctName = lValNode.getIdentNode().getName();
        IRsym distinct = new IRsym(SymbolManager.getVarIsGlobal(distinctName),SymbolManager.getVarNickName(distinctName));
        IRbuilder.addInstrToBB(new Instr(InstrType.store,distinct, (IRsym) op1,null));
        return distinct;
    }
}
