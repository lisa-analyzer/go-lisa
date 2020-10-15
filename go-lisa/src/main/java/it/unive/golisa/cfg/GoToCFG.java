package it.unive.golisa.cfg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.literals.GoInteger;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.SequentialEdge;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.logging.IterationLogger;

public class GoToCFG extends GoParserBaseVisitor<Statement> {

	private static final Logger log = LogManager.getLogger(GoToCFG.class); 

	/**
	 * Go program file path
	 */
	private String filePath;

	/**
	 * List of CFGs collected into the Go program at filePath
	 */
	private List<CFG> cfgs;

	public GoToCFG(String filePath) {
		this.cfgs = new ArrayList<CFG>();
		this.filePath = filePath;
	}

	/**
	 * Returns the parsed file path.
	 * @return the parsed file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Set the file path
	 * @param filePath the file path
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Returns the parsed CFGs
	 * @return the parsed CFGs
	 */
	public List<CFG> getCFGs() {
		return cfgs;
	}

	/**
	 * Current CFG to parse
	 */
	private CFG currentCFG;

	public static void main(String[] args) throws IOException {
		log.info("GoToCFG setup...");

		String filePath = "src/test/resources/go-tutorial/go001.go";
		GoToCFG interpreter = new GoToCFG(filePath);
		log.info("Reading file..." + filePath);

		InputStream stream;
		try {
			stream = new FileInputStream(interpreter.getFilePath());
		} catch (FileNotFoundException e) {
			System.err.println(filePath + " does not exist. Exiting.");
			return;
		}

		GoLexer lexer = new GoLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		GoParser parser = new GoParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.sourceFile();
		interpreter.visit(tree);
		stream.close();
	}

	@Override
	public Statement visit(ParseTree tree) {
		if (tree instanceof SourceFileContext)
			visitSourceFile((SourceFileContext) tree);
		else {
			visit(((RuleContext) tree));
		}
		
		return null;
	}

