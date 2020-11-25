package it.unive.golisa.cfg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.call.binary.GoAnd;
import it.unive.golisa.cfg.call.binary.GoDiv;
import it.unive.golisa.cfg.call.binary.GoEquals;
import it.unive.golisa.cfg.call.binary.GoGreater;
import it.unive.golisa.cfg.call.binary.GoLeftShift;
import it.unive.golisa.cfg.call.binary.GoLess;
import it.unive.golisa.cfg.call.binary.GoLogicalAnd;
import it.unive.golisa.cfg.call.binary.GoLogicalOr;
import it.unive.golisa.cfg.call.binary.GoModule;
import it.unive.golisa.cfg.call.binary.GoMul;
import it.unive.golisa.cfg.call.binary.GoOr;
import it.unive.golisa.cfg.call.binary.GoRightShift;
import it.unive.golisa.cfg.call.binary.GoSubtraction;
import it.unive.golisa.cfg.call.binary.GoSum;
import it.unive.golisa.cfg.call.binary.GoXOr;
import it.unive.golisa.cfg.call.unary.GoMinus;
import it.unive.golisa.cfg.call.unary.GoNot;
import it.unive.golisa.cfg.call.unary.GoPlus;
import it.unive.golisa.cfg.custom.GoAssignment;
import it.unive.golisa.cfg.custom.GoCollectionAccess;
import it.unive.golisa.cfg.custom.GoConstantDeclaration;
import it.unive.golisa.cfg.custom.GoFieldAccess;
import it.unive.golisa.cfg.custom.GoReturn;
import it.unive.golisa.cfg.custom.GoTypeConversion;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.*;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoAliasType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt16Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt8Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt16Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt32Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt64Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.Parameter;
import it.unive.lisa.cfg.edge.FalseEdge;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.edge.TrueEdge;
import it.unive.lisa.cfg.statement.CFGCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NoOp;
import it.unive.lisa.cfg.statement.OpenCall;
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
public class GoFrontEnd extends GoParserBaseVisitor<Object> {

	private static final Logger log = LogManager.getLogger(GoFrontEnd.class); 

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
	public GoFrontEnd(String filePath) {
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
		String file = "src/test/resources/go-tutorial/type/type002.go";
		GoFrontEnd translator = new GoFrontEnd(file);
		System.err.println(translator.toLiSACFG().iterator().next().getEdges());
		System.err.println(GoStructType.structTypes);
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

//	@Override
//	public Statement visit(ParseTree tree) {
//
//		if (tree instanceof SourceFileContext)
//			return visitSourceFile((SourceFileContext) tree);
//		else {
//			return visit(((RuleContext) tree));
//		}
//	}
	//	@Override 
	//	public Expression visitChildren(RuleNode node) {
	//				throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());

	//	}

	//	@Override
	//	public Statement visitTerminal(TerminalNode node) {
	//		throw new UnsupportedOperationException("Unsupported translation: " + node.getText());
	//
	//	}
	//
	//	@Override
	//	public Statement visitErrorNode(ErrorNode node) {
	//		throw new UnsupportedOperationException("Unsupported translation: " + node.getText());
	//	}

	@Override
	public Collection<CFG> visitSourceFile(SourceFileContext ctx) {
		
		for (DeclarationContext decl : IterationLogger.iterate(log, ctx.declaration(), "Parsing global declarations...", "Global declarations"))
			visitDeclaration(decl);

		// Visit of each FunctionDeclContext appearing in the source code
		// and creating the corresponding CFG object (we do this to handle CFG calls)
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Parsing function declarations...", "Function declarations"))
			cfgs.add(new CFG(buildCFGDescriptor(funcDecl)));

		// Visit of each FunctionDeclContext populating the corresponding cfg
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Visiting function declarations...", "Function declarations")) {
			currentCFG = getCFGByName(funcDecl.IDENTIFIER().getText());
			visitFunctionDecl(funcDecl);
		}

		return cfgs;
	}

	/**
	 * Checks if a cfg named name is contained in cfgs.
	 * 
	 * @param name	the cfg name to be searched
	 * @return {@code true} if there exists a cfg named name; {@code false} othewise
	 */
	private boolean containsCFG(String name) {
		return cfgs.stream().anyMatch(cfg -> cfg.getDescriptor().getName().equals(name));
	}

