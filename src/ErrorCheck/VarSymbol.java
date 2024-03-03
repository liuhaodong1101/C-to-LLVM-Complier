package ErrorCheck;

import LLVM.Type;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class VarSymbol  {
    private int line;
    private boolean isGlobal;
    private String name;

    private Type type = INT32;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLlvmName() {
        return llvmName;
    }

    public void setLlvmName(String llvmName) {
        this.llvmName = llvmName;
    }

    private String llvmName ="-114514name";
    private int degree;

    private ArrayList<Integer> dims = new ArrayList<>();
    private ArrayList<Integer> values = new ArrayList<>();

    public boolean isConst() {
        return isConst;
    }

    public int getValue() {
        if(values.size()==0) {
            return -114514;
        } else {
            return values.get(0);
        }
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public VarSymbol(String name, int line, int degree, boolean isGlobal) {
        this.name = name;
        this.line = line;
        this.isGlobal = isGlobal;
        this.degree = degree;
    }

    public VarSymbol(String name, int line, boolean isGlobal, ArrayList<Integer> dims, ArrayList<Integer> initVals,Type type,String llvmName) {
        this.name = name;
        this.line = line;
        this.isGlobal = isGlobal;
        this.dims = dims;
        this.degree = dims.size();
        this.values = initVals;
        this.type = type;
        this.llvmName = llvmName;
    }
    public String getName() {
        return name;
    }

    private boolean isConst;

    public int getDegree() {
        return degree;
    }



    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isArr(){
        return this.degree>0;
    }

}
