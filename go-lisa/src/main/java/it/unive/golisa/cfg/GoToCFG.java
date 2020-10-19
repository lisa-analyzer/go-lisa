package it.unive.golisa.cfg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.calls.*;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literals.*;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.FalseEdge;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.edge.TrueEdge;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NoOp;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.logging.IterationLogger;

/**
 * @GoToCFG manages the translation from a Go program to the
 * corresponding LiSA @CFG.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoToCFG extends GoParserBaseVisitor<Statement> {

	private static final Logger log = LogManager.getLogger(GoToCFG.class); 

	/**
	 * Go program file path.
	 */
	private String filePath;

	/**
	 * List of CFGs collected into the Go program at filePath.
	 */
	private Collection<CFG> cfgs;


	/**
	 * Builds an instance of @GoToCFG for a given Go program
	 * given at the location filePath.
	 *  
	 * @param filePath file path to a Go program.
	 */
	public GoToCFG(String filePath) {
		this.cfgs = new HashSet<CFG>();
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
	public Collection<CFG> getCFGs() {
		return cfgs;
	}

	/**
	 * Current CFG to parse
	 */
	private CFG currentCFG;


	public static void main(String[] args) throws IOException {
		String file = "src/test/resources/go-tutorial/go004.go";
		GoToCFG translator = new GoToCFG(file);
		System.err.println(translator.toLiSACFG().iterator().next().getEdges());
	}

	/**
	 * Returns the collection of @CFG in a Go program at filePath.
	 * 
	 * @return collection of @CFG in file
	 * @throws IOException if {@code stream} to file cannot be written to or closed
	 */
	public Collection<CFG> toLiSACFG() throws IOException {
		log.info("GoToCFG setup...");
		log.info("Reading file... " + filePath);

		InputStream stream;
		try {
			stream = new FileInputStream(getFilePath());
		} catch (FileNotFoundException e) {
			System.err.println(filePath + " does not exist. Exiting.");
			return new ArrayList<>();
		}

		GoLexer lexer = new GoLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		GoParser parser = new GoParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.sourceFile();
		visit(tree);
		stream.close();

		return cfgs;
	}

	@Override
	public Statement visit(ParseTree tree) {

		if (tree instanceof SourceFileContext)
			return visitSourceFile((SourceFileContext) tree);
		else {
			System.err.println(tree.getText());
			return visit(((RuleContext) tree));
		}
	}

	//	@Override 
	//	public Expression visitChildren(RuleNode node) {
	//		return null;
	//	}

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
		return visitChildren(ctx);
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
			size += paramCxt.identifierList().IDENTIFIER().size();



		String[] cfgArgs = new String[size];

		int i = 0;

		//TODO: for the moment, we skip the formal parameter type
		for (ParameterDeclContext paramCxt : formalPars.parameterDecl()) 
			for (ParseTree v : paramCxt.identifierList().IDENTIFIER())
				cfgArgs[i++] = v.getText();

		CFG cfg = new CFG(new CFGDescriptor(filePath, line, col, funcName, cfgArgs));
		log.info(cfg);
		cfgs.add(cfg);

		currentCFG = cfg;
		return visitBlock(ctx.block());		
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
		Statement lastStatement = null;
		for (VarSpecContext varSpec : ctx.varSpec()) 
			lastStatement = visitVarSpec(varSpec);

		return lastStatement;
	}


	@Override
	public Statement visitVarSpec(VarSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();
		
		Statement prev = null;

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText());
			Expression exp = (Expression) visitExpression(exps.expression(i));

			int line = getLine(ctx);
			int col = getCol(ctx);

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, target, exp);
			currentCFG.addNode(asg);

			if (prev != null)
				currentCFG.addEdge(new SequentialEdge(prev, asg));
			prev = asg;

		}

		return prev;
	}

	@Override
	public Statement visitBlock(BlockContext ctx) {
		// Visit the statement list inside the block
		return visitStatementList(ctx.statementList());
	}

	@Override
	public Statement visitStatementList(StatementListContext ctx) {

		Statement previousStmt = null;

		for (int i = 0; i < ctx.statement().size(); i++)  {
			Statement currentStmt = visitStatement(ctx.statement(i));

			if (previousStmt != null) 
				currentCFG.addEdge(new SequentialEdge(previousStmt, getEntryNode(ctx.statement(i))));
			previousStmt = currentStmt;
		}

		return previousStmt;
	}

	@Override
	public Statement visitStatement(StatementContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitSimpleStmt(SimpleStmtContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitExpressionStmt(ExpressionStmtContext ctx) {
		return visitChildren(ctx);
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
		/*
		 * The short variable declaration in only
		 * syntactic sugar for variable declaration
		 * (e.g., x := 5 corresponds to var x int = 5).
		 * Hence, the translation is identical to the one
		 * of a variable declaration.
		 */
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();
		
		Statement prev = null;

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText());
			Expression exp = (Expression) visitExpression(exps.expression(i));

			int line = getLine(ctx);
			int col = getCol(ctx);

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, target, exp);
			currentCFG.addNode(asg);

			if (prev != null)
				currentCFG.addEdge(new SequentialEdge(prev, asg));
			prev = asg;

		}

		return prev;
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

		// Visit If statement Boolean Guard
		Statement booleanGuard = visitExpression(ctx.expression());
		currentCFG.addNode(booleanGuard);

		NoOp ifExitNode = new NoOp(currentCFG);
		currentCFG.addNode(ifExitNode);

		if (ctx.ELSE() == null) {
			// If statement without else branch
			Statement exitStatementTrueBranch = visitBlock(ctx.block(0));
			Statement entryStatementTrueBranch = getEntryNode(ctx.block(0));
			
			currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));			
			currentCFG.addEdge(new FalseEdge(booleanGuard, ifExitNode));			
			currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
		} else {
			// If statement with else branch
			Statement exitStatementTrueBranch = visitBlock(ctx.block(0));
			Statement exitStatementFalseBranch = visitBlock(ctx.block(1));

			Statement entryStatementTrueBranch = getEntryNode(ctx.block(0));
			Statement entryStatementFalseBranch = getEntryNode(ctx.block(1));

			currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
			currentCFG.addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

			currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
			currentCFG.addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
		}
		
		
		if (ctx.simpleStmt() != null) {
			Statement initialStatement = visitSimpleStmt(ctx.simpleStmt());
			currentCFG.addNode(initialStatement);
			currentCFG.addEdge(new SequentialEdge(initialStatement, booleanGuard));
		}
		
		return ifExitNode;
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
	public Expression visitExpression(ExpressionContext ctx) {	

		// Go sum (+)
		if (ctx.PLUS() != null)
			return new GoSum(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go multiplication (*)
		if (ctx.STAR() != null)
			return new GoMul(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go division (/)
		if (ctx.DIV() != null)
			return new GoDiv(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go minus (-)
		if (ctx.MINUS() != null)
			return new GoMinus(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&&)
		if (ctx.LOGICAL_AND() != null) 
			return new GoAnd(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
		
		// Go and (||)
		if (ctx.LOGICAL_OR() != null)
			return new GoOr(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go equals (==)
		if (ctx.EQUALS() != null)
			return new GoEquals(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitPrimaryExpr(PrimaryExprContext ctx) {
		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
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
	public Expression visitOperand(OperandContext ctx) {
		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitLiteral(LiteralContext ctx) {
		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitBasicLit(BasicLitContext ctx) {

		// Go decimal integer
		if (ctx.integer() != null)
			return visitInteger(ctx.integer());

		//TODO: for the moment, we skip any other integer literal format (e.g., octal, imaginary)
		return null;
	}

	@Override
	public Expression visitInteger(IntegerContext ctx) {
		return new GoInteger(currentCFG, Integer.parseInt(ctx.DECIMAL_LIT().getText()));
	}

	@Override
	public Expression visitOperandName(OperandNameContext ctx) {
		if (ctx.IDENTIFIER() != null) 
			return new Variable(currentCFG, ctx.IDENTIFIER().getText());
		
		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
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

	private int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	} 

	private int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	} 
	
	private Statement getEntryNode(ParserRuleContext ctx) {
		if (ctx instanceof BlockContext)
			return getBlockEntryNode((BlockContext) ctx);
		
		if (ctx instanceof IfStmtContext) 
			return getNodeFromContext(((IfStmtContext) ctx).expression());
		
		if (ctx instanceof StatementContext) {
			if (((StatementContext) ctx).ifStmt() != null)
				return getNodeFromContext(((StatementContext) ctx).ifStmt().expression());				
		}
				
		// If ctx is a simple statement (not composite) return this
		return getNodeFromContext(ctx);
	}
	
	private Statement getNodeFromContext(ParserRuleContext ctx) {
		for (Statement node : currentCFG.getNodes()) {
			if (node.getLine() == getLine(ctx) && node.getCol() == getCol(ctx))
				return node;
		}
		
		throw new IllegalStateException("Cannot find the node " + ctx.getText());
	}
	
	private Statement getBlockEntryNode(BlockContext block) {
		return getNodeFromContext(block.statementList().statement(0));
	}
}
