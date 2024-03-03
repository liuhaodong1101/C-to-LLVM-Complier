package LLVM.Types;

import LLVM.Type;

public class BaseType extends Type {
    private String name;

    private BaseType(String name) {
        this.name = name;
    }

    public static final BaseType INT32 = new BaseType("i32");
    public static final BaseType VOID = new BaseType("void");

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
