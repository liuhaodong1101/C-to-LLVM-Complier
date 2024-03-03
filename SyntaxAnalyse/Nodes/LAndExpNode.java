package SyntaxAnalyse.Nodes;

import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class LAndExpNode extends Node {
    private EqExpNode eqExpNode;
    private LAndExpNode lAndExpNode;

    private BasicBlock trueBranch;
    private BasicBlock falseBranch;

    public void setTrueBranch(BasicBlock trueBranch) {
        this.trueBranch = trueBranch;
    }

    public void setFalseBranch(BasicBlock falseBranch) {
        this.falseBranch = falseBranch;
    }
    private int type;//1 one 2 more

    public LAndExpNode(int beginLine, EqExpNode eqExpNode, LAndExpNode lAndExpNode, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.LAndExp;
        this.eqExpNode = eqExpNode;
        this.lAndExpNode = lAndExpNode;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            eqExpNode.PR();
        }else {
            lAndExpNode.PR();
            TokenMap.printDoubleToken("&&");
            eqExpNode.PR();
        }
        System.out.println(this);
    }
    @Override
    public Value genIR(){
        if(type == 1){
            eqExpNode.setTrueBranch(trueBranch);
            eqExpNode.setFalseBranch(falseBranch);
            eqExpNode.fgg();
            return null;
        } else {
            BasicBlock basicBlock = IRbuilder.allocBB();
            lAndExpNode.setTrueBranch(basicBlock);
            lAndExpNode.setFalseBranch(falseBranch);
            eqExpNode.setTrueBranch(trueBranch);
            eqExpNode.setFalseBranch(falseBranch);
            lAndExpNode.genIR();
            IRbuilder.activeBB(basicBlock);
            eqExpNode.fgg();
            return null;
        }
    }
}