	/**
	 * Retrieve a cfg given its name, assuming that the cfg is contained in cfgs.
	 * Hence, before calling this function, check the presence of cfg by using
	 * the function {@link GoFrontEnd#containsCFG}.
	 * 
	 * @param name	the cfg name
	 * @return the cfg named name
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
	 * returns an array of the corresponding {@link Parameter}s.
	 * 
	 * @param paramCtx	the parameter context
	 * @return an array of {@link Parameter}
	 */
	private Parameter[] getParameters(ParameterDeclContext paramCtx) {
		Parameter[] args = new Parameter[paramCtx.identifierList().IDENTIFIER().size()];
		Type argType = visitType_(paramCtx.type_());
		for (int i = 0; i < paramCtx.identifierList().IDENTIFIER().size(); i++)
			args[i] = new Parameter(paramCtx.identifierList().IDENTIFIER(i).getText(), argType);
		return args;
	}

	/**
	 * Given a signature context, returns the Go type 
	 * corresponding to the return type of the signature.
	 * If the result type is missing (i.e. null) then
	 * the {@link Untyped} typed is returned. 
	 * 
	 * @param signature the signature context
	 * @return the Go type corresponding to the return type of {@code signature}
	 */
	private Type getGoReturnType(SignatureContext signature) {
		// The return type is not specified
		if (signature.result() == null)
			return Untyped.INSTANCE;

		// The return type is specified
		if (signature.result().type_() != null)
			return visitType_(signature.result().type_());

		// The return type and the variable returned are specified 
		if (signature.result().parameters() != null) {
			/* TODO: a tuple of (variable, type) can be returned
			 * e.g. (x int, y int, z string) 
			 * Not sure if the returned variables must be tracked in the CFG descriptor
			 * since on a typed function a return statement is required.
			 * Probably, a tuple of types is enough as return type, in this case
			 */
		}

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
	public Pair<Statement, Statement> visitDeclaration(DeclarationContext ctx) {
		Object result = visitChildren(ctx);
		if ((result instanceof Pair<?,?>))
			return (Pair<Statement, Statement>) result;	
		else 
			return null;
	}

	@Override
	public Pair<Statement, Statement> visitConstDecl(ConstDeclContext ctx) {
		Statement lastStmt = null;
		Statement entryNode = null;

		for (ConstSpecContext constSpec : ctx.constSpec()) {
			Pair<Statement, Statement> currStmt = visitConstSpec(constSpec);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()));
			else
				entryNode = currStmt.getRight();

			lastStmt = currStmt.getRight();
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitConstSpec(ConstSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement lastStmt = null;
		Statement entryNode = null;
		Type type = ctx.type_() == null ? Untyped.INSTANCE : visitType_(ctx.type_());

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);
			Expression exp = visitExpression(exps.expression(i));

			GoConstantDeclaration asg = new GoConstantDeclaration(currentCFG, target, exp);
			currentCFG.addNode(asg);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
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
		for (TypeSpecContext typeSpec : ctx.typeSpec())
			visitTypeSpec(typeSpec);
		return null;
	}

