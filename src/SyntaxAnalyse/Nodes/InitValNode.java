package SyntaxAnalyse.Nodes;

import LLVM.IRsym;
import LLVM.IRsyms;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class InitValNode extends Node {
    private boolean isSingle;
    private ExpNode expNode;
    private ArrayList<InitValNode> initValNodes;
    public InitValNode(int beginLine,ExpNode expNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.InitVal;
        this.isSingle = true;
        this.expNode = expNode;
    }
    public InitValNode(int beginLine,ArrayList<InitValNode> initValNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.InitVal;
        this.isSingle = false;
        this.initValNodes = initValNodes;
    }


    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(isSingle) {
            expNode.ErrorCheck();
        } else {
            for(InitValNode initValNode : initValNodes) {
                initValNode.ErrorCheck();
            }
        }
    }

    @Override
    public void PR() {
        super.PR();
        if(isSingle){
            expNode.PR();
        } else {
            TokenMap.printSingleToken("{");
            if(!initValNodes.isEmpty()) {
                initValNodes.get(0).PR();
                for(int i =1;i<initValNodes.size();i++){
                    TokenMap.printSingleToken(",");
                    initValNodes.get(i).PR();
                }
            }
            TokenMap.printSingleToken("}");
        }
        System.out.println(this);
    }

    public ArrayList<Integer> initValues(){
        ArrayList<Integer> integers = new ArrayList<>();
        if(isSingle) {
            integers.add(expNode.eval());
            return integers;
        } else {
            for(int i = 0;i<initValNodes.size();i++){
                integers.addAll(initValNodes.get(i).initValues());
            }
            return integers;
        }
    }

    @Override
    public Value genIR(){
        ArrayList<IRsym> initValList = new ArrayList<>();
        if(isSingle) {
            initValList.add(expNode.genIR());
            return new IRsyms(initValList);
        }else {
            for(int i = 0;i<initValNodes.size();i++){
                IRsyms initVals = (IRsyms) initValNodes.get(i).genIR();
                initValList.addAll(initVals.getiRsyms());
            }
            return new IRsyms(initValList);
        }
    }
}
