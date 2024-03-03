package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Params;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Value.Nothing;

public class UnaryExpNode extends Node {
    public UnaryExpNode(int beginLine, PrimaryExpNode primaryExpNode,
                        IdentNode identNode, FuncRParamsNode funcRParamsNode, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.UnaryExp;
        this.primaryExpNode = primaryExpNode;
        this.identNode = identNode;
        this.funcRParamsNode = funcRParamsNode;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
        this.type = type;
    }

    private PrimaryExpNode primaryExpNode;
    private IdentNode identNode;
    private FuncRParamsNode funcRParamsNode;
    private UnaryOpNode unaryOpNode;
    private UnaryExpNode unaryExpNode;
    private int type;//1 primaryExp 2 has not FuncRParamsNode 3 has FuncRParamsNode 4 unaryOpNode + unaryExpNode

    @Override
    public void PR() {
        super.PR();
        switch (type) {
            case 1:
                primaryExpNode.PR();
                break;
            case 2:
            case 3:
                identNode.PR();
                TokenMap.printSingleToken("(");
                if(funcRParamsNode!=null) funcRParamsNode.PR();
                TokenMap.printSingleToken(")");
                break;
            case 4:
                unaryOpNode.PR();
                unaryExpNode.PR();
                break;
            default:
                System.out.println("ERROR AT UnaryExpNode");
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(type == 1) {
            primaryExpNode.ErrorCheck();
        } else if(type == 2) {
            identNode.setCheckMode(2);
            identNode.ErrorCheck();
            if(SymbolManager.funcHasBeenDefine(identNode.getName())) {
                if (SymbolManager.getFPNum(identNode.getName()) != 0) {
                    ErrorData.AddError(new Error(identNode.getBeginLine(), 'd'));
                }
            }

        } else if(type == 3) {
            identNode.setCheckMode(2);
            identNode.ErrorCheck();
            funcRParamsNode.ErrorCheck();//todo
            int flag = 0;
            if(SymbolManager.funcHasBeenDefine(identNode.getName())) {
                if (SymbolManager.getFPNum(identNode.getName()) != funcRParamsNode.getRpNum()) {
                    ErrorData.AddError(new Error(identNode.getBeginLine(), 'd'));
                    flag = 1;
                }
            }
            if(SymbolManager.funcHasBeenDefine(identNode.getName()) && flag == 0) {
                ArrayList<Integer> arrayList = SymbolManager.getFuncDegrees(identNode.getName());
                ArrayList<Integer> arrayList1 = funcRParamsNode.getDegrees();
                if(!arrayList1.contains(-100)&&!arrayList1.contains(-1000)) {
                    if (arrayList1.size() == arrayList.size()) {
                        if (!arrayList.equals(arrayList1)) {
                            ErrorData.AddError(new Error(identNode.getBeginLine(), 'e'));
                        }
                    }
                }
             }
        } else {
            unaryExpNode.ErrorCheck();
        }
    }
    public int getDegree(){
        if(type == 1) {
            return primaryExpNode.getDegree();
        } else if(type == 2 || type == 3) {
            if (SymbolManager.funcHasBeenDefine(identNode.getName()))
                return SymbolManager.getFuncReturnDegree(identNode.getName());
            else return -100;
        } else {
            return unaryExpNode.getDegree();
        }
    }

    @Override
    public Value genIR(){
        if(type == 1) {
            return primaryExpNode.genIR();
        } else if(type == 4) {
            if(unaryOpNode.getType() == 1) { //add
                Value value = unaryExpNode.genIR();
                IRsym iRsym = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.add,iRsym,IRbuilder.allocConstSym("0"), (IRsym) value);
                IRbuilder.addInstrToBB(instr);
                return iRsym;
            } else if(unaryOpNode.getType() == 2) {//sub
                Value value = unaryExpNode.genIR();
                IRsym iRsym = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.sub, iRsym,IRbuilder.allocConstSym("0"), (IRsym) value);
                IRbuilder.addInstrToBB(instr);
                return iRsym;
            } else {//not
                Value value = unaryExpNode.genIR();
                IRsym iRsym = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.icmpeq,iRsym,new IRsym(false,"0"), (IRsym) value);
                IRbuilder.addInstrToBB(instr);
                IRsym iRsym1 = IRbuilder.allocNewSym();
                Instr instr1 = new Instr(InstrType.zext1_32,iRsym1,iRsym,iRsym);
                IRbuilder.addInstrToBB(instr1);
                return iRsym1;
                // TODO: 2023/10/31
            }
        } else if(type==2 || type == 3){
            Params params;
            if(type==3)  params = funcRParamsNode.getParams();
            else params = null;
            IRsym distinct;
            if(!SymbolManager.retIsVoid(identNode.getName())) {
                distinct = IRbuilder.allocNewSym();
            } else {
                distinct = null;
            }
            Instr instr = new Instr(InstrType.call,distinct,identNode.getName(),params);
            instr.setType(SymbolManager.getFunType(identNode.getName()));
            IRbuilder.addInstrToBB(instr);
            return distinct;
        }
        return Nothing;
    }

    public int eval(){
        if(type == 1) {
            return primaryExpNode.eval();
        } else if(type == 2 || type == 3){
            return -114514; // TODO: 2023/11/2
        } else {
            if(unaryOpNode.getType() == 1) {
                return 1*unaryExpNode.eval();
            } else if(unaryOpNode.getType() == 2){
                return -1*unaryExpNode.eval();
            } else {
                if(unaryExpNode.eval() !=0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}
