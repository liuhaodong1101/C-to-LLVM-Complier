package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import ErrorCheck.VarSymbol;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Param;
import LLVM.Type;
import LLVM.Types.ArrayType;
import LLVM.Types.PointerType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;
import static LLVM.Types.PointerType.INT32P;

public class FuncFParamNode extends Node {
    private BTypeNode bTypeNode;
    private IdentNode identNode;
    private ArrayList<ConstExpNode> constExpNodes;

    private int type; // 1 simple 2 has [] 3 has constExp
    public FuncFParamNode(int beginLine, BTypeNode bTypeNode, IdentNode identNode, ArrayList<ConstExpNode> constExpNodes,int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.FuncFParam;
        this.bTypeNode = bTypeNode;
        this.identNode = identNode;
        this.constExpNodes = constExpNodes;
        this.type =type;
    }

    @Override
    public void PR() {
        super.PR();
        bTypeNode.PR();
        identNode.PR();
        if(type == 1) {

        } else if(type == 2) {
            TokenMap.printSingleToken("[");
            TokenMap.printSingleToken("]");
        } else {
            TokenMap.printSingleToken("[");
            TokenMap.printSingleToken("]");
            for(int i = 0;i<constExpNodes.size();i++){
                TokenMap.printSingleToken("[");
                constExpNodes.get(i).PR();
                TokenMap.printSingleToken("]");
            }
        }
        System.out.println(this);
    }

    public int getDegree(){
        if(type == 1) {
            return 0;
        } else if(type == 2) {
            return 1;
        } else {
            return 2;
        }
    }
    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(SymbolManager.isContainVarOrFuncSymbol(identNode.getName())){
            ErrorData.AddError(new Error(identNode.getBeginLine(),'b'));
        } else {
            VarSymbol varSymbol = new VarSymbol(identNode.getName(),identNode.getBeginLine(),type - 1,false);
            varSymbol.setConst(false);
            SymbolManager.AddVarSymbol(varSymbol);
        }
    }
    @Override
    public Value genIR(){
        if(type == 1) {
            IRsym iRsym = IRbuilder.allocNewSym();
            VarSymbol varSymbol = new VarSymbol(identNode.getName(),identNode.getBeginLine(),type - 1,false);
            varSymbol.setConst(false);
            varSymbol.setLlvmName(iRsym.getName());
            SymbolManager.AddVarSymbol(varSymbol);

            Type type1 = INT32;
            if(bTypeNode.getType() != 1) {
                // TODO: 2023/11/3
            }
            Param param =  new Param(iRsym.getName(),type1);
            param.setOriginName(identNode.getName());
            return param;
        } else if(type == 2){
            IRsym iRsym = IRbuilder.allocNewSym();
            VarSymbol varSymbol = new VarSymbol(identNode.getName(),identNode.getBeginLine(),type -1,false);
            varSymbol.setConst(false);
            varSymbol.setType(INT32P);
            SymbolManager.AddVarSymbol(varSymbol);
            Type type1 = INT32P;
            if(bTypeNode.getType() != 1) {
                // TODO: 2023/11/3
            }
            Param param = new Param(iRsym.getName(),type1);
            param.setOriginName(identNode.getName());
            return param;
        } else if(type == 3) {
            IRsym iRsym = IRbuilder.allocNewSym();
            VarSymbol varSymbol = new VarSymbol(identNode.getName(),identNode.getBeginLine(),type -1,false);
            varSymbol.setConst(false);
            int len = constExpNodes.get(0).eval(); // 只考虑2维数组
            ArrayList<Integer> dims = new ArrayList<>();
            dims.add(len);
            ArrayType arrayType = new ArrayType(INT32,dims);
            PointerType pointerType = new PointerType(arrayType);
            varSymbol.setType(pointerType);
            SymbolManager.AddVarSymbol(varSymbol);
            if(bTypeNode.getType() != 1) {
                // TODO: 2023/11/3
            }
            Param param = new Param(iRsym.getName(),pointerType);
            param.setOriginName(identNode.getName());
            return param;
        }
        return Value.Nothing;
    }
}
