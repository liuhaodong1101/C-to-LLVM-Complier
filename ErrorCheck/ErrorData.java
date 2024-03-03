package ErrorCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ErrorData {
    private static ArrayList<Error> errors = new ArrayList<>();
    public static void AddError(Error error){
        errors.add(error);
    }
    public static void sortErr(){
        Comparator<Error> lineComparator = Comparator.comparingInt(error -> error.line);
        Collections.sort(errors, lineComparator);
    }
    public static void Print() {
        for(Error error : errors){
            System.out.println(error.getLine() + " " + error.getType());
        }
    }
}
