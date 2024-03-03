package TokenAnalyse;

import SyntaxAnalyse.TokenStream;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class Lexer {
    private static int lineNum = 1;

    private static boolean in_single_line_comment = false;

    private static boolean in_multiline_comment = false;

    private static TokenMap tokenMap = new TokenMap();

    public static ArrayList<Token> tokenArrayList = TokenStream.getTokens();

    public static boolean isLetterOrUnderscore(char c) {
        return Character.isLetter(c) || c == '_';
    }

    public static boolean isLetterOrUnderscoreOrDigit(char c) {
        return Character.isLetter(c) || c == '_' || Character.isDigit(c);
    }

    public static int doSys(PushbackInputStream p) throws IOException {
        int a = p.read();
        char ch = (char) a;
        String curStr = "";
        while(ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t') {
            if(ch == '\n') {
                lineNum++;
                in_single_line_comment = false;
            }
            a = p.read();
            ch = (char) a;
        }
        if(a == -1) {
            return -1;
        }
        if(in_single_line_comment) {
            return 0;
        }
        if(in_multiline_comment) {
            curStr += ch;
            int tmp = p.read();
            if (tmp == -1) {
                return -1;
            } else {
                String tmpStr = curStr + (char) tmp;
                if(tokenMap.checkDoubleOpMap(tmpStr) == TokenType.END_MULTILINE_COMMENT) {
                    in_multiline_comment = false;
                } else {
                    p.unread(tmp);//back tmp
                }
                return 0;
            }
        }
        if(isLetterOrUnderscore(ch)) {
            curStr +=ch;
            int tmp;
            while((tmp = p.read()) != -1) {
                ch = (char) tmp;
                if(isLetterOrUnderscoreOrDigit(ch)) curStr +=ch;
                else {
                    p.unread(ch);
                    break;
                }
            }
            if(tokenMap.checkKeyMap(curStr) == TokenType.NOTHING){
                Token curToken = new Token(TokenType.IDENFR, curStr,lineNum);
                tokenArrayList.add(curToken);
                return 0;
            } else {
                Token curToken = new Token(tokenMap.checkKeyMap(curStr), curStr,lineNum);
                tokenArrayList.add(curToken);
                return 0;
            }
        }


        if(Character.isDigit(ch)) {//digit
            curStr+=ch;
            int tmp;
            while((tmp = p.read()) != -1) {
                ch = (char) tmp;
                if(Character.isDigit(ch)) curStr+=ch;
                else {
                    p.unread(ch);
                    break;
                }
            }
            Token curToken = new Token(TokenType.INTCON, curStr,lineNum);
            tokenArrayList.add(curToken);
            return 0;
        }

        if(ch == '\"') { // FormatString
            curStr+=ch;
            int tmp;
            while((tmp = p.read()) != -1) {
                ch = (char) tmp;
                curStr+=ch;
                if(ch == '\"') break;
            }
            Token curToken = new Token(TokenType.STRCON, curStr,lineNum);
            tokenArrayList.add(curToken);
            return 0;
        } else {
            curStr += ch;
            int tmp = p.read();
            if (tmp == -1) { //  doubleOp impossible
                Token curToken = new Token(tokenMap.checkSingleOpMap(curStr), curStr, lineNum);
                tokenArrayList.add(curToken);
                return -1;
            } else { // doubleOp possible
                String tmpStr = curStr + (char) tmp;
                if (tokenMap.checkDoubleOpMap(tmpStr) != TokenType.NOTHING) {//is double
                    curStr = tmpStr;
                    Token curToken = new Token(tokenMap.checkDoubleOpMap(curStr), curStr, lineNum);
                    if(tokenMap.checkDoubleOpMap(tmpStr) == TokenType.SINGLE_LINE_COMMENT) {
                        in_single_line_comment = true;
                    } else if (tokenMap.checkDoubleOpMap(tmpStr) == TokenType.BEGIN_MULTILINE_COMMENT) {
                        in_multiline_comment = true;
                    } else {
                        tokenArrayList.add(curToken);
                    }
                    return 0;
                } else { // not doubleOp
                    p.unread(tmp); // back tmp
                    Token curToken = new Token(tokenMap.checkSingleOpMap(curStr), curStr, lineNum);
                    tokenArrayList.add(curToken);
                    return 0;
                }
            }
        }
    }
}
