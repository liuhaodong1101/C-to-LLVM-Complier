package LLVM;

public class IRsym  extends Value{
    private boolean isGlobal;
    public IRsym(boolean isGlobal, String name) {
        super(name);
        this.isGlobal = isGlobal;
    }



    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
