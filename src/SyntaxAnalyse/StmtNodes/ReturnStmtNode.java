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
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import static LLVM.Value.Nothing;

public class ReturnStmtNode extends Node {
    private ExpNode expNode;
    private int type;//1 has exp 2 not has exp

    public ReturnStmtNode(int beginLine, ExpNode expNode, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.ReturnStmt;
        this.expNode = expNode;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("return");
        if(type == 1){
            expNode.PR();
        }
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(expNode!=null) expNode.ErrorCheck();
        if(!SymbolManager.isNeedReturnValue() && type == 1) {
            ErrorData.AddError(new Error(beginLine,'f'));
        }
    }

    @Override
    public Value genIR(){
        if(type == 1) {
            Value value = expNode.genIR();
            Instr instr = new Instr(InstrType.ret, (IRsym) value,new IRsym(false,""),null);
            IRbuilder.addInstrToBB(instr);
        } else {
            Instr instr  = new Instr(InstrType.ret,null,new IRsym(false,""),null);
            IRbuilder.addInstrToBB(instr);
        }
        return Nothing;
    }
}
