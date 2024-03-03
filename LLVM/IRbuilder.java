package LLVM;

import ErrorCheck.SymbolManager;

import java.util.ArrayList;
import java.util.HashMap;

public  class IRbuilder {

    private static HashMap<FunctionDecl, Integer> localVarCntMap = new HashMap<>();
    private static boolean isOnlyExp = false;

    public static void setIsOnlyExp(boolean isOnlyExp) {
        IRbuilder.isOnlyExp = isOnlyExp;
    }

    private static FunctionDecl curFunctionDecl;
    private static BasicBlock curBB;

    public static void setLocalVarCntMap(FunctionDecl functionDecl) {
        localVarCntMap.put(functionDecl,0);
    }

    public static IRsym allocNewSym(){
        IRsym iRsym = new IRsym(false, "%" + getAndAddLocalVarCntMap(curFunctionDecl));
        return iRsym;
    }
    public static IRsym allocConstSym(String value){
        return new IRsym(SymbolManager.isGlobal(),value);
    }

    public static BasicBlock getCurBB() {
        return curBB;
    }


    public static void setCurBB(BasicBlock curBB) {
        IRbuilder.curBB = curBB;
    }

    public static BasicBlock allocAndActiveABB(){
        int tmp = getAndAddLocalVarCntMap(curFunctionDecl);
        BasicBlock basicBlock = new BasicBlock( tmp+"",new ArrayList<>(), curFunctionDecl);
        setCurBB(basicBlock);
        curFunctionDecl.addBB(basicBlock);
        return basicBlock;
    }

    public static BasicBlock allocBB(){
        int tmp = 0;
        BasicBlock basicBlock = new BasicBlock( tmp+"",new ArrayList<>(), curFunctionDecl);
        return basicBlock;
    }
    public static void activeBB(BasicBlock basicBlock) {
        if(curBB.isEmpty()) {
            basicBlock.setName(curBB.getName());
            curFunctionDecl.removeBB(curBB);
            curBB = basicBlock;
            curFunctionDecl.addBB(curBB);
        } else {
            int tmp = getAndAddLocalVarCntMap(curFunctionDecl);
            basicBlock.setName(tmp+"");
            setCurBB(basicBlock);
            curFunctionDecl.addBB(basicBlock);
        }
    }

    public static FunctionDecl newFunc(String name, Type retType){
        FunctionDecl functionDecl = new FunctionDecl(name,new ArrayList<>(),new ArrayList<>(),retType);
        curFunctionDecl = functionDecl;
        return functionDecl;
    }

    public static void addInstrToBB(Instr instr) {
        curBB.addInstr(instr);
        /*if(!curBB.isRetEnd()) {
            curBB.addInstr(instr);
        }*/
    }
    public static int getAndAddLocalVarCntMap(FunctionDecl functionDecl) {
        localVarCntMap.put(functionDecl,localVarCntMap.get(functionDecl) + 1);
        return localVarCntMap.get(functionDecl) -1;
    }


    public static ArrayList<String> genStr(String input){
        char[] arr = input.toCharArray();
        int pos = arr.length;
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0;i<pos;i++) {
            char c = arr[i];
            if (c == '\\' && i < pos -1 && arr[i+1] == 'n') {
                int a= 10;
                char ascii = (char) a;
                strings.add(ascii +" ");
                i++;
            }
            else if (c == '%' && i < pos -1 && arr[i+1] == 'd') {
                strings.add("%d");
                i++;
            } else {
                // 输出字符的ASCII码
                strings.add(c+"");
            }
        }
        return strings;
    }
}