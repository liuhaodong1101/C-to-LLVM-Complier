package SyntaxAnalyse.Nodes;

import ErrorCheck.SymbolManager;
import LLVM.GlobalVar;
import LLVM.GlobalVars;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Value.Nothing;

public class VarDeclNode extends Node {
    private boolean isSingle;
    private ArrayList<VarDefNode> varDefNodes;

    private BTypeNode bTypeNode;
    public VarDeclNode(int beginLine, ArrayList<VarDefNode> varDefNodes,BTypeNode bTypeNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.VarDecl;
        this.varDefNodes = varDefNodes;
        if(varDefNodes.size() == 1) {
            this.isSingle = true;
        } else {
            this.isSingle = false;
        }
        this.bTypeNode = bTypeNode;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("int");
        varDefNodes.get(0).PR();
        for(int i =1;i<varDefNodes.size();i++){
            TokenMap.printSingleToken(",");
            varDefNodes.get(i).PR();
        }
        TokenMap.printSingleToken(";");
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        for(VarDefNode varDefNode:varDefNodes){
            varDefNode.ErrorCheck();
        }
    }

    @Override
    public Value genIR(){
        SymbolManager.setbTypeNode(bTypeNode);
        if(SymbolManager.isGlobal()) {
            ArrayList<GlobalVar> globalVars = new ArrayList<>();
            for (VarDefNode varDefNode : varDefNodes) {
                GlobalVar globalVar = (GlobalVar) varDefNode.genIR();
                globalVars.add(globalVar);
            }
            GlobalVars globalVars1 = new GlobalVars("vars", globalVars);
            return globalVars1;
        } else {
            for (VarDefNode varDefNode : varDefNodes) {
                varDefNode.genIR();
            }
            return Nothing;
        }
    }
}
