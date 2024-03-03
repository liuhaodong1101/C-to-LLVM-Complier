package SyntaxAnalyse.Nodes;

import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

public class BTypeNode extends Node {
    private int type;

    public int getType() {
        return type;
    }

    public BTypeNode(int beginLine, int type) {
        super(beginLine);
        this.type = type;
        this.syntaxType = SyntaxType.BType;
    }

    @Override
    public void PR() {
        super.PR();
        if(type == 1) {
            TokenMap.printKeyToken("int");
        } else {
            //double
            TokenMap.printKeyToken("int");
        }
    }
}
