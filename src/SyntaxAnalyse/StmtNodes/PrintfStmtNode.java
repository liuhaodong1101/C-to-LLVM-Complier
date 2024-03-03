package SyntaxAnalyse.StmtNodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Param;
import LLVM.Params;
import LLVM.Types.BaseType;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.Nodes.ExpNode;
import SyntaxAnalyse.Nodes.FormatStringNode;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;


public class PrintfStmtNode extends Node {
    private FormatStringNode formatStringNode;
    private ArrayList<ExpNode> expNodes;
    private int type;//1 has exp 2 not has exp

    public PrintfStmtNode(int beginLine, FormatStringNode formatStringNode, ArrayList<ExpNode> expNodes, int type) {
        super(beginLine);
        this.formatStringNode = formatStringNode;
        this.expNodes = expNodes;
        this.type = type;
        this.syntaxType = SyntaxType.PrintfStmt;
    }

    @Override
    public void PR() {
        super.PR();
        TokenMap.printKeyToken("printf");
        TokenMap.printSingleToken("(");
        formatStringNode.PR();
        if(type == 1) {
            for(int i = 0; i< expNodes.size();i++){
                TokenMap.printSingleToken(",");
                expNodes.get(i).PR();
            }
        }
        TokenMap.printSingleToken(")");
        TokenMap.printSingleToken(";");
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(type ==1){
            for (ExpNode expNode:expNodes){
                expNode.ErrorCheck();
            }
        }
        if(formatStringNode.getElementNum()!=expNodes.size()){
            ErrorData.AddError(new Error(beginLine,'l'));
        }
    }

    public Value genIR(){
        String input = formatStringNode.getValue();
        ArrayList<String> strings;
        strings = IRbuilder.genStr(input.substring(1, input.length() - 1));
        int len = expNodes.size();
        ArrayList<IRsym> iRsyms = new ArrayList<>();
        for(int i =0;i<len;i++){
            IRsym iRsym  = (IRsym) expNodes.get(i).genIR();
            iRsyms.add(iRsym);
        }
        int len2 = strings.size();
        int pos = 0;
        for(int i =0;i<len2;i++){
            if(!strings.get(i).equals("%d")) {
                int ascii = strings.get(i).charAt(0);
                Param param = new Param(Integer.toString(ascii),INT32);
                Params params = new Params();
                params.addPara(param);
                Instr instr = new Instr(InstrType.call,null,"putch",params);
                instr.setType(BaseType.VOID);
                IRbuilder.addInstrToBB(instr);
            } else {
                Param param = new Param(iRsyms.get(pos).getName(),INT32);
                pos++;
                Params params = new Params();
                params.addPara(param);
                Instr instr = new Instr(InstrType.call,null,"putint",params);
                instr.setType(BaseType.VOID);
                IRbuilder.addInstrToBB(instr);
            }
        }
        return null;
    }
}
