package SyntaxAnalyse.Nodes;

import ErrorCheck.ErrorData;
import LLVM.IRsym;
import LLVM.Param;
import LLVM.Params;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class FuncRParamsNode extends Node {
    private ArrayList<ExpNode> expNodes;

    public FuncRParamsNode(int beginLine, ArrayList<ExpNode> expNodes) {
        super(beginLine);
        this.syntaxType = SyntaxType.FuncRParams;
        this.expNodes = expNodes;
    }

    @Override
    public void PR() {
        super.PR();
        expNodes.get(0).PR();
        for(int i = 1;i<expNodes.size();i++){
            TokenMap.printSingleToken(",");
            expNodes.get(i).PR();
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        for(ExpNode expNode:expNodes){
            expNode.ErrorCheck();
        }
    }
    public int getRpNum(){
        return expNodes.size();
    }
    public ArrayList<Integer> getDegrees(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(ExpNode expNode:expNodes) {
            arrayList.add(expNode.getDegree());
        }
        return arrayList;
    }

    public Params getParams() {
        ArrayList<Param> params = new ArrayList<>();
        for(ExpNode expNode:expNodes){
            IRsym iRsym = expNode.genIR();
            Param param = new Param(iRsym.getName(),INT32);
            params.add(param);
        }
        Params params1 = new Params(params);
        return params1;
    }
}
