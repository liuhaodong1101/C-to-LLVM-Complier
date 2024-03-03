package LLVM;


import java.util.ArrayList;

public class FunctionDecl extends User{
    // 基本信息
    private ArrayList<Param> paramList;
    private ArrayList<BasicBlock> BBList;

    public void setParamList(ArrayList<Param> paramList) {
        this.paramList = paramList;
    }

    private Type retType;

    public FunctionDecl(String name, ArrayList<Param> paramList, ArrayList<BasicBlock> BBList, Type retType) {
        super(name);
        this.paramList = paramList;
        this.BBList = BBList;
        this.retType = retType;
        IRbuilder.setLocalVarCntMap(this);
    }

    public void print() {
        String paramI = "";
        int paramNum = paramList.size();
        for(int i = 0; i < paramNum; i++) {
            if(i != paramNum -1) {
                paramI += paramList.get(i) + ",";
            } else {
                paramI += paramList.get(i);
            }
        }
        System.out.println("define "+ retType.toString() + " @" + name +"(" + paramI + ") {");

     //
        int BbNum = BBList.size();
        if(!BBList.get(BbNum -1).isRetEnd()) {
            Instr retIns = new Instr(InstrType.ret,null,new IRsym(false,""),null);
            BBList.get(BbNum -1).addInstr(retIns);
        }
        for(BasicBlock basicBlock:BBList) {
            System.out.println(basicBlock.getName()+":");
            basicBlock.print();
        }
        System.out.println("}");
    }

    public void addBB(BasicBlock basicBlock){
        BBList.add(basicBlock);
    }

    public void removeBB(BasicBlock basicBlock){
        BBList.remove(basicBlock);
    }
}