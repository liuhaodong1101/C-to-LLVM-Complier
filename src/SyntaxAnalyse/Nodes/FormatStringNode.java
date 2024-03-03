package SyntaxAnalyse.Nodes;

import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenType;

public class FormatStringNode extends Node {
    private String value;

    public String getValue() {
        return value;
    }

    public FormatStringNode(int beginLine, String value) {
        super(beginLine);
        this.syntaxType = SyntaxType.FormatString;
        this.value = value;
    }

    @Override
    public void PR() {
        super.PR();
        System.out.println(TokenType.STRCON + " " + value);
    }

    public  int getElementNum() {
        int count = 0;
        String b = "%d";
        int index = value.indexOf(b);
        while (index != -1) {
            count++;
            index = value.indexOf(b, index + 1);
        }
        return count;
    }
}
