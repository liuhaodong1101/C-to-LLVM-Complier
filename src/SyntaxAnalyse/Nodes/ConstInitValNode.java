package SyntaxAnalyse.Nodes;

import LLVM.IRsym;
import LLVM.IRsyms;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class ConstInitValNode extends Node {
    private boolean isSingle;
    private ConstExpNode constExpNode;
    private ArrayList<ConstInitValNode> constInitValNodes;
    public ConstInitValNode(int beginLine,ConstExpNode constExpNode) {
        super(beginLine);
        this.syntaxType = SyntaxType.ConstInitVal;
        this.constExpNode = constExpNode;
        this.isSingle = true;
    }

    public ConstInitValNode(int beginLine,ArrayList<ConstInitValNode> constInitValNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.ConstInitVal;
        this.constInitValNodes = constInitValNodes;
        this.isSingle = false;
    }

    @Override
    public void PR() {
        super.PR();
        if(isSingle) {
            constExpNode.PR();
        } else {
            TokenMap.printSingleToken("{");
            if(!constInitValNodes.isEmpty()){
                constInitValNodes.get(0).PR();
                for(int i = 1;i<constInitValNodes.size();i++){
                    TokenMap.printSingleToken(",");
                    constInitValNodes.get(i).PR();
                }
            }
            TokenMap.printSingleToken("}");
        }
        System.out.println(this);
    }

    public ArrayList<Integer> initValues(){
        ArrayList<Integer> integers = new ArrayList<>();
        if(isSingle) {
            integers.add(constExpNode.eval());
            return integers;
        } else {
            for(int i = 0;i<constInitValNodes.size();i++){
                integers.addAll(constInitValNodes.get(i).initValues());
            }
            return integers;
        }
    }

    @Override
    public Value genIR(){
        ArrayList<IRsym> iRsyms = new ArrayList<>();
        if(isSingle) {
            iRsyms.add(constExpNode.genIR());
            return new IRsyms(iRsyms);
        }else {
           //数组
            for(int i = 0;i<constInitValNodes.size();i++){
                IRsyms iRsyms1 = (IRsyms) constInitValNodes.get(i).genIR();
                iRsyms.addAll(iRsyms1.getiRsyms());
            }
            return new IRsyms(iRsyms);
        }
    }
}
