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
import SyntaxAnalyse.Nodes.LValNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class GetIntStmtNode extends Node {
    private LValNode lValNode;

    public GetIntStmtNode(int beginLine, LValNode lValNode) {
        super(beginLine);
        this.lValNode = lValNode;
        this.syntaxType = SyntaxType.GetIntStmt;
    }

    @Override
    public void PR() {
        super.PR();
        lValNode.PR();
        TokenMap.printSingleToken("=");
        TokenMap.printKeyToken("getint");
        TokenMap.printSingleToken("(");
        TokenMap.printSingleToken(")");
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(SymbolManager.isVarConst(lValNode.getIdentNode().getName())) {
            ErrorData.AddError(new Error(beginLine,'h'));
        }
        lValNode.ErrorCheck();
    }

    @Override
    public Value genIR(){
        IRsym tmpVal = IRbuilder.allocNewSym();
        Instr instr1  = new Instr(InstrType.call, tmpVal,"getint",null);
        IRbuilder.addInstrToBB(instr1);
        lValNode.setLeft(true);
        Value value = lValNode.genIR();
        lValNode.setLeft(false);
        IRbuilder.addInstrToBB(new Instr(InstrType.store, (IRsym) value,  tmpVal,null));
        return null;
    }
}
