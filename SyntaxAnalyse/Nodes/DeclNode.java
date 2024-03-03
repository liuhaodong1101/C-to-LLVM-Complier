package SyntaxAnalyse.Nodes;

import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;

import java.util.ArrayList;

public class DeclNode extends Node {
    private Node node;
    private int type; // 1 is constDeclNode, 2 is varDeclNode
    public DeclNode(int beginLine,Node node,int type) {
        super(beginLine);
        this.syntaxType = SyntaxType.Decl;
        this.node = node;
        this.type = type;
    }

    @Override
    public void PR() {
        super.PR();
        node.PR();
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        node.ErrorCheck();
    }

    public Value genIR(){
        return node.genIR();
    }
}
