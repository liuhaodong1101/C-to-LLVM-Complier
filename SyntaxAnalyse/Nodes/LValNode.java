package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Type;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import SyntaxAnalyse.TokenStream;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class LValNode extends Node {
    private boolean isLeft = false;
    private IdentNode identNode;

    public void setLeft(boolean left) {
        isLeft = left;
    }

    private ArrayList<ExpNode> expNodes;
    private int degree;

    public LValNode(int beginLine, IdentNode identNode, ArrayList<ExpNode> expNodes, int degree) {
        super(beginLine);
        this.syntaxType = SyntaxType.LVal;
        this.identNode = identNode;
        this.expNodes = expNodes;
        this.degree = degree;
    }

    public IdentNode getIdentNode() {
        return identNode;
    }

    @Override
    public void PR() {
        super.PR();
        identNode.PR();
        for(int i = 0;i<expNodes.size();i++){
            TokenMap.printSingleToken("[");
            expNodes.get(i).PR();
            TokenMap.printSingleToken("]");
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(!SymbolManager.varHasBeenDefine(identNode.getName())){
            ErrorData.AddError(new Error(beginLine,'c'));
        }
    }
    public int getDegree(){
        if(SymbolManager.varHasBeenDefine(identNode.getName())) {
            return SymbolManager.getVarDegree(identNode.getName()) - expNodes.size();
        }
        else {
            return -1000;
        }
    }

    public Value genIR(){
        if(SymbolManager.isVarArray(identNode.getName())) {
            String arrayName = identNode.getName();
            int arrDegree = SymbolManager.getVarDegree(arrayName);
            ArrayList<IRsym> iRsyms = new ArrayList<>();
            for(ExpNode expNode:expNodes){
                iRsyms.add(expNode.genIR());
            }
            String arrayNickName = SymbolManager.getVarNickName(arrayName);
            IRsym iRsym;
            if(SymbolManager.isInFuncParams(identNode.getName())) {
                IRsym op1 = new IRsym(SymbolManager.isGlobal(), arrayNickName);
                iRsym = IRbuilder.allocNewSym();
                Instr instr = new Instr(InstrType.load, iRsym, op1, null);
                Type type = SymbolManager.getVarType(arrayName);
                instr.setType(type);
                IRbuilder.addInstrToBB(instr);
            } else {
                iRsym= new IRsym(SymbolManager.isGlobal(), arrayNickName);
            }
            IRsym arrayPointer = IRbuilder.allocNewSym();
            Instr getPointInstr = new Instr(InstrType.getelementptr, arrayPointer, iRsym,null);
            getPointInstr.setType(SymbolManager.getVarType(arrayName));
            getPointInstr.setOps(iRsyms);
            IRbuilder.addInstrToBB(getPointInstr);
            if(expNodes.size() == arrDegree && !isLeft) {
                IRsym integerDistinct = IRbuilder.allocNewSym();
                Instr loadInstr = new Instr(InstrType.load, integerDistinct, arrayPointer,null);
                IRbuilder.addInstrToBB(loadInstr);
                return integerDistinct;
            }
            return arrayPointer;
        } else {
            String name = SymbolManager.getVarNickName(identNode.getName());
            IRsym op1 = new IRsym(SymbolManager.isGlobal(), name);
            if(isLeft) {
                return op1;
            }
            IRsym iRsym = IRbuilder.allocNewSym();
            Instr instr = new Instr(InstrType.load, iRsym, op1, null);
            IRbuilder.addInstrToBB(instr);
            return iRsym;
        }
    }
    public int eval(){//todo
        return SymbolManager.getSingleGlobalVarValue(identNode.getName());
    }
}
