package SyntaxAnalyse.StmtNodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class BreakStmtNode extends Node {
    public BreakStmtNode(int beginLine) {
        super(beginLine);
        this.syntaxType = SyntaxType.BreakStmt;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("break");
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(!SymbolManager.isIsInForNode()) {
            ErrorData.AddError(new Error(beginLine,'m'));
        }
    }
    @Override
    public Value genIR(){
        Instr instr = new Instr(InstrType.br,null,breakBranch,null);
        IRbuilder.addInstrToBB(instr);
        return null;
    }
}
