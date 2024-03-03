package LLVM;

import java.util.ArrayList;

public class Params extends Value{
    ArrayList<Param> params;
    public Params(ArrayList<Param> params) {
        super("Params");
        this.params = params;
    }

    public ArrayList<Param> getParams() {
        return params;
    }

    public Params() {
        super("Params");
        this.params = new ArrayList<>();
    }

    public void addPara(Param param) {
        this.params.add(param);
    }
    @Override
    public String toString() {
        String tmp = "";
        int len = params.size();
        if(len>0) {
            for (int i = 0; i < len - 1; i++) {
                tmp += params.get(i) + ", ";
            }
            tmp += params.get(len - 1);
        }
        return tmp;
    }
}
