package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.FuncSymbol;
import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.FunctionDecl;
import LLVM.IRbuilder;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class MainFuncDefNode extends Node {
    private BlockNode blockNode;
    private int endLine;

    public MainFuncDefNode(int beginLine, BlockNode blockNode,int endLine) {
        super(beginLine);
        this.syntaxType = SyntaxType.MainFuncDef;
        this.blockNode = blockNode;
        this.endLine = endLine;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("int");
        TokenMap.printKeyToken("main");
        TokenMap.printSingleToken("(");
        TokenMap.printSingleToken(")");
        blockNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(SymbolManager.isContainVarOrFuncSymbol("main")) {
            ErrorData.AddError(new Error(beginLine,'b'));
        } else {
            int type = 2;
            int elementNum = 0;
            ArrayList<Integer> degrees = null;
            FuncSymbol funcSymbol = new FuncSymbol(beginLine,"main",type,elementNum,degrees);
            SymbolManager.AddFuncSymbol(funcSymbol);
        }
        SymbolManager.setNeedReturnValue(true);
        blockNode.ErrorCheck();
        if(!blockNode.IsReturnEnd()){
            ErrorData.AddError(new Error(endLine,'g'));
        }
        SymbolManager.setNeedReturnValue(false);
    }
    @Override
    public Value genIR(){
        int type = 2;
        int elementNum = 0;
        ArrayList<Integer> degrees = null;
        FuncSymbol funcSymbol = new FuncSymbol(beginLine,"main",type,elementNum,degrees);
        SymbolManager.AddFuncSymbol(funcSymbol);
        FunctionDecl functionDecl = IRbuilder.newFunc("main",INT32);
        IRbuilder.allocAndActiveABB();
        blockNode.genIR();
        return functionDecl;
    }
}
