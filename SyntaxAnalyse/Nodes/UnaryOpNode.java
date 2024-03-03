package SyntaxAnalyse.Nodes;

import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class UnaryOpNode extends Node {
    private int type; //1 add 2 sub 3 not

    public UnaryOpNode(int beginLine, int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.UnaryOp;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            TokenMap.printSingleToken("+");
        } else if(type == 2) {
            TokenMap.printSingleToken("-");
        } else {
            TokenMap.printSingleToken("!");
        }
        System.out.println(this);
    }
}
