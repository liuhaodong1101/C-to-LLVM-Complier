package SyntaxAnalyse.StmtNodes;

import LLVM.IRbuilder;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.ExpNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class ExpStmtNode extends Node {
    private ExpNode expNode;
    private boolean hasExp;
    public ExpStmtNode(int beginLine) {
        super(beginLine);
        hasExp = false;
        this.syntaxType = SyntaxType.ExpStmt;
    }
    public ExpStmtNode(int beginLine, ExpNode expNode) {
        super(beginLine);
        hasExp = true;
        this.expNode = expNode;
        this.syntaxType = SyntaxType.ExpStmt;
    }

    @Override
    public void PR() {
        super.PR();
        if(hasExp){
            expNode.PR();
        }
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(hasExp) {
            expNode.ErrorCheck();
        }
    }
    @Override
    public Value genIR(){
        if(hasExp) {
            IRbuilder.setIsOnlyExp(true);
            Value value = expNode.genIR();
            IRbuilder.setIsOnlyExp(false);
            return value;
        } else {
            return null;
        }
    }
}
