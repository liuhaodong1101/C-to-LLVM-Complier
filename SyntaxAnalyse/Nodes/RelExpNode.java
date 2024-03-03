package SyntaxAnalyse.Nodes;

import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class RelExpNode extends Node {
    private AddExpNode addExpNode;
    private RelExpNode relExpNode;
    private int type;//1 only one 2 more
    private int opt; //1 < 2 > 3 <= 4 >=

    public RelExpNode(int beginLine, AddExpNode addExpNode, RelExpNode relExpNode, int type, int opt) {
        super(beginLine);
        this.syntaxType = SyntaxType.RelExp;
        this.addExpNode = addExpNode;
        this.relExpNode = relExpNode;
        this.type = type;
        this.opt = opt;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            addExpNode.PR();
        } else {
            relExpNode.PR();
            if(opt == 1) {
                TokenMap.printSingleToken("<");
            } else if(opt == 2) {
                TokenMap.printSingleToken(">");
            } else if(opt == 3) {
                TokenMap.printDoubleToken("<=");
            } else{
                TokenMap.printDoubleToken(">=");
            }
            addExpNode.PR();
        }
        System.out.println(this);
    }

    @Override
    public Value genIR(){
        if(type == 1) {
            return addExpNode.genIR();
        } else {
            Value value = relExpNode.genIR();
            Value value1 = addExpNode.genIR();
            IRsym distinct = IRbuilder.allocNewSym();
            Instr instr;
            if(opt == 1) {
                instr = new Instr(InstrType.icmpslt,distinct, (IRsym) value, (IRsym) value1);
            } else if(opt == 2) {
                instr = new Instr(InstrType.icmpsgt,distinct, (IRsym) value, (IRsym) value1);
            } else if(opt == 3) {
                instr = new Instr(InstrType.icmpsle,distinct, (IRsym) value, (IRsym) value1);
            } else{
                instr = new Instr(InstrType.icmpsge,distinct, (IRsym) value, (IRsym) value1);
            }
            IRbuilder.addInstrToBB(instr);
            IRsym distinct2 = IRbuilder.allocNewSym();
            instr = new Instr(InstrType.zext1_32,distinct2,distinct,distinct);
            IRbuilder.addInstrToBB(instr);
            return distinct2;
        }
    }
}
