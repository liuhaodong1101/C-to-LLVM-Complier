package TokenAnalyse;

import java.util.HashMap;

public class TokenMap {
    private static HashMap<String,TokenType> keyMap = new HashMap<>();
    private static HashMap<String,TokenType> singleOpMap = new HashMap<>();

    private static HashMap<String,TokenType> doubleOpMap = new HashMap<>();
    static {
        keyMap.put("main",TokenType.MAINTK);
        keyMap.put("const",TokenType.CONSTTK);
        keyMap.put("int",TokenType.INTTK);
        keyMap.put("break",TokenType.BREAKTK);
        keyMap.put("continue",TokenType.CONTINUETK);
        keyMap.put("if",TokenType.IFTK);
        keyMap.put("else",TokenType.ELSETK);
        keyMap.put("for",TokenType.FORTK);
        keyMap.put("getint",TokenType.GETINTTK);
        keyMap.put("printf",TokenType.PRINTFTK);
        keyMap.put("return",TokenType.RETURNTK);
        keyMap.put("void",TokenType.VOIDTK);
        singleOpMap.put("!",TokenType.NOT);
        singleOpMap.put("+",TokenType.PLUS);
        singleOpMap.put("-",TokenType.MINU);
        singleOpMap.put("*",TokenType.MULT);
        singleOpMap.put("/",TokenType.DIV);
        singleOpMap.put("%",TokenType.MOD);
        singleOpMap.put("<",TokenType.LSS);
        singleOpMap.put(">",TokenType.GRE);
        singleOpMap.put("=",TokenType.ASSIGN);
        singleOpMap.put(";",TokenType.SEMICN);
        singleOpMap.put(",",TokenType.COMMA);
        singleOpMap.put("(",TokenType.LPARENT);
        singleOpMap.put(")",TokenType.RPARENT);
        singleOpMap.put("[",TokenType.LBRACK);
        singleOpMap.put("]",TokenType.RBRACK);
        singleOpMap.put("{",TokenType.LBRACE);
        singleOpMap.put("}",TokenType.RBRACE);
        doubleOpMap.put("&&",TokenType.AND);
        doubleOpMap.put("||",TokenType.OR);
        doubleOpMap.put("<=",TokenType.LEQ);
        doubleOpMap.put(">=",TokenType.GEQ);
        doubleOpMap.put("==",TokenType.EQL);
        doubleOpMap.put("!=",TokenType.NEQ);
        doubleOpMap.put("//",TokenType.SINGLE_LINE_COMMENT);
        doubleOpMap.put("/*",TokenType.BEGIN_MULTILINE_COMMENT);
        doubleOpMap.put("*/",TokenType.END_MULTILINE_COMMENT);
    }
    public TokenMap() {
    }

    public static TokenType checkKeyMap(String s){
        TokenType t = keyMap.get(s);
        if(t == null) {
            return TokenType.NOTHING;
        } else {
            return t;
        }
    }
    public static TokenType checkSingleOpMap(String s){
        TokenType t = singleOpMap.get(s);
        if(t == null) {
            return TokenType.NOTHING;
        } else {
            return t;
        }
    }
    public static TokenType checkDoubleOpMap(String s){
        TokenType t = doubleOpMap.get(s);
        if(t == null) {
            return TokenType.NOTHING;
        } else {
            return t;
        }
    }

    public static void printKeyToken(String s){
        System.out.println(keyMap.get(s) + " " + s);
    }

    public static void printSingleToken(String s){
        System.out.println(singleOpMap.get(s) + " " + s);
    }

    public static void printDoubleToken(String s){
        System.out.println(doubleOpMap.get(s) + " " + s);
    }
}
