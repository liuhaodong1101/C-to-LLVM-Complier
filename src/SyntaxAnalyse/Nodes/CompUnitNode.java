package SyntaxAnalyse.Nodes;

import LLVM.FunctionDecl;
import LLVM.GlobalVars;
import LLVM.Module;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

import java.util.ArrayList;

import static LLVM.Value.Nothing;

public class CompUnitNode extends Node {
    private ArrayList<DeclNode> declNodes;
    private ArrayList<FuncDefNode> funcDefNodes;
    private MainFuncDefNode mainFuncDefNode;
    public CompUnitNode(int beginLine,ArrayList<DeclNode> declNodes,ArrayList<FuncDefNode> funcDefNodes,MainFuncDefNode mainFuncDefNode) {
        super(beginLine);
        this.syntaxType =  SyntaxType.CompUnit;
        this.declNodes = declNodes;
        this.funcDefNodes = funcDefNodes;
        this.mainFuncDefNode = mainFuncDefNode;
    }
    @Override
    public Value genIR(){
        ArrayList<GlobalVars> globalVarsList = new ArrayList<>();
        ArrayList<FunctionDecl> functionDecls = new ArrayList<>();
        for(DeclNode declNode:declNodes) {
            GlobalVars globalVars = (GlobalVars) declNode.genIR();
            globalVarsList.add(globalVars);
        }
        for (FuncDefNode funcDefNode:funcDefNodes) {
            FunctionDecl functionDecl = (FunctionDecl) funcDefNode.genIR();
            functionDecls.add(functionDecl);
        }
        FunctionDecl mainFunction = (FunctionDecl) mainFuncDefNode.genIR();
        Module module = new Module(globalVarsList,functionDecls,mainFunction);
        module.print();
        return Nothing;
    }
    @Override
    public void PR() {
        super.PR();
        for(int i = 0;i<declNodes.size();i++){
            declNodes.get(i).PR();
        }
        for(int i = 0;i<funcDefNodes.size();i++){
            funcDefNodes.get(i).PR();
        }
        mainFuncDefNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        for(int i = 0;i<declNodes.size();i++){
            declNodes.get(i).ErrorCheck();
        }
        for(int i = 0;i<funcDefNodes.size();i++){
            funcDefNodes.get(i).ErrorCheck();
        }
        mainFuncDefNode.ErrorCheck();
    }
}
