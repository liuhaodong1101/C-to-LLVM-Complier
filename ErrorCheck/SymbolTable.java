package ErrorCheck;

import javax.swing.plaf.PanelUI;
import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private SymbolTable father;
    private HashMap<String,FuncSymbol> funcSymbolHashMap;
    private HashMap<String,VarSymbol> varSymbolHashMap;
    private ArrayList<SymbolTable> sons;

    public SymbolTable(SymbolTable father) {
        this.father = father;
        this.funcSymbolHashMap = new HashMap<>();
        this.varSymbolHashMap = new HashMap<>();
        this.sons = new ArrayList<>();
    }
    public void AddFuncSymbol(FuncSymbol funcSymbol){
        funcSymbolHashMap.put(funcSymbol.getName(),funcSymbol);
    }

    public SymbolTable getFather() {
        return father;
    }

    public boolean isContainFuncSymbol(String name) {
        return funcSymbolHashMap.containsKey(name);
    }

    public void AddVarSymbol(VarSymbol varSymbol){
        varSymbolHashMap.put(varSymbol.getName(),varSymbol);
    }
    public boolean isContainVarSymbol(String name) {
        return varSymbolHashMap.containsKey(name);
    }
    public void addSon(SymbolTable symbolTable){
        sons.add(symbolTable);
    }

    public FuncSymbol getFunc(String name) {
        return funcSymbolHashMap.get(name);
    }

    public VarSymbol getVar(String name) {
        return varSymbolHashMap.get(name);
    }


}
