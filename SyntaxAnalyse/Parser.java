package SyntaxAnalyse;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.SymbolManager;
import SyntaxAnalyse.Nodes.*;
import SyntaxAnalyse.StmtNodes.AssignStmtNode;
import SyntaxAnalyse.StmtNodes.BlockStmtNode;
import SyntaxAnalyse.StmtNodes.BreakStmtNode;
import SyntaxAnalyse.StmtNodes.ContinueStmtNode;
import SyntaxAnalyse.StmtNodes.ExpStmtNode;
import SyntaxAnalyse.StmtNodes.ForNode;
import SyntaxAnalyse.StmtNodes.GetIntStmtNode;
import SyntaxAnalyse.StmtNodes.IfStmtNode;
import SyntaxAnalyse.StmtNodes.PrintfStmtNode;
import SyntaxAnalyse.StmtNodes.ReturnStmtNode;
import TokenAnalyse.Token;
import TokenAnalyse.TokenType;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Token curToken;
    private static int curLine = 0;
    private static int lastLine = 0;
    private static boolean isErrorCheck = true;
    private static int expFalse = 0;
    public static void getSym(){
        curToken = TokenStream.getToken();
        lastLine = curLine;
        curLine = curToken.getLineNum();

    }

    public static void resetSym(){
        TokenStream.setPos(-1);
    }
    public static void test() {
        getSym();
        CompUnitNode compUnitNode = CompUnit();
        compUnitNode.ErrorCheck();
        if(Error.count != 0){
            try {
                PrintStream fileOut = new PrintStream("error.txt");
                System.setOut(fileOut);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ErrorData.sortErr();
            ErrorData.Print();
        } else {
            isErrorCheck = false;
            try {
                PrintStream fileOut = new PrintStream("llvm_ir.txt");
                System.setOut(fileOut);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            resetSym();
            SymbolManager.resetSym();
            getSym();
            CompUnitNode compUnitNode1 = CompUnit();
            compUnitNode1.genIR();
        }
    }
    public static CompUnitNode CompUnit(){
        ArrayList<DeclNode> declNodes = ZeroOrMoreDecl();
        ArrayList<FuncDefNode> funcDefNodes = ZeroOrMoreFuncDef();
        MainFuncDefNode mainFuncDefNode = mainFuncDef();
        CompUnitNode compUnitNode = new CompUnitNode(curLine,declNodes,funcDefNodes,mainFuncDefNode);
        return compUnitNode;
    }
    public static ArrayList<DeclNode> ZeroOrMoreDecl(){
        ArrayList<DeclNode> declNodes = new ArrayList<>();
        while(curToken.getType() == TokenType.CONSTTK || TokenStream.foreSeeTwoToken() != TokenType.LPARENT) {
            DeclNode declNode = decl();
            declNodes.add(declNode);
        }
        return declNodes;
    }
    public static ArrayList<FuncDefNode> ZeroOrMoreFuncDef(){
        ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
        while(TokenStream.foreSeeAToken() != TokenType.MAINTK) {
            FuncDefNode funcDefNode = funcDef();
            funcDefNodes.add(funcDefNode);
        }
        return funcDefNodes;
    }
    public static ConstDeclNode ConstDecl(){
        int line = curLine;
        getSym();
        BTypeNode bTypeNode = BTypeNode();
        ArrayList<ConstDefNode> constDefNodes = new ArrayList<>();
        ConstDefNode constDefNode = ConstDef();
        constDefNodes.add(constDefNode);
        while(curToken.getType() == TokenType.COMMA){
            getSym();
            ConstDefNode constDefNode1 = ConstDef();
            constDefNodes.add(constDefNode1);
        }
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(lastLine,'i'));
        } else {
            getSym();
        }
        return new ConstDeclNode(line,constDefNodes,bTypeNode);
    }
    public static VarDeclNode VarDecl(){
        int line = curLine;
        BTypeNode bTypeNode = BTypeNode();
        ArrayList<VarDefNode> varDefNodes = new ArrayList<>();
        VarDefNode varDefNode = varDef();
        varDefNodes.add(varDefNode);
        while(curToken.getType() == TokenType.COMMA){
            getSym();
            VarDefNode varDefNode1 = varDef();
            varDefNodes.add(varDefNode1);
        }
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(lastLine,'i'));
        } else {
            getSym();
        }
        return new VarDeclNode(line,varDefNodes,bTypeNode);
    }
    public static BTypeNode BTypeNode(){
        BTypeNode bTypeNode = new BTypeNode(curLine,1);
        getSym();
        return bTypeNode;
    }
    public static ConstDefNode ConstDef(){
        int line = curLine;
        IdentNode identNode = ident();
        ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
        while(curToken.getType() == TokenType.LBRACK){
            getSym();
            ConstExpNode constExpNode = constExp();
            if(curToken.getType()!=TokenType.RBRACK) {
                ErrorData.AddError(new Error(lastLine,'k'));
            } else {
                getSym();
            }
            constExpNodes.add(constExpNode);
        }
        getSym(); // "="
        ConstInitValNode constInitValNode = ConstInitVal();
        return new ConstDefNode(line,identNode,constExpNodes,constInitValNode);
    }
    public static IdentNode ident(){
        int line = curLine;
        String value = curToken.getValue();
        getSym();
        return new IdentNode(line,value);
    }
    public static ConstExpNode constExp(){
        int line = curLine;
        AddExpNode addExpNode = addExp();
        return new ConstExpNode(line,addExpNode);
    }
    public static ConstInitValNode ConstInitVal(){
        int line = curLine;
        if(curToken.getType() == TokenType.LBRACE) {
            getSym();
            ArrayList<ConstInitValNode> constInitValNodes = new ArrayList<>();
            if(curToken.getType() == TokenType.RBRACE){
                getSym();
                return new ConstInitValNode(line,constInitValNodes);
            } else {
                ConstInitValNode constInitValNode = ConstInitVal();
                constInitValNodes.add(constInitValNode);
                while(curToken.getType() == TokenType.COMMA) {
                    getSym();
                    constInitValNodes.add(ConstInitVal());
                }
                getSym();
                return new ConstInitValNode(line,constInitValNodes);
            }
        } else {
            ConstExpNode constExpNode = constExp();
            return new ConstInitValNode(line,constExpNode);
        }
    }
    public static VarDefNode varDef(){
        int line = curLine;
        IdentNode identNode = ident();
        ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
        while(curToken.getType() == TokenType.LBRACK) {
            getSym();
            constExpNodes.add(constExp());
            if(curToken.getType()!=TokenType.RBRACK) {
                ErrorData.AddError(new Error(lastLine,'k'));
            } else {
                getSym();
            }
        }
        if(curToken.getType() == TokenType.ASSIGN) {
            getSym();
            InitValNode initValNode = initVal();
            return new VarDefNode(line,identNode,constExpNodes,initValNode);
        } else {
            return new VarDefNode(line,identNode,constExpNodes);
        }
    }
    public static InitValNode initVal(){
        int line = curLine;
        if(curToken.getType() == TokenType.LBRACE) {
            getSym();
            ArrayList<InitValNode> initValNodes = new ArrayList<>();
            if(curToken.getType() == TokenType.RBRACE){
                getSym();
                return new InitValNode(line,initValNodes);
            } else {
                InitValNode initValNode = initVal();
                initValNodes.add(initValNode);
                while(curToken.getType() == TokenType.COMMA) {
                    getSym();
                    initValNodes.add(initVal());
                }
                getSym();
                return new InitValNode(line,initValNodes);
            }
        } else {
            ExpNode expNode = exp();
            return new InitValNode(line,expNode);
        }
    }
    public static ExpNode exp(){
        int line = curLine;
        AddExpNode addExpNode = addExp();
        return new ExpNode(line,addExpNode);
    }
    public static FuncDefNode funcDef(){
        int line = curLine;
        FuncTypeNode funcTypeNode = funcType();
        IdentNode identNode = ident();
        getSym();
        if(curToken.getType() != TokenType.RPARENT) { //f(FP--- OR f({}
            if(curToken.getType() == TokenType.LBRACE) { // f({
                ErrorData.AddError(new Error(lastLine,'j'));
                BlockNode blockNode = block();
                return new FuncDefNode(line,funcTypeNode,identNode,blockNode,lastLine);
            } else { //f(fp---
                FuncFParamsNode funcFParamsNode = funcFParams();
                if(curToken.getType()!=TokenType.RPARENT) {
                    ErrorData.AddError(new Error(lastLine,'j'));
                } else {
                    getSym();
                }
                BlockNode blockNode = block();
                return new FuncDefNode(line,funcTypeNode,identNode,funcFParamsNode,blockNode,lastLine);
            }
        } else { // f()
            getSym();
            BlockNode blockNode = block();
            return new FuncDefNode(line,funcTypeNode,identNode,blockNode,lastLine);
        }
    }

    public static FuncTypeNode funcType(){
        int line = curLine;
        if(curToken.getType() == TokenType.VOIDTK) {
            getSym();
            return new FuncTypeNode(line,true);
        } else {
            getSym();
            return new FuncTypeNode(line,false);
        }
    }
    public static FuncFParamsNode funcFParams(){
        int line = curLine;
        ArrayList<FuncFParamNode> funcFParamNodes = new ArrayList<>();
        FuncFParamNode funcFParamNode = funcFParam();
        funcFParamNodes.add(funcFParamNode);
        while(curToken.getType() == TokenType.COMMA) {
            getSym();
            funcFParamNode= funcFParam();
            funcFParamNodes.add(funcFParamNode);
        }
        return new FuncFParamsNode(line,funcFParamNodes);
    }
    public static BlockNode block(){
        int line = curLine;
        getSym();
        boolean flag = false;
        ArrayList<BlockItemNode> blockItemNodes = new ArrayList<>();
        while(curToken.getType() != TokenType.RBRACE) {
            BlockItemNode blockItemNode = blockItem();
            if(flag == false || isErrorCheck) {
                blockItemNodes.add(blockItemNode);
            }
            if(blockItemNode.isBreakOrContinue()){
                flag = true;
            }
        }
        getSym();
        return new BlockNode(line,blockItemNodes);
    }

    public static MainFuncDefNode mainFuncDef(){
        int line = curLine;
        getSym();
        getSym();
        getSym();
        getSym();
        BlockNode blockNode = block();
        return new MainFuncDefNode(line,blockNode,lastLine);
    }
    public static FuncFParamNode funcFParam(){
        int line = curLine;
        BTypeNode bTypeNode = BTypeNode();
        IdentNode identNode = ident();
        if(curToken.getType() == TokenType.LBRACK){
            getSym();
            if(curToken.getType()!=TokenType.RBRACK) {
                ErrorData.AddError(new Error(lastLine,'k'));
            } else {
                getSym();
            }
            ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
            while(curToken.getType() == TokenType.LBRACK) {
                getSym();
                ConstExpNode constExpNode = constExp();
                if(curToken.getType()!=TokenType.RBRACK) {
                    ErrorData.AddError(new Error(lastLine,'k'));
                } else {
                    getSym();
                }
                constExpNodes.add(constExpNode);
            }
            if(!constExpNodes.isEmpty()) {
                return new FuncFParamNode(line,bTypeNode,identNode,constExpNodes,3);
            } else {
                return new FuncFParamNode(line,bTypeNode,identNode,constExpNodes,2);
            }
        } else {
            return new FuncFParamNode(line,bTypeNode,identNode,null,1);
        }
    }
    public static BlockItemNode blockItem(){
        int line = curLine;
        if(curToken.getType() == TokenType.CONSTTK || curToken.getType() == TokenType.INTTK) {
            DeclNode declNode = decl();
            return new BlockItemNode(line,declNode,false);
        } else {
            StmtNode stmtNode = stmt();
            return new BlockItemNode(line,stmtNode,true);
        }
    }

    public static DeclNode decl(){
        int line = curLine;
        if(curToken.getType() == TokenType.CONSTTK) {
            ConstDeclNode constDeclNode = ConstDecl();
            return new DeclNode(line,constDeclNode,1);
        } else {
            VarDeclNode varDeclNode = VarDecl();
            return new DeclNode(line,varDeclNode,2);
        }
    }
    public static StmtNode stmt(){
        int line = curLine;
        if(curToken.getType() == TokenType.IFTK) {
            IfStmtNode ifStmtNode = ifStmt();
            return new StmtNode(line,ifStmtNode,4);
        } else if (curToken.getType() == TokenType.FORTK){
            ForNode forNode = forNode();
            return new StmtNode(line,forNode,5);
        } else if(curToken.getType() == TokenType.BREAKTK) {
            BreakStmtNode breakStmtNode = breakStmt();
            return new StmtNode(line,breakStmtNode,6);
        } else if(curToken.getType() == TokenType.CONTINUETK) {
            ContinueStmtNode continueStmtNode = continueStmt();
            return new StmtNode(line,continueStmtNode,7);
        } else if(curToken.getType() == TokenType.RETURNTK) {
            ReturnStmtNode returnStmtNode = returnStmt();
            return new StmtNode(line,returnStmtNode,8);
        } else if(curToken.getType()==TokenType.PRINTFTK){
            PrintfStmtNode printfStmtNode = printfStmt();
            return new StmtNode(line,printfStmtNode,10);
        } else if(curToken.getType() == TokenType.LBRACE) {
            BlockStmtNode blockStmtNode = blockStmt();
            return new StmtNode(line,blockStmtNode,3);
        } else if(curToken.getType() == TokenType.SEMICN) {
            getSym();
            ExpStmtNode expStmtNode = new ExpStmtNode(line);
            return new StmtNode(line,expStmtNode,2);
        }
        else {
            int curPos = TokenStream.getPos();
            LValNode lValNode = lVal();
            if(curToken.getType() == TokenType.ASSIGN){
                getSym();
                if(curToken.getType() == TokenType.GETINTTK) {
                    TokenStream.setPos(curPos-1);
                    getSym();
                    GetIntStmtNode getIntStmtNode = getIntStmt();
                    return new StmtNode(line,getIntStmtNode,9);
                } else {
                    TokenStream.setPos(curPos-1);
                    getSym();
                    AssignStmtNode assignStmtNode = assignStmt();
                    return new StmtNode(line,assignStmtNode,1);
                }
            } else {
                TokenStream.setPos(curPos-1);
                getSym();
                ExpStmtNode expStmtNode = expStmt();
                return new StmtNode(line,expStmtNode,2);
            }
        }
    }
    public static AssignStmtNode assignStmt(){
        int line = curLine;
        LValNode lValNode = lVal();
        getSym();
        ExpNode expNode = exp();
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(lastLine,'i'));
        } else {
            getSym();
        }
        return new AssignStmtNode(line,lValNode,expNode);
    }

    public static ExpStmtNode expStmt(){
        int line = curLine;
        if(curToken.getType()!=TokenType.SEMICN) {
            ExpNode expNode = exp();
            if(curToken.getType()!=TokenType.SEMICN) {
                ErrorData.AddError(new Error(lastLine,'i'));
            } else {
                getSym();
            }
            return new ExpStmtNode(line,expNode);
        } else {
            getSym();
            return new ExpStmtNode(line);
        }
    }

    public static BlockStmtNode blockStmt(){
        int line = curLine;
        BlockNode blockNode = block();
        return new BlockStmtNode(line,blockNode);
    }
    public static IfStmtNode ifStmt(){
        int line = curLine;
        getSym();
        getSym();
        CondNode condNode = cond();
        if(curToken.getType()!=TokenType.RPARENT) {
            ErrorData.AddError(new Error(lastLine,'j'));
        } else {
            getSym();
        }
        StmtNode stmtNode = stmt();
        if(curToken.getType() == TokenType.ELSETK) {
            getSym();
            StmtNode stmtNode1 = stmt();
            return new IfStmtNode(line,condNode,stmtNode,stmtNode1,1);
        } else {
            return new IfStmtNode(line,condNode,stmtNode,2);
        }
    }
    public static ForNode forNode(){
        int line = curLine;
        getSym();
        getSym();
        ForStmtNode forStmtNode = null;
        CondNode condNode = null;
        ForStmtNode forStmtNode1 = null;
        StmtNode stmtNode = null;
        if(curToken.getType() != TokenType.SEMICN) {
            forStmtNode =  forStmt();
            getSym();
        } else {
            getSym();
        }

        if(curToken.getType() != TokenType.SEMICN) {
            condNode = cond();
            getSym();
        } else {
            getSym();
        }

        if(curToken.getType() != TokenType.RPARENT) {
            forStmtNode1 = forStmt();
            if(curToken.getType()!=TokenType.RPARENT) {
                ErrorData.AddError(new Error(lastLine,'j'));
            } else {
                getSym();
            }
        } else {
            getSym();
        }
        stmtNode = stmt();
        return new ForNode(line,forStmtNode,forStmtNode1,condNode,stmtNode,1);
    }
    public static CondNode cond(){
        int line = curLine;
        LOrExpNode lOrExpNode = lOrExp();
        return new CondNode(line,lOrExpNode);
    }
    public static ForStmtNode forStmt(){
        int line = curLine;
        LValNode lValNode = lVal();
        getSym();
        ExpNode expNode = exp();
        return new ForStmtNode(line,lValNode,expNode);
    }
    public static BreakStmtNode breakStmt(){
        int line = curLine;
        getSym();
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(line,'i'));
        } else {
            getSym();
        }
        return new BreakStmtNode(line);
    }
    public static ContinueStmtNode continueStmt(){
        int line = curLine;
        getSym();
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(line,'i'));
        } else {
            getSym();
        }
        return new ContinueStmtNode(line);
    }
    public static ReturnStmtNode returnStmt(){
        int line = curLine;
        getSym();
        int curPos = TokenStream.getPos();
        expFalse = 0;
        ExpNode expNode = exp();
        TokenStream.setPos(curPos-1);
        getSym();
        if(expFalse == 1) { // return ; or                  return
                            //                              if (a+1)
            expFalse = 0;
            if(curToken.getType()!=TokenType.SEMICN) {
                ErrorData.AddError(new Error(lastLine,'i'));
            } else {
                getSym();
            }
            return new ReturnStmtNode(line, null, 2);
        } else {
            expNode = exp();
            if(curToken.getType()!=TokenType.SEMICN) {
                ErrorData.AddError(new Error(lastLine,'i'));
            } else {
                getSym();
            }
            return new ReturnStmtNode(line,expNode,1);
        }
    }
    public static GetIntStmtNode getIntStmt(){
        int line = curLine;
        LValNode lValNode = lVal();
        getSym();
        getSym();
        getSym();
        if(curToken.getType()!=TokenType.RPARENT) {
            ErrorData.AddError(new Error(lastLine,'j'));
        } else {
            getSym();
        }
        if(curToken.getType()!=TokenType.SEMICN){
            ErrorData.AddError(new Error(lastLine,'i'));
        }else {
            getSym();
        }
        return new GetIntStmtNode(line,lValNode);
    }
    public static PrintfStmtNode printfStmt(){
        int line = curLine;
        getSym();
        getSym();
        String value = curToken.getValue();
        String pattern = "^\"(%d|([\\x20\\x21\\x28-\\x5B\\x5D-\\x7E]|\\\\n))*\"$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(value);
        if(!m.matches()) {
            ErrorData.AddError(new Error(curLine,'a'));
        }
        FormatStringNode formatStringNode = new FormatStringNode(curLine,value);
        getSym();
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        while(curToken.getType() == TokenType.COMMA) {
            getSym();
            ExpNode expNode = exp();
            expNodes.add(expNode);
        }
        if(curToken.getType()!=TokenType.RPARENT) {
            ErrorData.AddError(new Error(lastLine,'j'));
        } else {
            getSym();
        }
        if(curToken.getType()!=TokenType.SEMICN) {
            ErrorData.AddError(new Error(lastLine,'i'));
        } else {
            getSym();
        }
        if(!expNodes.isEmpty()) {
            return new PrintfStmtNode(line,formatStringNode,expNodes,1);
        } else {
            return new PrintfStmtNode(line,formatStringNode,expNodes,2);
        }
    }
    public static AddExpNode addExp(){
        int line = curLine;
        ArrayList<MulExpNode> mulExpNodes = new ArrayList<>();
        ArrayList<Integer> opts = new ArrayList<>();
        MulExpNode mulExpNode = mulExp();
        mulExpNodes.add(mulExpNode);
        while(curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU) {
            int opt;
            if(curToken.getType() == TokenType.PLUS) {
                opt = 1;
                opts.add(opt);
            } else {
                opt = 2;
                opts.add(opt);
            }
            getSym();
            MulExpNode mulExpNode1 = mulExp();
            mulExpNodes.add(mulExpNode1);
        }
        if(mulExpNodes.size() == 1) {
            return new AddExpNode(line,mulExpNode,null,1,1);
        } else {
            AddExpNode firstAddExpNode = new AddExpNode(line,mulExpNode,null,1,1);
            AddExpNode beforeAddExpNode = firstAddExpNode;
            AddExpNode newAddExpNode = null;
            for(int i =1;i<mulExpNodes.size();i++) {
                MulExpNode mulExpNodeTmp = mulExpNodes.get(i);
                int opt = opts.get(i-1);
                newAddExpNode = new AddExpNode(line,mulExpNodeTmp,beforeAddExpNode,2,opt);
                beforeAddExpNode = newAddExpNode;
            }
            return newAddExpNode;
        }
    }
    public static LOrExpNode lOrExp(){
        int line = curLine;
        ArrayList<LAndExpNode> lAndExpNodes = new ArrayList<>();
        LAndExpNode lAndExpNode = lAndExp();
        lAndExpNodes.add(lAndExpNode);
        while(curToken.getType() == TokenType.OR) {
            getSym();
            LAndExpNode lAndExpNode1 = lAndExp();
            lAndExpNodes.add(lAndExpNode1);
        }
        if(lAndExpNodes.size() == 1) {
            return new LOrExpNode(line,null,lAndExpNode,1);
        } else {
            LOrExpNode firstAddExpNode = new LOrExpNode(line,null,lAndExpNode,1);
            LOrExpNode beforeAddExpNode = firstAddExpNode;
            LOrExpNode newLorExpNode = null;
            for(int i =1;i<lAndExpNodes.size();i++) {
                LAndExpNode lAndExpNodeTmp = lAndExpNodes.get(i);
                newLorExpNode = new LOrExpNode(line,beforeAddExpNode,lAndExpNodeTmp,2);
                beforeAddExpNode = newLorExpNode;
            }
            return newLorExpNode;
        }
    }
    public static LValNode lVal(){
        int line = curLine;
        IdentNode identNode = ident();
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        while(curToken.getType() == TokenType.LBRACK) {
            getSym();
            expNodes.add(exp());
            if(curToken.getType()!=TokenType.RBRACK) {
                ErrorData.AddError(new Error(lastLine,'k'));
            } else {
                getSym();
            }
        }
        return new LValNode(line,identNode,expNodes,expNodes.size() + 1);
    }
    public static PrimaryExpNode primaryExp(){
        int line = curLine;
        if(curToken.getType() == TokenType.LPARENT){
            getSym();
            ExpNode expNode = exp();
            getSym();
            return new PrimaryExpNode(line,expNode,1);
        } else if(curToken.getType() == TokenType.IDENFR){
            LValNode lValNode = lVal();
            return new PrimaryExpNode(line,lValNode,2);
        } else if(curToken.getType() == TokenType.INTCON){
            NumberNode numberNode = numberNode();
            return new PrimaryExpNode(line,numberNode,3);
        } else {
            expFalse = 1;
            return null;
        }
    }
    public static NumberNode numberNode(){
        int line = curLine;
        IntConstNode intConstNode = intConst();
        return new NumberNode(line,intConstNode);
    }

    public static IntConstNode intConst(){
        int line = curLine;
        String value = curToken.getValue();
        getSym();
        return new IntConstNode(line,value);
    }

    public static UnaryOpNode unaryOp(){
        int line = curLine;
        int opt;
        if(curToken.getType() == TokenType.PLUS) {
            opt = 1;
        } else if(curToken.getType() == TokenType.MINU) {
            opt = 2;
        } else {
            opt = 3;
        }
        getSym();
        return new UnaryOpNode(line,opt);
    }

    public static UnaryExpNode unaryExp() {
        int line = curLine;
        if(curToken.getType() == TokenType.IDENFR && TokenStream.foreSeeAToken() == TokenType.LPARENT) {//
            IdentNode identNode = ident();
            getSym();
            int curPos = TokenStream.getPos();
            expFalse = 0;
            FuncRParamsNode funcRParamsNode = funcRParams();
            TokenStream.setPos(curPos - 1);
            getSym();
            if(expFalse == 1) {
                expFalse = 0;
                if(curToken.getType() != TokenType.RPARENT) {
                    ErrorData.AddError(new Error(lastLine,'j'));
                }else {
                    getSym();
                }
                return new UnaryExpNode(line,null,identNode,null,null,null,2);
            } else {
                funcRParamsNode = funcRParams();
                if(curToken.getType() != TokenType.RPARENT) {
                    ErrorData.AddError(new Error(lastLine,'j'));
                }else {
                    getSym();
                }
                return new UnaryExpNode(line,null,identNode,funcRParamsNode,null,null,3);
            }
        }  else if (curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU || curToken.getType() == TokenType.NOT){
            UnaryOpNode unaryOpNode = unaryOp();
            UnaryExpNode unaryExpNode = unaryExp();
            return new UnaryExpNode(line,null,null,null,unaryOpNode,unaryExpNode,4);
        } else {
            PrimaryExpNode primaryExpNode = primaryExp();
            return new UnaryExpNode(line,primaryExpNode,null,null,null,null,1);
        }
    }

    public static FuncRParamsNode funcRParams(){
        int line = curLine;
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        ExpNode expNode = exp();
        expNodes.add(expNode);
        while(curToken.getType() == TokenType.COMMA) {
            getSym();
            expNode = exp();
            expNodes.add(expNode);
        }
        return new FuncRParamsNode(line,expNodes);
    }

    public static MulExpNode mulExp(){
        int line = curLine;
        ArrayList<UnaryExpNode> unaryExpNodes = new ArrayList<>();
        ArrayList<Integer> opts = new ArrayList<>();
        UnaryExpNode unaryExpNode = unaryExp();
        unaryExpNodes.add(unaryExpNode);
        while(curToken.getType() == TokenType.MULT || curToken.getType() == TokenType.DIV || curToken.getType() == TokenType.MOD) {
            int opt;
            if(curToken.getType() == TokenType.MULT) {
                opt = 1;
            } else if(curToken.getType() == TokenType.DIV){
                opt = 2;
            } else {
                opt = 3;
            }
            opts.add(opt);
            getSym();
            UnaryExpNode unaryExpNode1 = unaryExp();
            unaryExpNodes.add(unaryExpNode1);
        }
        if(unaryExpNodes.size() == 1) {
            return new MulExpNode(line,unaryExpNode,null,1,1);
        } else {
            MulExpNode firstMulExpNode = new MulExpNode(line,unaryExpNode,null,1,1);
            MulExpNode beforeMulExpNode = firstMulExpNode;
            MulExpNode newMulExpNode = null;
            for(int i =1;i<unaryExpNodes.size();i++) {
                UnaryExpNode unaryExpNodeTmp = unaryExpNodes.get(i);
                int opt = opts.get(i-1);
                newMulExpNode = new MulExpNode(line,unaryExpNodeTmp,beforeMulExpNode,2,opt);
                beforeMulExpNode = newMulExpNode;
            }
            return newMulExpNode;
        }
    }

    public static RelExpNode relExp(){
        int line = curLine;
        ArrayList<AddExpNode> addExpNodes = new ArrayList<>();
        ArrayList<Integer> opts = new ArrayList<>();
        AddExpNode addExpNode = addExp();
        addExpNodes.add(addExpNode);
        while(curToken.getType() == TokenType.LSS || curToken.getType() == TokenType.LEQ
                || curToken.getType() == TokenType.GRE || curToken.getType() == TokenType.GEQ) {
            int opt;
            if(curToken.getType() == TokenType.LSS) {
                opt = 1;
            } else if(curToken.getType() == TokenType.LEQ){
                opt = 3;
            } else if(curToken.getType() == TokenType.GRE) {
                opt = 2;
            }
            else {
                opt = 4;
            }
            opts.add(opt);
            getSym();
            AddExpNode addExpNode1 = addExp();
            addExpNodes.add(addExpNode1);
        }
        if(addExpNodes.size() == 1) {
            return new RelExpNode(line,addExpNode,null,1,1);
        } else {
            RelExpNode firstRelExpNode = new RelExpNode(line,addExpNode,null,1,1);
            RelExpNode beforeRelExpNode = firstRelExpNode;
            RelExpNode newRelExpNode = null;
            for(int i =1;i<addExpNodes.size();i++) {
                AddExpNode addExpNodeTmp = addExpNodes.get(i);
                int opt = opts.get(i-1);
                newRelExpNode = new RelExpNode(line,addExpNodeTmp,beforeRelExpNode,2,opt);
                beforeRelExpNode = newRelExpNode;
            }
            return newRelExpNode;
        }
    }
    public static EqExpNode eqExp(){
        int line = curLine;
        ArrayList<RelExpNode> relExpNodes = new ArrayList<>();
        ArrayList<Integer> opts = new ArrayList<>();
        RelExpNode relExpNode = relExp();
        relExpNodes.add(relExpNode);
        while(curToken.getType() == TokenType.EQL || curToken.getType() == TokenType.NEQ) {
            int opt;
            if(curToken.getType() == TokenType.EQL) {
                opt = 1;
            } else {
                opt = 2;
            }
            opts.add(opt);
            getSym();
            RelExpNode relExpNode1 = relExp();
            relExpNodes.add(relExpNode1);
        }
        if(relExpNodes.size() == 1) {
            return new EqExpNode(line,relExpNode,null,1,1);
        } else {
            EqExpNode firstEqExpNode = new EqExpNode(line,relExpNode,null,1,1);
            EqExpNode beforEqExpNode = firstEqExpNode;
            EqExpNode newEqExpNode = null;
            for(int i =1;i<relExpNodes.size();i++) {
                RelExpNode relExpNodeTmp = relExpNodes.get(i);
                int opt = opts.get(i-1);
                newEqExpNode = new EqExpNode(line,relExpNodeTmp,beforEqExpNode,2,opt);
                beforEqExpNode = newEqExpNode;
            }
            return newEqExpNode;
        }
    }
    public static LAndExpNode lAndExp(){
        int line = curLine;
        ArrayList<EqExpNode> eqExpNodes = new ArrayList<>();
        EqExpNode eqExpNode = eqExp();
        eqExpNodes.add(eqExpNode);
        while(curToken.getType() == TokenType.AND) {
            getSym();
            EqExpNode eqExpNode1 = eqExp();
            eqExpNodes.add(eqExpNode1);
        }
        if(eqExpNodes.size() == 1) {
            return new LAndExpNode(line,eqExpNode,null,1);
        } else {
            LAndExpNode firstLAndExpNode = new LAndExpNode(line,eqExpNode,null,1);
            LAndExpNode beforLAndExpNode = firstLAndExpNode;
            LAndExpNode newLAndExpNode = null;
            for(int i =1;i<eqExpNodes.size();i++) {
                EqExpNode eqExpNodeTmp = eqExpNodes.get(i);
                newLAndExpNode = new LAndExpNode(line,eqExpNodeTmp,beforLAndExpNode,2);
                beforLAndExpNode = newLAndExpNode;
            }
            return newLAndExpNode;
        }
    }
}
