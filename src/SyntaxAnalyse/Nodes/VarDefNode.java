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
import java.util.Collections;

import static LLVM.Types.BaseType.INT32;

public class VarDefNode extends Node {
    private int mode; // 1 not has initValNode, 2 has initValNode
    private IdentNode identNode;
    private ArrayList<ConstExpNode> constExpNodes;

    private InitValNode initValNode;
    public VarDefNode(int beginLine,IdentNode identNode,ArrayList<ConstExpNode> constExpNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.VarDef;
        this.identNode = identNode;
        this.constExpNodes = constExpNodes;
        this.mode = 1;
    }

    public VarDefNode(int beginLine,IdentNode identNode,ArrayList<ConstExpNode> constExpNodes,InitValNode initValNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.VarDef;
        this.identNode = identNode;
        this.constExpNodes = constExpNodes;
        this.initValNode = initValNode;
        this.mode = 2;
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
        if(mode == 2) {
            TokenMap.printSingleToken("=");
            initValNode.PR();
        }
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
            varSymbol.setConst(false);
            SymbolManager.AddVarSymbol(varSymbol);
        }
        if(initValNode!=null) initValNode.ErrorCheck();
    }

    @Override
    public Value genIR(){
        ArrayList<Integer> dims = new ArrayList<>();
        ArrayList<Integer> initVals = new ArrayList<>();
        Type type;
        if(SymbolManager.getbTypeNode().getType() == 1) {
            type = INT32;
        } else {
            type = INT32;
            // TODO: 2023/11/3
        }
        for(ConstExpNode constExpNode : constExpNodes) {
            dims.add(constExpNode.eval());
        }
        Type type1;
        if(dims.size()>0){
            type1 = new ArrayType(type,dims);
        } else {
            type1 = INT32;
        }
        if(SymbolManager.isGlobal()) {
            String name = identNode.getName();
            if(mode == 2) {
                initVals = initValNode.initValues();
            } else {
                int tmp = 1;
                for(Integer dim:dims) {
                    tmp*=dim;
                }
                initVals = new ArrayList<>(Collections.nCopies(tmp, 0));
            }
            VarSymbol varSymbol =new VarSymbol(identNode.getName(), identNode.getBeginLine(),true,dims,initVals,type1,"@"+identNode.getName());
            varSymbol.setConst(true);
            SymbolManager.AddVarSymbol(varSymbol);
            GlobalVar globalVar = new GlobalVar(name,false,dims,initVals,type);
            return globalVar;
        } else {
            if(!constExpNodes.isEmpty()) {   //考虑数组
                IRsym distinct = IRbuilder.allocNewSym();
                type = new ArrayType(type,dims);
                VarSymbol varSymbol =new VarSymbol(identNode.getName(), identNode.getBeginLine(),false,dims,initVals,type, distinct.getName());
                varSymbol.setConst(true);
                Instr instr = new Instr(InstrType.alloca, distinct,new IRsym(false,""),null);
                instr.setType(type);
                IRbuilder.addInstrToBB(instr);
                if(mode == 2) {
                    IRsyms initValArrayStruct = (IRsyms) initValNode.genIR();
                    ArrayList<IRsym> initValArray = initValArrayStruct.getiRsyms();
                    if(dims.size()==1)  {
                        int len = initValArray.size();
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
                }
                SymbolManager.AddVarSymbol(varSymbol);
                return distinct;
            } else {
                IRsym distinct = IRbuilder.allocNewSym();
                VarSymbol varSymbol = new VarSymbol(identNode.getName(), identNode.getBeginLine(), false, dims, initVals, type, distinct.getName());
                varSymbol.setConst(false);
                IRbuilder.addInstrToBB(new Instr(InstrType.alloca, distinct, new IRsym(false, ""), null));
                if (initValNode != null) {
                    IRsyms iRsyms = (IRsyms) initValNode.genIR();
                    IRsym value = iRsyms.getiRsyms().get(0);
                    IRbuilder.addInstrToBB(new Instr(InstrType.store, distinct, (IRsym) value, null));
                }
                SymbolManager.AddVarSymbol(varSymbol);
                return distinct;//
            }
        }
        // TODO: 2023/10/31  全局应算 局部拆分成 无参声明和赋值语句
    }
}
