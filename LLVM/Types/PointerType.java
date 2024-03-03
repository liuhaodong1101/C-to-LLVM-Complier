package LLVM.Types;

import LLVM.Type;

import static LLVM.Types.BaseType.INT32;

public class PointerType extends Type {
    private Type pointedType;

    public PointerType(Type pointedType) {
        this.pointedType = pointedType;
    }

    public Type getPointedType() {
        return pointedType;
    }
    public static final PointerType INT32P = new PointerType(INT32);

    @Override
    public String toString() {
        return pointedType.toString()+"*";
    }
}