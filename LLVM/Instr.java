package LLVM;

import ErrorCheck.SymbolManager;
import LLVM.Types.ArrayType;
import LLVM.Types.BaseType;
import LLVM.Types.PointerType;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;

public class Instr extends User{

    protected InstrType instrType;
    protected BasicBlock parentBB;
    protected IRsym distinct;

    protected BasicBlock branch1 = null;
    protected BasicBlock branch2 = null;

    protected IRsym op1;

    public void setOps(ArrayList<IRsym> ops) {
        this.ops = ops;
    }

    protected IRsym op2;

    protected ArrayList<IRsym> ops;

    protected Params params;

    protected String funcName;

    public Instr(InstrType instrType,IRsym distinct,String funcName,Params params) {
        super(distinct!=null?distinct.toString():funcName);
        this.instrType = instrType;
        this.distinct = distinct;
        this.funcName = funcName;
        this.params = params !=null?params:new Params(new ArrayList<>());
        this.parentBB = IRbuilder.getCurBB();
    }

    public Instr(InstrType instrType,IRsym distinct,BasicBlock basicBlock1,BasicBlock basicBlock2) {
        super("br");
        this.instrType = instrType;
        this.distinct = distinct;
        this.branch1 = basicBlock1;
        this.branch2 = basicBlock2;
        this.parentBB = IRbuilder.getCurBB();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    protected Type type = INT32;
    public InstrType getInstrType() {
        return instrType;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Instr(InstrType instrType,IRsym distinct, IRsym op1, IRsym op2) {
        super(distinct!=null?distinct.toString():"NULL");
        this.instrType = instrType;
        this.parentBB = IRbuilder.getCurBB();
        this.distinct = distinct;
        this.op1 = op1;
        this.op2 = op2;
    }
    public void print(){
        // TODO: 2023/10/31 missing i32
        System.out.print("      ");
        if(instrType == InstrType.ret) {
            if(distinct!=null) {
                System.out.println(instrType + " " + type + " " + distinct);
            } else {
                System.out.println(instrType+" void");
            }
        }
        else if(instrType==InstrType.icmpeq) {
            System.out.println(distinct+ " = icmp eq " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.icmpne) {
            System.out.println(distinct+ " = icmp ne " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.icmpslt) {
            System.out.println(distinct+ " = icmp slt " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.icmpsle) {
            System.out.println(distinct+ " = icmp sle " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.icmpsgt) {
            System.out.println(distinct+ " = icmp sgt " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.icmpsge) {
            System.out.println(distinct+ " = icmp sge " + type +" " +  op1 + ", " + op2);
        }
        else if(instrType==InstrType.br) {
            if(distinct!=null) {
                System.out.println(instrType + " i1 " + distinct + ", label " + "%"+branch1.getName() + ", label " + "%" + branch2.getName());
            } else {
                System.out.println(instrType + " label " + "%" + branch1.getName());
            }
        } else if(instrType==InstrType.getelementptr){
            printGetElementPtr();
        }
        else if(instrType == InstrType.zext1_32) {
            System.out.println(distinct + " = " + "zext i1 " + op1 + " to i32");
        }
        else if(instrType == InstrType.alloca) {
            System.out.println(distinct + " = " + instrType + " " +  type);
        } else if(instrType == InstrType.load) {
            System.out.println(distinct + " = " + instrType + " " + type + ", " + type + "* " + op1);
        } else if(instrType == InstrType.store) {
            System.out.println(instrType + " " + type + " " + op1 + ", " + type + "* " + distinct);
        } else if(instrType == InstrType.call) {
            printCall();
        }
        else {
            System.out.println(distinct + " = " + instrType + " "+type+" " + op1 + ", " + op2);
        }
    }
    public void printCall(){
        String fParamStr = "";
        if(funcName == "putint" || funcName == "putch"){
            ArrayList<Param> params2 = params.getParams();
            int len = params2.size();
            for(int i = 0;i<len -1 ;i++){
                fParamStr += "i32" + " " + params2.get(i).getName() + ", ";
            }
            if(len-1>=0) {
                fParamStr += "i32" + " " + params2.get(len-1).getName();
            }
        } else {
            ArrayList<Param> fParams = SymbolManager.getFuncParams(funcName);
            ArrayList<Param> rParams = params.getParams();
            int len = rParams.size();
            for(int i = 0;i<len -1 ;i++){
                fParamStr += fParams.get(i).getType() + " " + rParams.get(i).getName() + ", ";
            }
            if(len-1>=0) {
                fParamStr += fParams.get(len-1).getType() + " " + rParams.get(len-1).getName();
            }
        }
        if(distinct!=null) {
            System.out.println(distinct + " = " + "call " + type + " " +"@"+funcName + "(" + fParamStr + ")");// TODO: 2023/11/3
        } else {
            System.out.println("call " + type +" " +"@"+ funcName + "(" + fParamStr + ")");
        }
    }
    public void printGetElementPtr(){
        if(type instanceof ArrayType) {
            System.out.print(distinct + " = " + instrType + " " + type + ", " + type + "*" + op1 + ", i32 0");
            if(((ArrayType) type).getDim().size() == 2){
                if(ops.size() == 0) {
                    System.out.print(", i32 0");
                }
                if(ops.size() ==1) {
                    System.out.print(", i32 " + ops.get(0).getName() + ", i32 0");
                } else {
                    for (IRsym op : ops) {
                        System.out.print(", i32 " + op.getName());
                    }
                }
            }else if(((ArrayType) type).getDim().size() == 1){
                if(ops.size() == 0) {
                    System.out.print(", i32 0");
                }
                if(ops.size() ==1) {
                    System.out.print(", i32 " + ops.get(0).getName());
                }
            }
            System.out.println("");
        } else if(type instanceof PointerType) {
            Type pointedType = ((PointerType) type).getPointedType();
            System.out.print(distinct + " = " + instrType + " " + pointedType + ", " + pointedType + "*" + op1);
            if(pointedType instanceof BaseType) {
                for (IRsym op : ops) {
                    System.out.print(", i32 " + op.getName());
                }
            } else {
                if(ops.size() == 0) {
                    System.out.print(", i32 0");
                }
                if(ops.size() ==1) {
                    System.out.print(", i32 " + ops.get(0).getName() + ", i32 0");
                } else {
                    for (IRsym iRsym : ops) {
                        System.out.print(", i32 " + iRsym.getName());
                    }
                }
            }
            System.out.println("");
        }
    }
}