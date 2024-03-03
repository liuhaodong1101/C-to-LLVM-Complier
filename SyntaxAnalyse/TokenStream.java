package SyntaxAnalyse;

import TokenAnalyse.Token;
import TokenAnalyse.TokenType;

import java.util.ArrayList;

public class TokenStream {
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static int pos = -1;
    public static Token getToken(){
        pos++;
        if(pos < tokens.size()){
            return tokens.get(pos);
        } else {
            return new Token(TokenType.NOTHING,"",-1);
        }
    }
    public static TokenType foreSeeAToken(){
        if(pos + 1 < tokens.size()){
            return tokens.get(pos+1).getType();
        }
        else {
            return TokenType.NOTHING;
        }
    }

    public static TokenType foreSeeTwoToken(){
        if(pos + 2 < tokens.size()){
            return tokens.get(pos+2).getType();
        }
        else {
            return TokenType.NOTHING;
        }
    }
    public static ArrayList<Token> getTokens(){
        return tokens;
    }
    public static void setPos(int po){
        pos = po;
    }

    public static int getPos() {
        return pos;
    }
}
