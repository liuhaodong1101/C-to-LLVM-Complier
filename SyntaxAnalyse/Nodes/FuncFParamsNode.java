package SyntaxAnalyse.Nodes;

import LLVM.IRsym;
import LLVM.Param;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

public class FuncFParamsNode extends Node {
    private ArrayList<FuncFParamNode> funcFParamNodes;
    private boolean isSingle;
    public FuncFParamsNode(int beginLine,ArrayList<FuncFParamNode> funcFParamNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.FuncFParams;
        this.funcFParamNodes = funcFParamNodes;
        if(funcFParamNodes.size() == 1){
            isSingle = true;
        } else {
            isSingle = false;
        }
    }

    @Override
    public void PR() {
        super.PR();
        if(isSingle) {
            funcFParamNodes.get(0).PR();
            System.out.println(this);
        } else {
            funcFParamNodes.get(0).PR();
            for(int i = 1;i<funcFParamNodes.size();i++){
                TokenMap.printSingleToken(",");
                funcFParamNodes.get(i).PR();
            }
            System.out.println(this);
        }
    }
    public ArrayList<Integer> getDegree(){
            ArrayList<Integer> arrayList = new ArrayList<>();
            for(FuncFParamNode funcFParamNode:funcFParamNodes) {
                arrayList.add(funcFParamNode.getDegree());
            }
            return arrayList;
    }
    public int getNum(){
        return funcFParamNodes.size();
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        for(FuncFParamNode funcFParamNode:funcFParamNodes) {
            funcFParamNode.ErrorCheck();
        }
    }

    public ArrayList<Param> getParams() {
        ArrayList<Param> params = new ArrayList<>();
        for(FuncFParamNode funcFParamNode :funcFParamNodes){
            params.add((Param)funcFParamNode.genIR());
        }
        return params;
    }
}
