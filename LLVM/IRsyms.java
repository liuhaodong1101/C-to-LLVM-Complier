package LLVM;

import java.util.ArrayList;

public class IRsyms extends Value{
    public IRsyms(ArrayList<IRsym> iRsyms) {
        super("IRSYMS");
        this.iRsyms = iRsyms;
    }

    public ArrayList<IRsym> getiRsyms() {
        return iRsyms;
    }

    ArrayList<IRsym> iRsyms;

}
