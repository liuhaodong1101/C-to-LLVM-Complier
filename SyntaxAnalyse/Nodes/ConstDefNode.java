package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import ErrorCheck.VarSymbol;
import LLVM.GlobalVar;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.IRsyms;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Type;
import LLVM.Types.ArrayType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class ConstDefNode extends Node {
    private IdentNode identNode;
    private ArrayList<ConstExpNode> constExpNodes;
    private ConstInitValNode constInitValNode;
    public ConstDefNode(int beginLine,IdentNode identNode,ArrayList<ConstExpNode> constExpNodes,ConstInitValNode constInitValNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.ConstDef;
        this.identNode = identNode;
        this.constExpNodes = constExpNodes;
        this.constInitValNode = constInitValNode;
    }

    @Override
    public void PR() {
        super.PR();
        identNode.PR();
        for(int i = 0;i<constExpNodes.size();i++){
            TokenMap.printSingleToken("[");
            constExpNodes.get(i).PR();
            TokenMap.printSingleToken("]");
        }
        TokenMap.printSingleToken("=");
        constInitValNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(SymbolManager.isContainVarOrFuncSymbol(identNode.getName())){
            ErrorData.AddError(new Error(identNode.getBeginLine(),'b'));
        } else {
            boolean global = SymbolManager.isGlobal();
            VarSymbol varSymbol = new VarSymbol(identNode.getName(),identNode.getBeginLine(),constExpNodes.size(),global);
            varSymbol.setConst(true);
            SymbolManager.AddVarSymbol(varSymbol);
        }
    }
    @Override
    public Value genIR(){
        Type type;
        if(SymbolManager.getbTypeNode().getType() == 1) {
            type = INT32;
        } else {
            type = INT32;
            // TODO: 2023/11/3  其他double float
        }
        VarSymbol varSymbol;
        IRsym distinct;
        ArrayList<Integer> dims = new ArrayList<>();
        ArrayList<Integer> initVals = new ArrayList<>();
        initVals = constInitValNode.initValues();
        if(SymbolManager.isGlobal()) {
            String name = identNode.getName();
            for(ConstExpNode constExpNode : constExpNodes) {
                dims.add(constExpNode.eval());
            }
            Type type1 = INT32;
            if(dims.size()>0){
                type1 = new ArrayType(type,dims);
            }
            varSymbol =new VarSymbol(identNode.getName(), identNode.getBeginLine(),true,dims,initVals,type1,"@"+identNode.getName());
            varSymbol.setConst(true);
            SymbolManager.AddVarSymbol(varSymbol);
            GlobalVar globalVar = new GlobalVar(name,true,dims,initVals,type);
            return globalVar;
        } else {
            if(!constExpNodes.isEmpty()) {   //考虑数组
                for(ConstExpNode constExpNode : constExpNodes) {
                    dims.add(constExpNode.eval());
                }
                distinct = IRbuilder.allocNewSym();
                type = new ArrayType(type,dims);
                varSymbol =new VarSymbol(identNode.getName(), identNode.getBeginLine(),false,dims,initVals,type, distinct.getName());
                varSymbol.setConst(true);
                Instr instr = new Instr(InstrType.alloca, distinct,new IRsym(false,""),null);
                instr.setType(type);
                IRbuilder.addInstrToBB(instr);
                IRsyms initValArrayStruct = (IRsyms) constInitValNode.genIR();
                ArrayList<IRsym> initValArray = initValArrayStruct.getiRsyms();
                if(dims.size()==1)  {
                    int len = dims.get(0);
                    for(int i =0;i<len;i++) {
                        ArrayList<IRsym> degrees = new ArrayList<>();
                        degrees.add(new IRsym(false, i+" "));
                        IRsym pointerReceiver = IRbuilder.allocNewSym();
                        Instr getPointInstr = new Instr(InstrType.getelementptr, pointerReceiver,distinct,null);
                        getPointInstr.setType(type);
                        getPointInstr.setOps(degrees);
                        IRbuilder.addInstrToBB(getPointInstr);
                        Instr store = new Instr(InstrType.store,pointerReceiver, initValArray.get(i),null);
                        IRbuilder.addInstrToBB(store);
                    }
                } else if(dims.size()==2){
                    int len1 = dims.get(0);
                    int len2 = dims.get(1);
                    for(int i = 0;i<len1;i++){
                        for(int j = 0;j<len2;j++){
                            ArrayList<IRsym> degrees = new ArrayList<>();
                            degrees.add(new IRsym(false, i+" "));
                            degrees.add(new IRsym(false, j+" "));
                            IRsym pointerReceiver = IRbuilder.allocNewSym();
                            Instr getPointInstr = new Instr(InstrType.getelementptr, pointerReceiver,distinct,null);
                            getPointInstr.setOps(degrees);
                            getPointInstr.setType(type);
                            IRbuilder.addInstrToBB(getPointInstr);
                            Instr store = new Instr(InstrType.store,pointerReceiver, initValArray.get(i*len2 + j),null);
                            IRbuilder.addInstrToBB(store);
                        }
                    }
                }
            } else {
                //不考虑数组
                distinct = IRbuilder.allocNewSym();
                varSymbol =new VarSymbol(identNode.getName(), identNode.getBeginLine(),false,dims,initVals,type, distinct.getName());
                varSymbol.setConst(true);
                IRbuilder.addInstrToBB(new Instr(InstrType.alloca, distinct,new IRsym(false,""),null));
                IRsyms iRsyms = (IRsyms) constInitValNode.genIR();
                IRsym value = iRsyms.getiRsyms().get(0);
                IRbuilder.addInstrToBB(new Instr(InstrType.store,distinct, value,null));
            }
            SymbolManager.AddVarSymbol(varSymbol);
            return distinct;
        }

        // TODO: 2023/10/31  全局应算 局部拆分成 无参声明和赋值语句
    }
}
