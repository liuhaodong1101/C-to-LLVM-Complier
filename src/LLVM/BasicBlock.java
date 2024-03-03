package LLVM;


import java.util.ArrayList;

public class BasicBlock extends Value {
    private ArrayList<Instr> instrList;
    private FunctionDecl parentFunctionDecl;

    public ArrayList<Instr> getInstrList() {
        return instrList;
    }

    public BasicBlock(String name, ArrayList<Instr> instrList, FunctionDecl parentFunctionDecl) {
        super(name);
        this.instrList = instrList;
        this.parentFunctionDecl = parentFunctionDecl;
    }
    public void addInstr(Instr instr){
        instrList.add(instr);
    }
    public BasicBlock(String name) {
        super(name);
    }

    public void print(){
        for(Instr instr :instrList){
            instr.print();
        }
    }

    public boolean isRetEnd(){
        int len = instrList.size();
        if(len == 0 ) return false;
        Instr instr = instrList.get(len-1);
        if(instr.getInstrType()!=InstrType.ret && instr.getInstrType()!=InstrType.br) {
            return false;
        } else {
            return true;
        }
    }
    public void addEmptyInstr(){
        instrList.add(new Instr(InstrType.empty,null,new IRsym(false,""),null));
    }
    public void removeEmptyInstr(){
        instrList.remove(0);
    }

    public boolean isEmpty(){
        return instrList.isEmpty();
    }
}