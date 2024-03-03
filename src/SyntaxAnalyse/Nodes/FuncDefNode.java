package SyntaxAnalyse.Nodes;

import ErrorCheck.Error;
import ErrorCheck.ErrorData;
import ErrorCheck.FuncSymbol;
import ErrorCheck.SymbolManager;
import LLVM.BasicBlock;
import LLVM.FunctionDecl;
import LLVM.IRbuilder;
import LLVM.IRsym;
import LLVM.Instr;
import LLVM.InstrType;
import LLVM.Param;
import LLVM.Value;
import SyntaxAnalyse.Node;
import SyntaxAnalyse.SyntaxType;
import TokenAnalyse.TokenMap;

import java.util.ArrayList;

import static LLVM.Types.BaseType.INT32;
import static LLVM.Types.BaseType.VOID;

public class FuncDefNode extends Node {
    private FuncTypeNode funcTypeNode;

    private int endLine;
    private IdentNode identNode;

    private FuncFParamsNode funcFParamsNode = null;
    private BlockNode blockNode;
    private boolean hasFuncFParamsNode;

    public FuncDefNode(int beginLine,FuncTypeNode funcTypeNode, IdentNode identNode, FuncFParamsNode funcFParamsNode, BlockNode blockNode,int endLine) {
        super(beginLine);
        this.syntaxType =  SyntaxType.FuncDef;
        this.funcTypeNode = funcTypeNode;
        this.identNode = identNode;
        this.funcFParamsNode = funcFParamsNode;
        this.blockNode = blockNode;
        this.hasFuncFParamsNode = true;
        this.endLine = endLine;
    }
    public FuncDefNode(int beginLine,FuncTypeNode funcTypeNode, IdentNode identNode, BlockNode blockNode,int endLine) {
        super(beginLine);
        this.syntaxType =  SyntaxType.FuncDef;
        this.funcTypeNode = funcTypeNode;
        this.identNode = identNode;
        this.blockNode = blockNode;
        this.hasFuncFParamsNode = false;
        this.endLine = endLine;
    }

    @Override
    public void PR() {
        super.PR();
        funcTypeNode.PR();
        identNode.PR();
        TokenMap.printSingleToken("(");
        if(hasFuncFParamsNode) {
            funcFParamsNode.PR();
        }
        TokenMap.printSingleToken(")");
        blockNode.PR();
        System.out.println(this);
    }

    @Override
    public void ErrorCheck() {
        super.ErrorCheck();
        if(SymbolManager.isContainVarOrFuncSymbol(identNode.getName())) {
            ErrorData.AddError(new Error(identNode.getBeginLine(),'b'));
        } else {
            int type = funcTypeNode.isVoid() ? 1 : 2;
            int elementNum = funcFParamsNode !=null ? funcFParamsNode.getNum() : 0;
            ArrayList<Integer> degrees = null;
            if(funcFParamsNode!=null) {
                degrees = funcFParamsNode.getDegree();
            }
            FuncSymbol funcSymbol = new FuncSymbol(identNode.getBeginLine(),identNode.getName(),type,elementNum,degrees);
            SymbolManager.AddFuncSymbol(funcSymbol);
        }
        SymbolManager.enterSymbolTable();

        if(funcFParamsNode!=null) {
            funcFParamsNode.ErrorCheck();
        }
        if(funcTypeNode.isVoid()) {
            SymbolManager.setNeedReturnValue(false);
        } else {
            SymbolManager.setNeedReturnValue(true);
        }
        ArrayList<BlockItemNode> blockItemNodes = blockNode.getBlockItemNodes();
        for(BlockItemNode blockStmtNode:blockItemNodes){
            blockStmtNode.ErrorCheck();
        }

        SymbolManager.backSymbolTable();
        if(!blockNode.IsReturnEnd() && !funcTypeNode.isVoid()){
            ErrorData.AddError(new Error(endLine,'g'));
        }
        SymbolManager.setNeedReturnValue(false);
    }
    @Override
    public Value genIR(){
        SymbolManager.setCurFuncName(identNode.getName());
        int type = funcTypeNode.isVoid()?1:2;
        FunctionDecl functionDecl = IRbuilder.newFunc(identNode.getName(),type==2?INT32:VOID);
        int elementNum = hasFuncFParamsNode?funcFParamsNode.getNum():0;
        ArrayList<Integer> degrees = hasFuncFParamsNode?funcFParamsNode.getDegree():null;
        FuncSymbol funcSymbol = new FuncSymbol(beginLine,identNode.getName(),type,elementNum,degrees);
        SymbolManager.AddFuncSymbol(funcSymbol);

        SymbolManager.enterSymbolTable();
        ArrayList<Param> params = new ArrayList<>();
        if(funcFParamsNode!=null) {
           params = funcFParamsNode.getParams();
        }
        funcSymbol.setParams(params);
        functionDecl.setParamList(params);
        BasicBlock basicBlock = IRbuilder.allocAndActiveABB();
        ArrayList<IRsym> paramSyms = new ArrayList<>();
        int paramNum = params.size();
        for(int i =0; i < paramNum;i++){
            paramSyms.add(IRbuilder.allocNewSym());
        }
        for(int i = 0; i <paramNum; i++) {
            Instr allocParam = new Instr(InstrType.alloca, paramSyms.get(i), paramSyms.get(i),null);
            allocParam.setType(params.get(i).getType());
            basicBlock.addInstr(allocParam);
        }
        for(int i = 0; i <paramNum; i++) {
            IRsym originParamName = new IRsym(false,params.get(i).getName());
            Instr storeInstr = new Instr(InstrType.store, paramSyms.get(i), originParamName,null);
            storeInstr.setType(params.get(i).getType());
            SymbolManager.modifyVarNickName(params.get(i).getOriginName(), paramSyms.get(i).getName());
            basicBlock.addInstr(storeInstr);
        }
        ArrayList<BlockItemNode> blockItemNodes = blockNode.getBlockItemNodes();
        for(BlockItemNode blockStmtNode:blockItemNodes){
            blockStmtNode.genIR();
        }
        SymbolManager.backSymbolTable();
        SymbolManager.setCurFuncName("GLOBAL");
        return functionDecl;
    }
}
