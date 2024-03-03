package LLVM.Types;

import LLVM.Type;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class ArrayType extends Type {
    private Type elementType;
    private ArrayList<Integer> dim;

    public ArrayType(Type elementType, ArrayList<Integer> dim) {
        this.elementType = elementType;
        this.dim = dim;
    }

    public ArrayList<Integer> getDim() {
        return dim;
    }
    public int getDegree(){
        return dim.size();
    }
    @Override
    public String toString() {
        String str;
        int len = dim.size();
        int t1 = dim.get(len -1);
        str = "[" + t1 + " x " + elementType+ "]";
        for(int j = len -2;j>=0;j--) {
            str = "[" + dim.get(j) + " x " + str + "]";
        }
        return str;
    }
}