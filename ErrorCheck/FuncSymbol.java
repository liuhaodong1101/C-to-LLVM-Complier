package ErrorCheck;

import LLVM.Param;

import java.util.ArrayList;

public class FuncSymbol {
    private int line;

    private ArrayList<Param> params;

    public ArrayList<Param> getParams() {
        return params;
    }

    public void setParams(ArrayList<Param> params) {
        this.params = params;
    }

    private String name;

    private int type; // 1 void 2 int

    private int elementNum;

    public int getType() {
        return type;
    }

    public ArrayList<Integer> getDegrees() {
        return degrees;
    }

    public int getElementNum() {
        return elementNum;
    }

    private ArrayList<Integer> degrees; // 0 int 1 int[] 2 int[][i]

    public FuncSymbol(int line, String name, int type, int elementNum, ArrayList<Integer> degrees) {
        this.line = line;
        this.name = name;
        this.type = type;
        this.elementNum = elementNum;
        this.degrees = degrees;
    }

    public String getName() {
        return name;
    }
}
