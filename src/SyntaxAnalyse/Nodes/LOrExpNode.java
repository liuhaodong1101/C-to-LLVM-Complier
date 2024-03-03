package SyntaxAnalyse.Nodes;

import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class LOrExpNode extends Node {
    private LOrExpNode lOrExpNode;
    private LAndExpNode lAndExpNode;
    private int type;//1 only one 2 more

    private BasicBlock trueBranch;
    private BasicBlock falseBranch;

    public BasicBlock getTrueBranch() {
        return trueBranch;
    }

    public void setTrueBranch(BasicBlock trueBranch) {
        this.trueBranch = trueBranch;
    }

    public BasicBlock getFalseBranch() {
        return falseBranch;
    }

    public void setFalseBranch(BasicBlock falseBranch) {
        this.falseBranch = falseBranch;
    }

    public LOrExpNode(int beginLine, LOrExpNode lOrExpNode, LAndExpNode lAndExpNode, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.LOrExp;
        this.lOrExpNode = lOrExpNode;
        this.lAndExpNode = lAndExpNode;
        this.type = type;
    }
    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            lAndExpNode.PR();
        }else {
            lOrExpNode.PR();
            TokenMap.printDoubleToken("||");
            lAndExpNode.PR();
        }
        System.out.println(this);
    }
    @Override
    public Value genIR(){
        if(type == 1) {
            lAndExpNode.setTrueBranch(trueBranch);
            lAndExpNode.setFalseBranch(falseBranch);
            return lAndExpNode.genIR();
        } else {
            BasicBlock basicBlock = IRbuilder.allocBB();
            lOrExpNode.setTrueBranch(trueBranch);
            lOrExpNode.setFalseBranch(basicBlock);
            lAndExpNode.setTrueBranch(trueBranch);
            lAndExpNode.setFalseBranch(falseBranch);
            lOrExpNode.genIR();
            IRbuilder.activeBB(basicBlock);
            return lAndExpNode.genIR();
        }
    }
}
