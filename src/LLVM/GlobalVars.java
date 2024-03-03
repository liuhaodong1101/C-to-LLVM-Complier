package LLVM;

import java.util.ArrayList;

public class GlobalVars extends Value{
    ArrayList<GlobalVar> globalVars;

    public GlobalVars(String name, ArrayList<GlobalVar> globalVars) {
        super(name);
        this.globalVars = globalVars;
    }
    public void print(){
        for(GlobalVar globalVar :globalVars){
            globalVar.print();
        }
    }
}
