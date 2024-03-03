package SyntaxAnalyse.Nodes;

import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;
import TokenAnalyse.TokenType;

public class FuncTypeNode extends Node {
    private boolean isVoid;
    public FuncTypeNode(int beginLine,boolean isVoid) {
        super(beginLine);
        this.syntaxType = SyntaxType.FuncType;
        this.isVoid = isVoid;
    }

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public void PR() {
        super.PR();
        if(isVoid) {
            TokenMap.printKeyToken("void");
        } else {
            TokenMap.printKeyToken("int");
        }
        System.out.println(this);
    }
}
