package SyntaxAnalyse.Nodes;

import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class AddExpNode extends Node {
    private MulExpNode mulExpNode;
    private AddExpNode addExpNode;
    private int opt; // 1 + 2 -

    public AddExpNode(int beginLine, MulExpNode mulExpNode, AddExpNode addExpNode,int type,int opt) {
        super(beginLine);
        this.mulExpNode = mulExpNode;
        this.addExpNode = addExpNode;
        this.opt = opt;
        this.syntaxType = SyntaxType.AddExp;
        this.type = type;
    }

    private int type; //1 only one 2 more

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            mulExpNode.PR();
        } else {
            addExpNode.PR();
            if(opt == 1) {
                TokenMap.printSingleToken("+");
            } else {
                TokenMap.printSingleToken("-");
            }
            mulExpNode.PR();
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(type == 1) {
            mulExpNode.ErrorCheck();
        } else {
            mulExpNode.ErrorCheck();
            addExpNode.ErrorCheck();
        }
    }

    public int getDegree(){
        if(type == 2) {
            return 0;
        } else {
            return mulExpNode.getDegree();
        }
    }

    @Override
    public Value genIR(){
        if(type == 1) {
            return mulExpNode.genIR();
        } else {
            Value op1 = addExpNode.genIR();
            Value op2 = mulExpNode.genIR();
            IRsym distinct = IRbuilder.allocNewSym();//new var
            if (opt == 1) {
                Instr instr = new Instr(InstrType.add, distinct, (IRsym) op1, (IRsym) op2);
                IRbuilder.addInstrToBB(instr);
            } else {
                Instr instr = new Instr(InstrType.sub, distinct, (IRsym) op1, (IRsym) op2);
                IRbuilder.addInstrToBB(instr);
            }
            return distinct;
        }
    }
    public int eval(){
        if(type == 1) {
            return mulExpNode.eval();
        } else {
            if(opt == 1) {
                return mulExpNode.eval() + addExpNode.eval();
            } else {
                return addExpNode.eval() - mulExpNode.eval();
            }
        }
    }
}
