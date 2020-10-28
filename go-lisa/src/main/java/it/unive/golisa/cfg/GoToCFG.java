package it.unive.golisa.cfg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.calls.unary.*;
import it.unive.golisa.cfg.calls.binary.*;
import it.unive.golisa.cfg.custom.GoAssignment;
import it.unive.golisa.cfg.custom.GoConstantDeclaration;
import it.unive.golisa.cfg.custom.GoReturn;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.*;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoIntType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.FalseEdge;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.edge.TrueEdge;
import it.unive.lisa.cfg.statement.CFGCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NoOp;
import it.unive.lisa.cfg.statement.Parameter;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;
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
	private final String filePath;

	/**
	 * Collection of CFGs collected into the Go program at filePath.
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
		String file = "src/test/resources/go-tutorial/func-decl/fun001.go";
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
			return visit(((RuleContext) tree));
		}
	}

	//	@Override 
	//	public Expression visitChildren(RuleNode node) {
	//				throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());

	//	}

	//	@Override
	//	public Statement visitTerminal(TerminalNode node) {
	//		// TODO Auto-generated method stub
	//		throw new UnsupportedOperationException("Unsupported translation: " + node.getText());
	//
	//	}
	//
	//	@Override
	//	public Statement visitErrorNode(ErrorNode node) {
	//		// TODO Auto-generated method stub
	//		throw new UnsupportedOperationException("Unsupported translation: " + node.getText());
	//	}

	@Override
	public Statement visitSourceFile(SourceFileContext ctx) {
		//TODO: we skip, for the moment package information and imports
		Statement lastStatement = null;

		// Visit of each FunctionDeclContext appearing in the source code
		// and creating the corresponding CFG object (we do this to handle CFG calls)
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Parsing function declarations...", "Function declarations"))
			cfgs.add(new CFG(buildCFGDescriptor(funcDecl)));

		// Visit of each FunctionDeclContext populating the corresponding cfg
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Visiting function declarations...", "Function declarations")) {
			currentCFG = getCFGByName(funcDecl.IDENTIFIER().getText());
			visitFunctionDecl(funcDecl);
		}

		return lastStatement;
	}

	/**
	 * Retrieve a cfg given its name.
	 * 
	 * @param name	the name
	 * @return	the cfg named name
	 */
	private CFG getCFGByName(String name) {
		for (CFG cfg : cfgs)
			if (cfg.getDescriptor().getName().equals(name))
				return cfg;

		throw new IllegalStateException("CFG not found: " + name);
	}

	private CFGDescriptor buildCFGDescriptor(FunctionDeclContext funcDecl) {
		String funcName = funcDecl.IDENTIFIER().getText();
		SignatureContext signature = funcDecl.signature();
		ParametersContext formalPars = signature.parameters();

		int line = getLine(signature);
		int col = getCol(signature);

		int size = 0;
		for (ParameterDeclContext paramCxt : formalPars.parameterDecl()) 
			size += paramCxt.identifierList().IDENTIFIER().size();

		Parameter[] cfgArgs = new Parameter[size];

		int i = 0;

		for (ParameterDeclContext paramCxt : formalPars.parameterDecl()) 
			for (Parameter p : getParameters(paramCxt)) 
				cfgArgs[i++] = p;

		return new CFGDescriptor(filePath, line, col, funcName, getGoReturnType(funcDecl.signature()), cfgArgs);
	}


	/**
	 * Given a parameter declaration context, parse the context and 
	 * returns an array of the corresponding {@link Parameter}.
	 * 
	 * @param paramCtx	the parameter context
	 * @return an array of {@link Parameter}, c
	 */
	private Parameter[] getParameters(ParameterDeclContext paramCtx) {
		Parameter[] args = new Parameter[paramCtx.identifierList().IDENTIFIER().size()];
		Type argType = getGoType(paramCtx.type_());
		for (int i = 0; i < paramCtx.identifierList().IDENTIFIER().size(); i++)
			args[i] = new Parameter(paramCtx.identifierList().IDENTIFIER(i).getText(), argType);
		return args;
	}

	private Type getGoReturnType(SignatureContext signature) {
		if (signature.result() == null)
			return Untyped.INSTANCE;
		else if (signature.result().type_() != null)
			return getGoType(signature.result().type_());

		throw new UnsupportedOperationException("Unsupported return type: " + signature.getText());
	}

	@Override
	public Statement visitPackageClause(PackageClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportDecl(ImportDeclContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportSpec(ImportSpecContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportPath(ImportPathContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitDeclaration(DeclarationContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Statement visitConstDecl(ConstDeclContext ctx) {
		Statement lastStmt = null;
		for (ConstSpecContext constSpec : ctx.constSpec()) {
			Statement currStmt = visitConstSpec(constSpec);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, getEntryNode(constSpec)));
			lastStmt = currStmt;
		}

		return lastStmt;
	}

	@Override
	public Statement visitConstSpec(ConstSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement prev = null;

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText());
			Expression exp = visitExpression(exps.expression(i));

			int line = getLine(ids.IDENTIFIER(i).getSymbol());
			int col = getCol(exps.expression(i));

			GoConstantDeclaration asg = new GoConstantDeclaration(currentCFG, filePath, line, col, target, exp);
			currentCFG.addNode(asg);
			if (prev != null)
				currentCFG.addEdge(new SequentialEdge(prev, asg));
			prev = asg;
		}

		return prev;
	}

	@Override
	public Statement visitIdentifierList(IdentifierListContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitExpressionList(ExpressionListContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeDecl(TypeDeclContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeSpec(TypeSpecContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitFunctionDecl(FunctionDeclContext ctx) {
		return visitBlock(ctx.block());		
	}

	@Override
	public Statement visitMethodDecl(MethodDeclContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitReceiver(ReceiverContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitVarDecl(VarDeclContext ctx) {
		Statement lastStmt = null;
		for (VarSpecContext varSpec : ctx.varSpec()) {
			Statement currStmt = visitVarSpec(varSpec);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, getEntryNode(varSpec)));
			lastStmt = currStmt;
		}

		return lastStmt;
	}

	@Override
	public Statement visitVarSpec(VarSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement prevStmt = null;
		Type type = getGoType(ctx.type_());
		
		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);
			Expression exp = (Expression) visitExpression(exps.expression(i));

			int line = getLine(ids.IDENTIFIER(i).getSymbol());
			int col = getCol(exps.expression(i));

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, target, exp);
			currentCFG.addNode(asg);

			if (prevStmt != null)
				currentCFG.addEdge(new SequentialEdge(prevStmt, asg));
			prevStmt = asg;
		}

		return prevStmt;
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
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitIncDecStmt(IncDecStmtContext ctx) {

		Expression exp = visitExpression(ctx.expression());

		if (exp instanceof Variable) {
			// TODO: at the moment, we support only increment and decrement of single variable
			// e.g., x++, x--
			int line = getLine(ctx);
			int col = getCol(ctx);

			Statement asg = null;

			// increment and decrement statements are syntactic sugar
			// e.g., x++ -> x = x + 1 and x-- -> x = x - 1
			if (ctx.PLUS_PLUS() != null)
				asg = new GoAssignment(currentCFG, filePath, line, col, (Variable) exp, 
						new GoSum(currentCFG, (Variable) exp,  new GoInteger(currentCFG, 1)));		
			else
				asg = new GoAssignment(currentCFG, filePath, line, col, (Variable) exp, 
						new GoSubtraction(currentCFG, (Variable) exp,  new GoInteger(currentCFG, 1)));

			currentCFG.addNode(asg);
			return asg;
		}

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitAssignment(AssignmentContext ctx) {		
		ExpressionListContext ids = ctx.expressionList(0);
		ExpressionListContext exps = ctx.expressionList(1);

		Statement prev = null;

		for (int i = 0; i < ids.expression().size(); i++) {

			Expression lhs = visitExpression(ids.expression(i));

			// For the moment, we only support assignment where the left-hand side operand is an identifier
			if (!(lhs instanceof Variable))
				throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());

			Expression exp = buildExpressionFromAssignment((Variable) lhs, ctx.assign_op(), visitExpression(exps.expression(i)));

			int line = getLine(ctx);
			int col = getCol(ctx);

			GoAssignment asg = new GoAssignment(currentCFG, filePath, line, col, lhs, exp);
			currentCFG.addNode(asg);

			if (prev != null)
				currentCFG.addEdge(new SequentialEdge(prev, asg));
			prev = asg;
		}

		return prev;
	}

	private Expression buildExpressionFromAssignment(Variable lhs, Assign_opContext op, Expression exp) {

		// +=
		if (op.PLUS() != null)
			return new GoSum(currentCFG, lhs, exp);
		// -=	
		if (op.MINUS() != null)
			return new GoSubtraction(currentCFG, lhs, exp);

		// *=
		if (op.STAR() != null)
			return new GoMul(currentCFG, lhs, exp);

		// /=
		if (op.DIV() != null)
			return new GoDiv(currentCFG, lhs, exp);

		// %=
		if (op.MOD() != null)
			return new GoModule(currentCFG, lhs, exp);

		// >>=
		if (op.RSHIFT() != null)
			return new GoRightShift(currentCFG, lhs, exp);

		// <<=
		if (op.LSHIFT() != null)
			return new GoLeftShift(currentCFG, lhs, exp);

		// &^=
		if (op.BIT_CLEAR() != null)
			throw new UnsupportedOperationException("Unsupported assignment operator: " + op.getText());

		// ^=
		if (op.CARET() != null)
			return new GoXOr(currentCFG, lhs, exp);

		// &=
		if (op.AMPERSAND() != null)
			return new GoAnd(currentCFG, lhs, exp);

		// |=
		if (op.OR() != null)
			return new GoOr(currentCFG, lhs, exp);

		// Return exp if the assignment operator is null
		return exp;
	}


	@Override
	public Statement visitAssign_op(Assign_opContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
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
			Expression exp = visitExpression(exps.expression(i));
			
			// The type of the variable is implict and should be retrieved from the type of exp
			Type type = exp.getStaticType();
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);

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
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitLabeledStmt(LabeledStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitReturnStmt(ReturnStmtContext ctx) {
		if (ctx.expressionList().expression().size() == 1)
			return new GoReturn(currentCFG, visitExpression(ctx.expressionList().expression(0)));

		throw new UnsupportedOperationException("Return of tuples not supported: " + ctx.getText());
	}

	@Override
	public Statement visitBreakStmt(BreakStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitContinueStmt(ContinueStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitGotoStmt(GotoStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitFallthroughStmt(FallthroughStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitDeferStmt(DeferStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitIfStmt(IfStmtContext ctx) {
		// Visit if statement Boolean Guard
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
			if (ctx.block(1) != null) {
				// If statement with else branch with no other if statements 
				Statement exitStatementTrueBranch = visitBlock(ctx.block(0));
				Statement exitStatementFalseBranch = visitBlock(ctx.block(1));

				Statement entryStatementTrueBranch = getEntryNode(ctx.block(0));
				Statement entryStatementFalseBranch = getEntryNode(ctx.block(1));

				currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				currentCFG.addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				currentCFG.addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			} else {
				// If statement with else branch with other if statements 
				Statement exitStatementTrueBranch = visitBlock(ctx.block(0));
				Statement exitStatementFalseBranch = visitIfStmt(ctx.ifStmt());

				Statement entryStatementTrueBranch = getEntryNode(ctx.block(0));
				Statement entryStatementFalseBranch = getEntryNode(ctx.ifStmt());

				currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				currentCFG.addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				currentCFG.addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			}
		}

		// Check whether the if-statement has an initial statement
		// e.g., if x := y; z < x block 
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
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitExprSwitchStmt(ExprSwitchStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitExprCaseClause(ExprCaseClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitExprSwitchCase(ExprSwitchCaseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeSwitchStmt(TypeSwitchStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeSwitchGuard(TypeSwitchGuardContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeCaseClause(TypeCaseClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeSwitchCase(TypeSwitchCaseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeList(TypeListContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitSelectStmt(SelectStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitCommClause(CommClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitCommCase(CommCaseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitRecvStmt(RecvStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitForStmt(ForStmtContext ctx) {

		// Simple for (without range and first expression)
		// e.g., for i := 0; i < 10; i++ block
		if (ctx.forClause() != null) {

			boolean hasInitStmt = hasInitStmt(ctx);
			boolean hasCondition = hasCondition(ctx);
			boolean hasPostStmt = hasPostStmt(ctx);

			// Checking if initialization is missing
			Statement init = null;
			if (hasInitStmt) {
				init = visitSimpleStmt(ctx.forClause().simpleStmt(0));
				currentCFG.addNode(init);
			}

			// Checking if condition is missing
			Statement cond = null;
			if (hasCondition) {
				cond = visitExpression(ctx.forClause().expression());
				currentCFG.addNode(cond);
			}


			// Checking if post statement is missing
			Statement post = null;
			if (hasPostStmt) {
				post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
				currentCFG.addNode(post);
			}

			// TODO: at the moment, we suppose that the block is non-empty
			Statement lastStmtOfBlock = visitBlock(ctx.block());
			Statement entryNodeOfBlock = getEntryNode(ctx.block());
			Statement exitNode = new NoOp(currentCFG);

			currentCFG.addNode(exitNode);

			if (hasCondition) {
				currentCFG.addEdge(new TrueEdge(cond, entryNodeOfBlock));
				currentCFG.addEdge(new FalseEdge(cond, exitNode));

				if (hasInitStmt)
					currentCFG.addEdge(new SequentialEdge(init, cond));

				if (hasPostStmt) {
					currentCFG.addEdge(new SequentialEdge(lastStmtOfBlock, post));
					currentCFG.addEdge(new SequentialEdge(post, cond));
				} else {
					currentCFG.addEdge(new SequentialEdge(lastStmtOfBlock, cond));
				}
			} else {
				if (hasInitStmt)
					currentCFG.addEdge(new SequentialEdge(init, entryNodeOfBlock));

				if (hasPostStmt) {
					currentCFG.addEdge(new SequentialEdge(lastStmtOfBlock, post));
					currentCFG.addEdge(new SequentialEdge(post, entryNodeOfBlock));
				} else {
					currentCFG.addEdge(new SequentialEdge(lastStmtOfBlock, entryNodeOfBlock));
				}
			}

			return exitNode;
		}


		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitForClause(ForClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitRangeClause(RangeClauseContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitGoStmt(GoStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitType_(Type_Context ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeName(TypeNameContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeLit(TypeLitContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitArrayType(ArrayTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitArrayLength(ArrayLengthContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitElementType(ElementTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitPointerType(PointerTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitInterfaceType(InterfaceTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitSliceType(SliceTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitMapType(MapTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitChannelType(ChannelTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitMethodSpec(MethodSpecContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitFunctionType(FunctionTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitSignature(SignatureContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitResult(ResultContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitParameters(ParametersContext ctx) {
		// This method should never be visited
		throw new IllegalStateException("visitParameters should never be visited.");
	}

	@Override
	public Statement visitParameterDecl(ParameterDeclContext ctx) {
		// This method should never be visited
		throw new IllegalStateException("visitParameterDecl should never be visited.");
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
			return new GoSubtraction(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&&)
		if (ctx.LOGICAL_AND() != null) 
			return new GoLogicalAnd(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (||)
		if (ctx.LOGICAL_OR() != null)
			return new GoLogicalOr(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go equals (==)
		if (ctx.EQUALS() != null)
			return new GoEquals(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go less (<)
		if (ctx.LESS() != null)
			return new GoLess(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater (>)
		if (ctx.GREATER() != null)
			return new GoGreater(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go module (%)
		if (ctx.MOD() != null)
			return new GoModule(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go right shift (>>)
		if (ctx.RSHIFT() != null)
			return new GoRightShift(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go left shift (<<)
		if (ctx.LSHIFT() != null)
			return new GoLeftShift(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go XOR (^)
		if (ctx.CARET() != null)
			return new GoXOr(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go or (|)
		if (ctx.OR() != null)
			return new GoOr(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&)
		if (ctx.AMPERSAND() != null)
			return new GoAnd(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitPrimaryExpr(PrimaryExprContext ctx) {

		// Function call (e.g., f(1,2,3) )
		if (ctx.primaryExpr() != null && ctx.arguments() != null) {
			Expression func = visitPrimaryExpr(ctx.primaryExpr());
			List<ExpressionContext> argsCtx = ctx.arguments().expressionList().expression();

			int i = 0;
			Expression[] cfgArgs = new Expression[argsCtx.size()];
			for (ExpressionContext arg : argsCtx) 
				cfgArgs[i++] = visitExpression(arg);

			if (func instanceof Variable) 
				return new CFGCall(currentCFG, getCFGByName(((Variable) func).getName()), cfgArgs);
		}

		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Statement visitUnaryExpr(UnaryExprContext ctx) {

		if (ctx.PLUS() != null)
			return new GoPlus(currentCFG, visitExpression(ctx.expression()));

		if (ctx.MINUS() != null)
			return new GoMinus(currentCFG, visitExpression(ctx.expression()));

		if (ctx.EXCLAMATION() != null)
			return new GoNot(currentCFG, visitExpression(ctx.expression()));

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitConversion(ConversionContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
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

		// Go nil value
		if (ctx.NIL_LIT() != null)
			return new GoNil(currentCFG);

		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitInteger(IntegerContext ctx) {
		//TODO: for the moment, we skip any other integer literal format (e.g., octal, imaginary)
		if (ctx.DECIMAL_LIT() != null)
			return new GoInteger(currentCFG, Integer.parseInt(ctx.DECIMAL_LIT().getText()));

		// TODO: 0 matched as octacl literal and not decimal literal
		if (ctx.OCTAL_LIT() != null)
			return new GoInteger(currentCFG, Integer.parseInt(ctx.OCTAL_LIT().getText()));

		if (ctx.IMAGINARY_LIT() != null)
			throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());

		if (ctx.HEX_LIT() != null)
			throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());	
	}

	@Override
	public Expression visitOperandName(OperandNameContext ctx) {
		if (ctx.IDENTIFIER() != null) {
			// Boolean values (true, false) are matched as identifiers
			if (ctx.IDENTIFIER().getText().equals("true") || ctx.IDENTIFIER().getText().equals("false"))
				return new GoBoolean(currentCFG, Boolean.parseBoolean(ctx.IDENTIFIER().getText()));
			else
				return new Variable(currentCFG, ctx.IDENTIFIER().getText());
		}

		Statement child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Statement visitQualifiedIdent(QualifiedIdentContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitCompositeLit(CompositeLitContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitLiteralType(LiteralTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitLiteralValue(LiteralValueContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitElementList(ElementListContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitKeyedElement(KeyedElementContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitKey(KeyContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitElement(ElementContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitStructType(StructTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitFieldDecl(FieldDeclContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitString_(String_Context ctx) {
		if (ctx.RAW_STRING_LIT() != null)
			return new GoString(currentCFG, removeQuotes(ctx.RAW_STRING_LIT().getText()));

		if (ctx.INTERPRETED_STRING_LIT() != null)
			return new GoString(currentCFG, removeQuotes(ctx.INTERPRETED_STRING_LIT().getText()));

		throw new IllegalStateException("Illegal state: string rule has no other productions.");
	}

	@Override
	public Statement visitAnonymousField(AnonymousFieldContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitFunctionLit(FunctionLitContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitIndex(IndexContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitSlice(SliceContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeAssertion(TypeAssertionContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitArguments(ArgumentsContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitMethodExpr(MethodExprContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitEos(EosContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	private int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	} 

	private int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	} 

	private int getCol(Token ctx) {
		return ctx.getCharPositionInLine();
	} 

	private int getLine(Token ctx) {
		return ctx.getLine();
	} 

	private Statement getEntryNode(ParserRuleContext ctx) {
		if (ctx instanceof BlockContext)
			return getBlockEntryNode((BlockContext) ctx);

		if (ctx instanceof IfStmtContext) {
			IfStmtContext ifStmt = (IfStmtContext) ctx;
			if (ifStmt.simpleStmt() != null)
				return getEntryNode(ifStmt.simpleStmt());
			else
				return getEntryNode(ifStmt.expression());
		}

		if (ctx instanceof StatementContext) {
			StatementContext stmt = (StatementContext) ctx;
			if (stmt.ifStmt() != null)
				return getEntryNode(stmt.ifStmt());

			if  (stmt.forStmt() != null) {
				return getForEntryNode(stmt.forStmt());
			}
		}

		if (ctx instanceof VarSpecContext) {
			return getNodeAt(getLine(((VarSpecContext) ctx).identifierList().IDENTIFIER(0).getSymbol()), 
					getCol(((VarSpecContext) ctx).expressionList().expression(0)));
		}

		if (ctx instanceof ConstSpecContext) {
			return getNodeAt(getLine(((ConstSpecContext) ctx).identifierList().IDENTIFIER(0).getSymbol()), 
					getCol(((ConstSpecContext) ctx).expressionList().expression(0)));
		}

		// If ctx is a simple statement (not composite) return the node corresponding to ctx
		return getNodeFromContext(ctx);
	}

	private Statement getNodeFromContext(ParserRuleContext ctx) {
		for (Statement node : currentCFG.getNodes()) {
			if (node.getLine() == getLine(ctx) && node.getCol() == getCol(ctx))
				return node;
		}

		throw new IllegalStateException("Cannot find the node " + ctx.getText() + " in cfg.");
	}

	/**
	 * Returns the node in currentCFG at a given position (line, column).
	 * 
	 * @param line	line where to find the node
	 * @param col	column where to search the node
	 * @return		the node at the position (line, column)
	 */
	private Statement getNodeAt(int line, int col) {
		for (Statement node : currentCFG.getNodes()) {
			if (node.getLine() == line && node.getCol() == col)
				return node;
		}

		throw new IllegalStateException("Cannot find the node at " + line + ":" + col);
	}

	/**
	 * Returns the entry node of a block statement.
	 * @param block 	the block statement
	 * @return 			the entry node of block
	 */
	private Statement getBlockEntryNode(BlockContext block) {
		return getNodeFromContext(block.statementList().statement(0));
	}

	/**
	 * Returns the entry node of a for statement.
	 * @param block 	the for statement
	 * @return the entry node of the for statement
	 */
	private Statement getForEntryNode(ForStmtContext forStmt) {

		if (forStmt.forClause() != null) {

			if (hasInitStmt(forStmt))
				return getNodeFromContext(forStmt.forClause().simpleStmt(0));

			if (hasCondition(forStmt))
				return getNodeFromContext(forStmt.forClause().expression());

			if (hasPostStmt(forStmt))
				if (hasInitStmt(forStmt))
					return getNodeFromContext(forStmt.forClause().simpleStmt(1));
				else
					return getNodeFromContext(forStmt.forClause().simpleStmt(0));
		}

		throw new UnsupportedOperationException("Unsupported translation: " + forStmt.getText());
	}

	/**
	 * Checks if the for statement has the initialization statement.
	 * 
	 * @param ctx 	the for statement context
	 * @return {@code true}	if the for statement has the initialization statement, {@code false} otherwise
	 */
	private boolean hasInitStmt(ForStmtContext ctx) {
		return ctx.forClause().simpleStmt(0) != null && getCol(ctx.forClause().simpleStmt(0)) < getCol(ctx.forClause().SEMI(0).getSymbol()); 
	}

	/**
	 * Checks if the for statement has the guard expression.
	 * 
	 * @param ctx 	the for statement context
	 * @return {@code true}	if the for statement has the guard expression, {@code false} otherwise
	 */
	private boolean hasCondition(ForStmtContext ctx) {
		return  ctx.forClause().expression() != null;
	}

	/**
	 * Checks if the for statement has the post statement.
	 * 
	 * @param ctx 	the for statement context
	 * @return {@code true}	if the for statement has the post statement; {@code false} otherwise
	 */
	private boolean hasPostStmt(ForStmtContext ctx) {
		return ctx.forClause().simpleStmt(1) != null ||
				(ctx.forClause().simpleStmt(0) != null &&  
				getCol(ctx.forClause().simpleStmt(0)) > getCol(ctx.forClause().SEMI(1).getSymbol()));
	}

	private String removeQuotes(String str) {
		return str.substring(1, str.length()-1);
	}

	private Type getGoType(Type_Context ctx) {
		// The type context is absent, Untyped type is returned
		if (ctx == null)
			return Untyped.INSTANCE;
		
		if (ctx.typeName() != null) {
			TypeNameContext typeName = ctx.typeName();

			if (typeName.IDENTIFIER() != null) {

				switch(typeName.IDENTIFIER().getText()) { 
				case "int": 
					return GoIntType.INSTANCE;
				case "string": 
					return GoStringType.INSTANCE;
				case "bool": 
					return GoBoolType.INSTANCE;
				default: 
					throw new UnsupportedOperationException("Unsupported basic type: " + ctx.getText());
				} 
			}

			if (typeName.qualifiedIdent() != null)
				throw new UnsupportedOperationException("Unsupported type: " + ctx.getText());
		}

		throw new UnsupportedOperationException("Unsupported type: " + ctx.getText());
	}
}
