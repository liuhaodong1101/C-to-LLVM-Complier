package ErrorCheck;

import LLVM.Param;
import LLVM.Type;
import LLVM.Types.BaseType;
import SyntaxAnalyse.Nodes.BTypeNode;

import java.util.ArrayList;

public  class SymbolManager {
    private static SymbolTable symbolTable = new SymbolTable(null);
    private static SymbolTable root = symbolTable;

    public static void setCurFuncName(String curFuncName) {
        SymbolManager.curFuncName = curFuncName;
    }

    private static String curFuncName = "GLOBAL";

    public static BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public static void setbTypeNode(BTypeNode bTypeNode) {
        SymbolManager.bTypeNode = bTypeNode;
    }

    private static BTypeNode bTypeNode;

    private static int ForDepth = 0;

    public static boolean isIsInForNode() {
        return ForDepth>0;
    }

    public static void addForDepth() {
        ForDepth++;
    }
    public static void subForDepth() {
        ForDepth--;
    }
    private static boolean needReturnValue = false;

    public static void setNeedReturnValue(boolean needReturnValue) {
        SymbolManager.needReturnValue = needReturnValue;
    }

    public static boolean isNeedReturnValue() {
        return needReturnValue;
    }

    public static void enterSymbolTable(){
        SymbolTable symbolTable1 = new SymbolTable(symbolTable);
        symbolTable.addSon(symbolTable1);
        symbolTable = symbolTable1;
    }
    public static void backSymbolTable(){
        symbolTable = symbolTable.getFather();
    }
    public static void AddFuncSymbol(FuncSymbol funcSymbol){
        symbolTable.AddFuncSymbol(funcSymbol);
    }
    public static void AddVarSymbol(VarSymbol varSymbol){
        symbolTable.AddVarSymbol(varSymbol);
    }

    public static boolean isContainVarOrFuncSymbol(String name) {
        return symbolTable.isContainFuncSymbol(name) || symbolTable.isContainVarSymbol(name);
    }

    public static boolean varHasBeenDefine(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return true;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }

    public static String getVarNickName(String name) {
        if(!varHasBeenDefine(name)) {
            return "-1145145";
        }
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).getLlvmName();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return "-1145145";
    }

    public static void modifyVarNickName(String name,String newNickName) {
        if(!varHasBeenDefine(name)) {
            return;
        }
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                 symbolTable1.getVar(name).setLlvmName(newNickName);
                 break;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
    }

    public static boolean getVarIsGlobal(String name) {
        if(!varHasBeenDefine(name)) {
            return false;
        }
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).isGlobal();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }


    public static boolean funcHasBeenDefine(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return true;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }


    public static void resetSym(){
        symbolTable = new SymbolTable(null);
        root = symbolTable;
        curFuncName = "GLOBAL";
        ForDepth = 0;
        needReturnValue = false;
    }

    public static int getFPNum(String name){
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getElementNum();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return -999;
    }

    public static Type getFunType(String name){
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getType()==1?BaseType.VOID:BaseType.INT32;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return BaseType.VOID;
    }

    public static boolean retIsVoid(String name){
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getType()==1;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }

    public static int getFuncReturnDegree(String name){
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getType() - 2;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return -999;
    }
    public static ArrayList<Param> getFuncParams(String name){
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getParams();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return null;
    }

    public static boolean isInFuncParams(String varName){
        String name = curFuncName;
        SymbolTable symbolTable1 = symbolTable;
        if(curFuncName.equals("GLOBAL")) {
            return false;
        }
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                ArrayList<Param> params = symbolTable1.getFunc(name).getParams();
                for(Param param:params){
                    if(param.getOriginName().equals(varName)) {
                        return true;
                    }
                }
                return false;
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }

    public static int getVarDegree(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).getDegree();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return -999;
    }

    public static Type getVarType(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).getType();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return null;
    }



    public static ArrayList<Integer> getFuncDegrees(String name) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainFuncSymbol(name) && !symbolTable1.isContainVarSymbol(name)) {
                return symbolTable1.getFunc(name).getDegrees();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return arrayList;
    }
    public static boolean isVarConst(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).isConst();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }

    public static boolean isVarArray(String name) {
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).isArr();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return false;
    }



    public static boolean isGlobal(){
        return symbolTable.getFather() == null;
    }

    public static int getSingleGlobalVarValue(String name) {
        //修改了
        SymbolTable symbolTable1 = symbolTable;
        while (symbolTable1!=null) {
            if(symbolTable1.isContainVarSymbol(name) && !symbolTable1.isContainFuncSymbol(name)) {
                return symbolTable1.getVar(name).getValue();
            }
            else {
                symbolTable1 = symbolTable1.getFather();
            }
        }
        return -114514;
    }
}