	@Override
	public Statement visitTypeSpec(TypeSpecContext ctx) {

		String typeName = ctx.IDENTIFIER().getText();

		Type type = visitType_(ctx.type_());


		if (ctx.ASSIGN() == null) {
			if (type instanceof GoStructType)
				GoStructType.addtStructType(typeName, (GoStructType) type);
			else
				throw new UnsupportedOperationException("Unsupported type translation: " + ctx.getText());

		} else {
			// Alias type
			GoAliasType.addAlias(typeName, new GoAliasType(typeName, type));
		}

		return null;
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {
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
	public Pair<Statement, Statement> visitVarDecl(VarDeclContext ctx) {
		Statement lastStmt = null;
		Statement entryNode = null;

		for (VarSpecContext varSpec : ctx.varSpec()) {
			Pair<Statement, Statement> currStmt = visitVarSpec(varSpec);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()));
			else
				entryNode = currStmt.getLeft();

			lastStmt = currStmt.getRight();
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitVarSpec(VarSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement lastStmt = null;
		Statement entryNode = null;
		Type type = visitType_(ctx.type_());

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);
			//TODO: check if exp is null
			Expression exp = (Expression) visitExpression(exps.expression(i));

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, target, exp);
			currentCFG.addNode(asg);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitBlock(BlockContext ctx) {
		// Visit the statement list inside the block
		return visitStatementList(ctx.statementList());
	}

	@Override
	public Pair<Statement, Statement> visitStatementList(StatementListContext ctx) {

		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ctx.statement().size(); i++)  {
			Pair<Statement, Statement> currentStmt = visitStatement(ctx.statement(i));

			if (lastStmt != null) 
				currentCFG.addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitStatement(StatementContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;
	}

	@Override
	public Pair<Statement, Statement>  visitSimpleStmt(SimpleStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;	
	}

	@Override
	public Pair<Statement, Statement> visitExpressionStmt(ExpressionStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;		
	}

	@Override
	public Statement visitSendStmt(SendStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Pair<Statement, Statement> visitIncDecStmt(IncDecStmtContext ctx) {

		Expression exp = visitExpression(ctx.expression());
		Statement asg = null;

		// increment and decrement statements are syntactic sugar
		// e.g., x++ -> x = x + 1 and x-- -> x = x - 1
		if (ctx.PLUS_PLUS() != null)
			asg = new GoAssignment(currentCFG, exp, 
					new GoSum(currentCFG,  exp,  new GoInteger(currentCFG, 1)));		
		else
			asg = new GoAssignment(currentCFG, exp, 
					new GoSubtraction(currentCFG, exp,  new GoInteger(currentCFG, 1)));

		currentCFG.addNode(asg);
		return Pair.of(asg, asg);
	}

	@Override
	public Pair<Statement, Statement> visitAssignment(AssignmentContext ctx) {		
		ExpressionListContext ids = ctx.expressionList(0);
		ExpressionListContext exps = ctx.expressionList(1);

		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ids.expression().size(); i++) {

			Expression lhs = visitExpression(ids.expression(i));
			Expression exp = buildExpressionFromAssignment(lhs, ctx.assign_op(), visitExpression(exps.expression(i)));

			GoAssignment asg = new GoAssignment(currentCFG, lhs, exp);
			currentCFG.addNode(asg);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
	}

	private Expression buildExpressionFromAssignment(Expression lhs, Assign_opContext op, Expression exp) {

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
	public Pair<Statement, Statement> visitShortVarDecl(ShortVarDeclContext ctx) {
		/*
		 * The short variable declaration is only
		 * syntactic sugar for variable declaration
		 * (e.g., x := 5 corresponds to var x int = 5).
		 * Hence, the translation is identical to the one
		 * of a variable declaration.
		 */
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			Expression exp = visitExpression(exps.expression(i));

			// The type of the variable is implicit and it is retrieved from the type of exp
			Type type = exp.getStaticType();
			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, target, exp);
			currentCFG.addNode(asg);

			if (lastStmt != null)
				currentCFG.addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;

		}

		return Pair.of(entryNode, lastStmt);
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
	public Pair<Statement, Statement> visitReturnStmt(ReturnStmtContext ctx) {

		if (ctx.expressionList().expression().size() == 1) {
			GoReturn ret =  new GoReturn(currentCFG, visitExpression(ctx.expressionList().expression(0)));
			currentCFG.addNode(ret);
			return Pair.of(ret, ret);
		}

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
	public Pair<Statement, Statement> visitIfStmt(IfStmtContext ctx) {

		// Visit if statement Boolean Guard
		Statement booleanGuard = visitExpression(ctx.expression());
		currentCFG.addNode(booleanGuard);

		NoOp ifExitNode = new NoOp(currentCFG);
		currentCFG.addNode(ifExitNode);

		Pair<Statement, Statement> trueBlock = visitBlock(ctx.block(0));
		Statement exitStatementTrueBranch = trueBlock.getRight();
		Statement entryStatementTrueBranch = trueBlock.getLeft();

		if (ctx.ELSE() == null) {
			// If statement without else branch
			currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));			
			currentCFG.addEdge(new FalseEdge(booleanGuard, ifExitNode));			
			currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
		} else {
			if (ctx.block(1) != null) {
				// If statement with else branch with no other if statements 
				Pair<Statement, Statement> falseBlock = visitBlock(ctx.block(1));
				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				currentCFG.addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				currentCFG.addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			} else {
				// If statement with else branch with other if statements 
				Pair<Statement, Statement> falseBlock = visitIfStmt(ctx.ifStmt());
				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				currentCFG.addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				currentCFG.addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				currentCFG.addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				currentCFG.addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			}
		}

		// Checks whether the if-statement has an initial statement
		// e.g., if x := y; z < x block 
		Statement entryNode = booleanGuard;
		if (ctx.simpleStmt() != null) {
			Pair<Statement, Statement> initialStmt = visitSimpleStmt(ctx.simpleStmt());
			entryNode = initialStmt.getLeft();
			currentCFG.addEdge(new SequentialEdge(initialStmt.getRight(), booleanGuard));
		} 

		return Pair.of(entryNode, ifExitNode);
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
	public Pair<Statement, Statement> visitForStmt(ForStmtContext ctx) {

		// Simple for (without range and first expression)
		// e.g., for i := 0; i < 10; i++ block
		if (ctx.forClause() != null) {

			boolean hasInitStmt = hasInitStmt(ctx);
			boolean hasCondition = hasCondition(ctx);
			boolean hasPostStmt = hasPostStmt(ctx);

			// Checking if initialization is missing
			Pair<Statement, Statement> init = null;
			Statement entryNode = null;
			if (hasInitStmt) {
				init = visitSimpleStmt(ctx.forClause().simpleStmt(0));
				currentCFG.addNode(entryNode = init.getLeft());
			}

			// Checking if condition is missing
			Statement cond = null;
			if (hasCondition) {
				cond = visitExpression(ctx.forClause().expression());
				currentCFG.addNode(cond);
			}


			// Checking if post statement is missing
			Pair<Statement, Statement> post = null;
			if (hasPostStmt) {
				post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
				currentCFG.addNode(post.getLeft());
			}

			// TODO: at the moment, we suppose that the block is non-empty
			Pair<Statement, Statement> block = visitBlock(ctx.block());
			Statement exitNodeBlock = block.getRight();
			Statement entryNodeOfBlock = block.getLeft();
			Statement exitNode = new NoOp(currentCFG);

			currentCFG.addNode(exitNode);

			if (hasCondition) {
				currentCFG.addEdge(new TrueEdge(cond, entryNodeOfBlock));
				currentCFG.addEdge(new FalseEdge(cond, exitNode));

				if (hasInitStmt) 
					currentCFG.addEdge(new SequentialEdge(init.getRight(), cond));
				else
					entryNode = cond; 

				if (hasPostStmt) {
					currentCFG.addEdge(new SequentialEdge(exitNodeBlock, post.getRight()));
					currentCFG.addEdge(new SequentialEdge(post.getLeft(), cond));
				} else {
					currentCFG.addEdge(new SequentialEdge(exitNodeBlock, cond));
				}
			} else {
				if (hasInitStmt)
					currentCFG.addEdge(new SequentialEdge(init.getRight(), entryNodeOfBlock));

				if (hasPostStmt) {
					currentCFG.addEdge(new SequentialEdge(exitNodeBlock, post.getLeft()));
					currentCFG.addEdge(new SequentialEdge(post.getRight(), entryNodeOfBlock));
				} else {
					currentCFG.addEdge(new SequentialEdge(exitNodeBlock, entryNodeOfBlock));
				}
			}

			return Pair.of(entryNode, exitNode);
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
	public Type visitType_(Type_Context ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Type))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Type) result;		
	}

	@Override
	public Type visitTypeName(TypeNameContext ctx) {
		return getGoType(ctx);
	}

	@Override
	public Type visitTypeLit(TypeLitContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Type))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Type) result;
	}

	@Override
	public Type visitArrayType(ArrayTypeContext ctx) {
		Type contentType = visitElementType(ctx.elementType());
		GoInteger length = visitArrayLength(ctx.arrayLength());
		return new GoArrayType(contentType, length);
	}

	@Override
	public GoInteger visitArrayLength(ArrayLengthContext ctx) {
		Expression length = visitExpression(ctx.expression());

		if (!(length instanceof GoInteger))
			throw new IllegalStateException("Go error: non-constant array bound");
		else 
			return (GoInteger) length;
	}

	@Override
	public Type visitElementType(ElementTypeContext ctx) {
		return visitType_(ctx.type_());
	}

	@Override
	public Type visitPointerType(PointerTypeContext ctx) {
		return new GoPointerType(visitType_(ctx.type_()));
	}

	@Override
	public Statement visitInterfaceType(InterfaceTypeContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Type visitSliceType(SliceTypeContext ctx) {
		return new GoSliceType(visitElementType(ctx.elementType()));
	}

	@Override
	public Type visitMapType(MapTypeContext ctx) {
		return new GoMapType(visitType_(ctx.type_()), visitElementType(ctx.elementType()));
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
			return new GoLogicalAnd(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (||)
		if (ctx.LOGICAL_OR() != null)
			return new GoLogicalOr(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go equals (==)
		if (ctx.EQUALS() != null)
			return new GoEquals(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go less (<)
		if (ctx.LESS() != null)
			return new GoLess(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater (>)
		if (ctx.GREATER() != null)
			return new GoGreater(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go module (%)
		if (ctx.MOD() != null)
			return new GoModule(currentCFG,  visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go right shift (>>)
		if (ctx.RSHIFT() != null)
			return new GoRightShift(currentCFG,  visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go left shift (<<)
		if (ctx.LSHIFT() != null)
			return new GoLeftShift(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go XOR (^)
		if (ctx.CARET() != null)
			return new GoXOr(currentCFG,  visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go or (|)
		if (ctx.OR() != null)
			return new GoOr(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&)
		if (ctx.AMPERSAND() != null)
			return new GoAnd(currentCFG, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitPrimaryExpr(PrimaryExprContext ctx) {

		if (ctx.primaryExpr() != null) {
			Expression primary = visitPrimaryExpr(ctx.primaryExpr());

			// Function call (e.g., f(1,2,3), f() )
			if (ctx.arguments() != null) {
				Expression[] cfgArgs;

				// Check if the function call has arguments
				if (ctx.arguments().expressionList() == null)
					cfgArgs = new Expression[] {};
				else {
					List<ExpressionContext> argsCtx = ctx.arguments().expressionList().expression();
					cfgArgs = new Expression[argsCtx.size()];

					int i = 0;
					for (ExpressionContext arg : argsCtx) 
						cfgArgs[i++] = visitExpression(arg);	
				}

				if (primary instanceof Variable) {
					String funName = ((Variable) primary).getName();
					/* If the function name is found in cfgs, this call corresponds to a {@link CFGCall}
					 * otherwise it is an {@link OpenCall}.
					 */
					if (containsCFG(funName))
						return new CFGCall(currentCFG, "", getCFGByName(funName), cfgArgs);
					else
						return new OpenCall(currentCFG, funName, cfgArgs);
				}
			}

			// Array/slice/map access e1[e2]
			else if (ctx.index() != null) {
				Expression index = visitIndex(ctx.index());
				return new GoCollectionAccess(currentCFG, primary, index);
			}

			// Field access x.f
			else if (ctx.IDENTIFIER() != null) {
				// TODO: create Identifier class
				Variable index = new Variable(currentCFG, ctx.IDENTIFIER().getText());
				return new GoFieldAccess(currentCFG, primary, index);
			}
		}

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitUnaryExpr(UnaryExprContext ctx) {

		if (ctx.PLUS() != null)
			return new GoPlus(currentCFG, visitExpression(ctx.expression()));

		if (ctx.MINUS() != null)
			return new GoMinus(currentCFG, visitExpression(ctx.expression()));

		if (ctx.EXCLAMATION() != null)
			return new GoNot(currentCFG, visitExpression(ctx.expression()));

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitConversion(ConversionContext ctx) {
		Type type = visitType_(ctx.type_());
		Expression exp = visitExpression(ctx.expression());
		return new GoTypeConversion(currentCFG, type, exp);
	}

	@Override
	public Expression visitOperand(OperandContext ctx) {
		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitLiteral(LiteralContext ctx) {
		Object child = visitChildren(ctx);
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

		// Go float value
		if (ctx.FLOAT_LIT() != null) 
			return new GoFloat(currentCFG, Double.parseDouble(ctx.FLOAT_LIT().getText()));

		Object child = visitChildren(ctx);
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

		Object child = visitChildren(ctx);
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

		Type type = visitLiteralType(ctx.literalType());
		Map<Expression, Expression> raw = visitLiteralValue(ctx.literalValue());

		if (type instanceof GoStructType)
			return new GoStructLiteral(currentCFG, raw, (GoStructType) type);

		if (type instanceof GoMapType)
			return new GoMapLiteral(currentCFG, raw, (GoMapType) type);

		if (type instanceof GoArrayType)
			return new GoArrayLiteral(currentCFG, raw, (GoArrayType) type);

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Type visitLiteralType(LiteralTypeContext ctx) {
		Object child = visitChildren(ctx);
		if (!(child instanceof Type))
			throw new IllegalStateException("Type expected");
		else
			return (Type) child;
	}

	@Override
	public Map<Expression, Expression> visitLiteralValue(LiteralValueContext ctx) {
		return visitElementList(ctx.elementList());
	}

	@Override
	public Map<Expression, Expression> visitElementList(ElementListContext ctx) {

		// All keyed or all without key
		Map<Expression, Expression> result = new HashMap<Expression, Expression>();

		for (KeyedElementContext keyedEl : ctx.keyedElement()) 
			result.put(visitKey(keyedEl.key()), visitElement(keyedEl.element()));

		return result;
	}

	@Override
	public Statement visitKeyedElement(KeyedElementContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitKey(KeyContext ctx) {

		if (ctx.IDENTIFIER() != null)
			return new Variable(currentCFG, ctx.IDENTIFIER().getText());

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitElement(ElementContext ctx) {	
		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public GoStructType visitStructType(StructTypeContext ctx) {
		Map<String, Type> fields = new HashMap<String, Type>();

		for (FieldDeclContext field : ctx.fieldDecl()) {
			Type fieldType = visitType_(field.type_());
			for (TerminalNode f : field.identifierList().IDENTIFIER())
				fields.put(f.getText(), fieldType);
		}

		return new GoStructType(fields);
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
	public Expression visitIndex(IndexContext ctx) {
		return visitExpression(ctx.expression());
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

	/**
	 * Given a type context, returns the corresponding Go type.
	 * 
	 * @param ctx the type context
	 * @return the Go type corresponding to {@ctx}
	 */
	private Type getGoType(TypeNameContext ctx) {

		if (ctx.IDENTIFIER() != null) {
			switch(ctx.IDENTIFIER().getText()) { 
			case "int": 
				return GoIntType.INSTANCE;
			case "int8": 
				return GoInt8Type.INSTANCE;
			case "int16": 
				return GoInt16Type.INSTANCE;
			case "int32": 
				return GoInt32Type.INSTANCE;
			case "int64": 
				return GoInt64Type.INSTANCE; 
			case "uint": 
				return GoUIntType.INSTANCE;
			case "byte": // byte is an alias for int8
			case "uint8": 
				return GoUInt8Type.INSTANCE;
			case "uint16": 
				return GoUInt16Type.INSTANCE;
			case "rune": // rune is an alias for int32
			case "uint32": 
				return GoUInt32Type.INSTANCE;
			case "uint64": 
				return GoUInt64Type.INSTANCE; 
			case "float32": 
				return GoFloat32Type.INSTANCE;
			case "float64": 
				return GoFloat64Type.INSTANCE; 
			case "string": 
				return GoStringType.INSTANCE;
			case "bool": 
				return GoBoolType.INSTANCE;	
			default: 
				if (GoStructType.structTypes.containsKey(ctx.IDENTIFIER().getText()))
					return GoStructType.structTypes.get(ctx.IDENTIFIER().getText());
				else
					throw new UnsupportedOperationException("Unsupported basic type: " + ctx.getText());
			} 
		}

		throw new UnsupportedOperationException("Unsupported type: " + ctx.getText());
	}
}
