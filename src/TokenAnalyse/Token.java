package TokenAnalyse;

public class Token {
    private TokenType type;
    private String value;

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNum() {
        return lineNum;
    }

    public Token(TokenType type, String value, int lineNum) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
    private int lineNum;
}
