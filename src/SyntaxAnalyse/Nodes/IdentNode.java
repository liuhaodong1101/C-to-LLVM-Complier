package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenType;

public class IdentNode extends Node {
    private String name;

    private int checkMode = 0;
    public IdentNode(int beginLine,String name) {
        super(beginLine);
        this.name = name;
        this.syntaxType = SyntaxType.Ident;
    }

    public String getName() {
        return name;
    }

    @Override
    public void PR() {
        super.PR();
        System.out.println(TokenType.IDENFR + " " + name);
    }

    public void setCheckMode(int checkMode) {
        this.checkMode = checkMode;
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(checkMode == 1){
            if(!SymbolManager.varHasBeenDefine(name)) {
                ErrorData.AddError(new Error(beginLine,'c'));
            }
        }else{
            if(!SymbolManager.funcHasBeenDefine(name)) {
                ErrorData.AddError(new Error(beginLine,'c'));
            }
        }
    }
}
