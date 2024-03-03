package ErrorCheck;

public class Error {
    int line;
    char type;
    public static int count;
    public int getLine() {
        return line;
    }

    public char getType() {
        return type;
    }

    public Error(int line, char type) {
        this.line = line;
        this.type = type;
        count++;
    }
}
