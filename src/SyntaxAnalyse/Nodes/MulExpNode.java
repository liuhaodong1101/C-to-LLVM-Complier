package SyntaxAnalyse.Nodes;

import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.Token;
import TokenAnalyse.TokenMap;
import TokenAnalyse.TokenType;

public class MulExpNode extends Node {
    private UnaryExpNode unaryExpNode;
    private MulExpNode mulExpNode;

    private int opt; // 1 * 2 / 3 %
    private int type; //1 only one 2 more

    public MulExpNode(int beginLine, UnaryExpNode unaryExpNode, MulExpNode mulExpNode, int type,int opt) {
        super(beginLine);
        this.opt = opt;
        this.syntaxType = SyntaxType.MulExp;
        this.unaryExpNode = unaryExpNode;
        this.mulExpNode = mulExpNode;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            unaryExpNode.PR();
        } else {
            mulExpNode.PR();
            if(opt == 1) {
                TokenMap.printSingleToken("*");
            } else if(opt == 2) {
                TokenMap.printSingleToken("/");
            } else {
                TokenMap.printSingleToken("%");
            }
            unaryExpNode.PR();
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(type == 1) {
            unaryExpNode.ErrorCheck();
        } else {
            unaryExpNode.ErrorCheck();
            mulExpNode.ErrorCheck();
        }
    }

    public int getDegree(){
        if(type == 2) {
            return 0;
        } else {
            return unaryExpNode.getDegree();
        }
    }

    @Override
    public Value genIR(){
        if(type == 1) {
            return unaryExpNode.genIR();
        } else {
            Value value = mulExpNode.genIR();
            Value value1 = unaryExpNode.genIR();
            IRsym iRsym = IRbuilder.allocNewSym();//new var
            if(opt == 1){
                Instr instr = new Instr(InstrType.mul, iRsym, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
            } else if (opt == 2) {
                Instr instr = new Instr(InstrType.sdiv, iRsym, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
            } else {
                Instr instr = new Instr(InstrType.srem, iRsym, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
            }
            return iRsym;
        }
    }

    public int eval(){
        if(type == 1){
            return unaryExpNode.eval();
        } else {
            if(opt == 1) {
                return mulExpNode.eval() * unaryExpNode.eval();
            } else if(opt == 2) {
                return mulExpNode.eval() / unaryExpNode.eval();
            } else {
                return mulExpNode.eval() % unaryExpNode.eval();
            }
         }
    }

}
