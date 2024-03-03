package LLVM;

public class Value {
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Value Nothing = new Value("NOTHING");

    public Value(String name) {
        this.name = name;
    }
}
