package LLVM;

import java.util.ArrayList;
import java.util.LinkedList;

public class Module extends Value{
    private  ArrayList<GlobalVars> globalVarList;
    private  ArrayList<FunctionDecl> functionDeclList;
    private  FunctionDecl mainFunction;

    public Module(ArrayList<GlobalVars> globalVarList, ArrayList<FunctionDecl> functionDeclList,FunctionDecl mainFunction) {
        super(null);
        this.globalVarList = globalVarList;
        this.functionDeclList = functionDeclList;
        this.mainFunction = mainFunction;
    }
    public void print(){
        printUsingFunc();
        for(GlobalVars globalVars:globalVarList){
            globalVars.print();
        }
        for(FunctionDecl functionDecl:functionDeclList){
            functionDecl.print();
        }
        mainFunction.print();
    }

    public void printUsingFunc(){
        System.out.print("declare i32 @getint()\n"+
                "declare void @putint(i32)\n"+
                "declare void @putch(i32)\n"+
                "declare void @putstr(i8*)\n");
    }
}
