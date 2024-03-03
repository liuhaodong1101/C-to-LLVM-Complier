package LLVM;

import java.util.ArrayList;
public class GlobalVar extends User{
    private boolean isConst;
    private boolean isArr;

    private String toPrint;
    private ArrayList<Integer> dims;
    private ArrayList<Integer> initVals;

    private Type type;

    public GlobalVar(String name, boolean isConst, ArrayList<Integer>dims,ArrayList<Integer> initVal,Type type) {
        super(name);
        this.isConst = isConst;
        this.toPrint = isConst?"constant":"global";
        this.initVals = initVal;
        this.dims = dims;
        this.type = type;
        this.isArr = dims.size() > 0?true:false;
    }
    public void print(){
        if(dims.size() == 1) {
            System.out.print("@" + name + " = "+toPrint+" " + "[" + dims.get(0) + " x " + type +"] ");
            int initValNum = initVals.size();
            if(isAllZero(0, initValNum)) {
                System.out.println(" zeroinitializer");
            } else {
                System.out.print("[");
                for (int i = 0; i < initValNum - 1; i++) {
                    System.out.print(type + " " + initVals.get(i) + ", ");
                }
                System.out.print(type + " " + initVals.get(initValNum - 1));
                System.out.print("]");
                System.out.println("");
            }
        }  else if(dims.size() == 2) {
            System.out.print("@" + name + " = "+toPrint+" " + "[" + dims.get(0) + " x [" + dims.get(1) +" x "+type +"]] ");
            System.out.print("[");
            int degree1 = dims.get(0);
            int degree2 = dims.get(1);
            int count = 0;
            while(true) {
                System.out.print("[" + dims.get(1) + " x " + type + "] ");
                int offset = count * degree2;
                if(isAllZero(offset,offset+ degree2)) {
                    System.out.print(" zeroinitializer");
                    count++;
                    if(count != degree1) {
                        System.out.print(",");
                    } else {
                        System.out.print("]");
                        break;
                    }
                } else {
                    System.out.print("[");
                    for(int i = offset; i<offset+ degree2 -1; i++) {
                        System.out.print(type + " " + initVals.get(i)+", ");
                    }
                    System.out.print(type + " " + initVals.get(offset+ degree2 -1));
                    count++;
                    if(count != degree1) {
                        System.out.print("],");
                    } else {
                        System.out.print("]]");
                        break;
                    }
                }
            }
            System.out.println("");
        }
        else {
            System.out.println("@" + name + " = "+toPrint+" " + type + " " + initVals.get(0));
        }
    }
    public boolean isAllZero(int begin,int end){
        boolean flag = true;
        for(int i = begin;i<end;i++){
            if(initVals.get(i)!=0){
                flag = false;
                return flag;
            }
        }
        return flag;
    }
}