	@Override
	public Statement visitTerminal(TerminalNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitErrorNode(ErrorNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSourceFile(SourceFileContext ctx) {
		//TODO: we skip, for the moment package information and imports

		// Visit of each @FunctionDeclContext appearing in the source code
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Parsing function declarations...", "Function declarations")) 
			visitFunctionDecl(funcDecl);

		return null;
	}

	@Override
	public Statement visitPackageClause(PackageClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitImportDecl(ImportDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitImportSpec(ImportSpecContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitImportPath(ImportPathContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitDeclaration(DeclarationContext ctx) {
		visitChildren(ctx);
		return null;
	}

	@Override
	public Statement visitConstDecl(ConstDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitConstSpec(ConstSpecContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitIdentifierList(IdentifierListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExpressionList(ExpressionListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeDecl(TypeDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeSpec(TypeSpecContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitFunctionDecl(FunctionDeclContext ctx) {
		String funcName = ctx.IDENTIFIER().getText();
		SignatureContext signature = ctx.signature();
		ParametersContext formalPars = signature.parameters();

		int line = signature.getStart().getLine();
		int col = signature.getStop().getStopIndex();

		int size = 0;
		for (ParameterDeclContext paramCxt : formalPars.parameterDecl()) 
			size += paramCxt.identifierList().children.size();

		String[] cfgArgs = new String[size];

		int i = 0;

		//TODO: for the moment, we skip the formal parameter type
		for (ParameterDeclContext paramCxt : formalPars.parameterDecl()) 
			for (ParseTree v : paramCxt.identifierList().children)
				cfgArgs[i++] = v.getText();

		CFG cfg = new CFG(new CFGDescriptor(filePath, line, col, funcName, cfgArgs));
		log.info(cfg);
		cfgs.add(cfg);

		currentCFG = cfg;
		visitBlock(ctx.block());		
		return null;
	}

	@Override
	public Statement visitMethodDecl(MethodDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitReceiver(ReceiverContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitVarDecl(VarDeclContext ctx) {
		for (VarSpecContext varSpec : ctx.varSpec()) 
			visitVarSpec(varSpec);

		return null;
	}


	@Override
	public Statement visitVarSpec(VarSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement prev = null;
		
		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText());
			Expression exp = (Expression) visitExpression(exps.expression(i));

			Assignment asg = new Assignment(currentCFG, target, exp);
			currentCFG.addNode(asg);

			if (prev != null)
				currentCFG.addEdge(new SequentialEdge(prev, asg));
			prev = asg;
	
		}

		return null;
	}

	@Override
	public Statement visitBlock(BlockContext ctx) {
		// Visit the statement list inside the block
		visitStatementList(ctx.statementList());
		return null;
	}

	@Override
	public Statement visitStatementList(StatementListContext ctx) {
		for (int i = 0; i < ctx.statement().size(); i++) 
			visitStatement(ctx.statement(i));

		return null;
	}

	@Override
	public Statement visitStatement(StatementContext ctx) {
		visitChildren(ctx);
		return null;
	}

	@Override
	public Statement visitSimpleStmt(SimpleStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExpressionStmt(ExpressionStmtContext ctx) {
		visitChildren(ctx);
		return null;
	}

	@Override
	public Statement visitSendStmt(SendStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitIncDecStmt(IncDecStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitAssignment(AssignmentContext ctx) {		
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Statement visitAssign_op(Assign_opContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitShortVarDecl(ShortVarDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitEmptyStmt(EmptyStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitLabeledStmt(LabeledStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitReturnStmt(ReturnStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitBreakStmt(BreakStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitContinueStmt(ContinueStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitGotoStmt(GotoStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitFallthroughStmt(FallthroughStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitDeferStmt(DeferStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitIfStmt(IfStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSwitchStmt(SwitchStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExprSwitchStmt(ExprSwitchStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExprCaseClause(ExprCaseClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExprSwitchCase(ExprSwitchCaseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeSwitchStmt(TypeSwitchStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeSwitchGuard(TypeSwitchGuardContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeCaseClause(TypeCaseClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeSwitchCase(TypeSwitchCaseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeList(TypeListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSelectStmt(SelectStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitCommClause(CommClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitCommCase(CommCaseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitRecvStmt(RecvStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitForStmt(ForStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitForClause(ForClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitRangeClause(RangeClauseContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitGoStmt(GoStmtContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitType_(Type_Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeName(TypeNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeLit(TypeLitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitArrayType(ArrayTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitArrayLength(ArrayLengthContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitElementType(ElementTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitPointerType(PointerTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitInterfaceType(InterfaceTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSliceType(SliceTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitMapType(MapTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitChannelType(ChannelTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitMethodSpec(MethodSpecContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitFunctionType(FunctionTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSignature(SignatureContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitResult(ResultContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitParameters(ParametersContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitParameterDecl(ParameterDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitExpression(ExpressionContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitPrimaryExpr(PrimaryExprContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitUnaryExpr(UnaryExprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitConversion(ConversionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitOperand(OperandContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitLiteral(LiteralContext ctx) {
		return visitChildren(ctx);

	}

	@Override
	public Statement visitBasicLit(BasicLitContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitInteger(IntegerContext ctx) {
		return new GoInteger(currentCFG, ctx.DECIMAL_LIT().getText());
	}

	@Override
	public Statement visitOperandName(OperandNameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitQualifiedIdent(QualifiedIdentContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitCompositeLit(CompositeLitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitLiteralType(LiteralTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitLiteralValue(LiteralValueContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitElementList(ElementListContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitKeyedElement(KeyedElementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitKey(KeyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitElement(ElementContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitStructType(StructTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitFieldDecl(FieldDeclContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitString_(String_Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitAnonymousField(AnonymousFieldContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitFunctionLit(FunctionLitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitIndex(IndexContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitSlice(SliceContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitTypeAssertion(TypeAssertionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitArguments(ArgumentsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitMethodExpr(MethodExprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visitEos(EosContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
