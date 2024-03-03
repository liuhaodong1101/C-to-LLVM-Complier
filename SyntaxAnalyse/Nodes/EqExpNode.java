package SyntaxAnalyse.Nodes;

import LLVM.BasicBlock;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import SyntaxAnalyse.TokenStream;
import TokenAnalyse.TokenMap;

public class EqExpNode extends Node {
    private RelExpNode relExpNode;
    private EqExpNode eqExpNode;
    private int type; // 1 only one 2 more
    private int opt; // 1 == 2 !=

    private BasicBlock trueBranch;
    private BasicBlock falseBranch;
    public void setTrueBranch(BasicBlock trueBranch) {
        this.trueBranch = trueBranch;
    }

    public void setFalseBranch(BasicBlock falseBranch) {
        this.falseBranch = falseBranch;
    }
    public EqExpNode(int beginLine, RelExpNode relExpNode, EqExpNode eqExpNode, int type, int opt) {
        super(beginLine);
        this.syntaxType = SyntaxType.EqExp;
        this.relExpNode = relExpNode;
        this.eqExpNode = eqExpNode;
        this.type = type;
        this.opt = opt;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            relExpNode.PR();
        } else {
            eqExpNode.PR();
            if(opt == 1) {
                TokenMap.printDoubleToken("==");
            } else {
                TokenMap.printDoubleToken("!=");
            }
            relExpNode.PR();
        }
        System.out.println(this);
    }
    @Override
    public Value genIR(){
        if(type == 1) {
            return relExpNode.genIR();
        } else {
            Value value = eqExpNode.genIR();
            Value value1 = relExpNode.genIR();
            if(opt == 1) {
                IRsym distinct = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.icmpeq,distinct, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
                IRsym distinct2 = IRbuilder.allocNewSym();
                Instr instr1 = new Instr(InstrType.zext1_32,distinct2,distinct,distinct);
                IRbuilder.addInstrToBB(instr1);
                return distinct2;
            } else {
                IRsym distinct = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.icmpne,distinct, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
                IRsym distinct2 = IRbuilder.allocNewSym();
                Instr instr1 = new Instr(InstrType.zext1_32,distinct2,distinct,distinct);
                IRbuilder.addInstrToBB(instr1);
                return distinct2;
            }
        }
    }

    public void fgg(){
        if(type == 1) {
            Value value = relExpNode.genIR();
            IRsym distinct = IRbuilder.allocNewSym();
            Instr instr = new Instr(InstrType.icmpne,distinct,new IRsym(false,"0"), (IRsym) value);
            IRbuilder.addInstrToBB(instr);
            instr = new Instr(InstrType.br,distinct,trueBranch,falseBranch);
            IRbuilder.addInstrToBB(instr);
        } else {
            Value value = eqExpNode.genIR();
            Value value1 = relExpNode.genIR();
            IRsym distinct = IRbuilder.allocNewSym();
            if(opt == 1) {
                Instr instr = new Instr(InstrType.icmpeq,distinct, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
            } else {
                Instr instr = new Instr(InstrType.icmpne,distinct, (IRsym) value, (IRsym) value1);
                IRbuilder.addInstrToBB(instr);
            }
            Instr instr = new Instr(InstrType.br,distinct,trueBranch,falseBranch);
            IRbuilder.addInstrToBB(instr);
        }
    }
}
