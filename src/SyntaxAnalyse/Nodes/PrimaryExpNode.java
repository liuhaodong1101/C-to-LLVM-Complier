package SyntaxAnalyse.Nodes;

import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class PrimaryExpNode extends Node {
    private Node node;
    private int type; //1 exp 2 l val 3 number

    public PrimaryExpNode(int beginLine, Node node, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.PrimaryExp;
        this.node = node;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            TokenMap.printSingleToken("(");
            node.PR();
            TokenMap.printSingleToken(")");
        } else {
            node.PR();
        }
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        node.ErrorCheck();
    }
    public int getDegree(){
        if(type == 1 || type == 3) {
            return 0;
        } else {
            return ((LValNode) node).getDegree();
        }
    }

    @Override
    public Value genIR(){
       return node.genIR();
    }

    public int eval(){
        return node.eval();
    }
}
