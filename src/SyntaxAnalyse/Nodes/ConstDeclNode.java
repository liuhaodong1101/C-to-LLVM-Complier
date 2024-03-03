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

public class ConstDeclNode extends Node {
    private ArrayList<ConstDefNode> constDefNodes;
    private boolean isSingle;

    private BTypeNode bTypeNode;
    public ConstDeclNode(int beginLine,ArrayList<ConstDefNode> constDefNodes,BTypeNode bTypeNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.ConstDecl;
        this.constDefNodes = constDefNodes;
        if(constDefNodes.size() == 1){
            isSingle = true;
        } else {
            isSingle = false;
        }
        this.bTypeNode = bTypeNode;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("const");
        TokenMap.printKeyToken("int");
        constDefNodes.get(0).PR();
        for(int i = 1;i<constDefNodes.size();i++){
            TokenMap.printSingleToken(",");
            constDefNodes.get(i).PR();
        }
        TokenMap.printSingleToken(";");
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        for(ConstDefNode constDefNode : constDefNodes) {
            constDefNode.ErrorCheck();
        }
    }
    @Override
    public Value genIR(){
        SymbolManager.setbTypeNode(bTypeNode);
        if(SymbolManager.isGlobal()) {
            ArrayList<GlobalVar> globalVars = new ArrayList<>();
            for (ConstDefNode constDefNode : constDefNodes) {
                GlobalVar globalVar = (GlobalVar) constDefNode.genIR();
                globalVars.add(globalVar);
            }
            GlobalVars globalVars1 = new GlobalVars("const vars", globalVars);
            return globalVars1;
        } else {
            for (ConstDefNode constDefNode : constDefNodes) {
                constDefNode.genIR();
            }
            return Nothing;
        }
    }
}
