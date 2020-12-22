package it.unive.golisa.cli;

import java.io.ByteArrayInputStream;
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
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.lang3.ArrayUtils;

import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.expression.GoFieldAccess;
import it.unive.golisa.cfg.expression.GoNew;
import it.unive.golisa.cfg.expression.GoTypeConversion;
import it.unive.golisa.cfg.expression.binary.GoAnd;
import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.expression.binary.GoEqual;
import it.unive.golisa.cfg.expression.binary.GoGreater;
import it.unive.golisa.cfg.expression.binary.GoLeftShift;
import it.unive.golisa.cfg.expression.binary.GoLess;
import it.unive.golisa.cfg.expression.binary.GoLogicalAnd;
import it.unive.golisa.cfg.expression.binary.GoLogicalOr;
import it.unive.golisa.cfg.expression.binary.GoModule;
import it.unive.golisa.cfg.expression.binary.GoMul;
import it.unive.golisa.cfg.expression.binary.GoOr;
import it.unive.golisa.cfg.expression.binary.GoRightShift;
import it.unive.golisa.cfg.expression.binary.GoSubtraction;
import it.unive.golisa.cfg.expression.binary.GoSum;
import it.unive.golisa.cfg.expression.binary.GoXOr;
import it.unive.golisa.cfg.expression.literal.*;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.expression.unary.GoMinus;
import it.unive.golisa.cfg.expression.unary.GoNot;
import it.unive.golisa.cfg.expression.unary.GoPlus;
import it.unive.golisa.cfg.expression.unary.GoRef;
import it.unive.golisa.cfg.statement.GoAssignment;
import it.unive.golisa.cfg.statement.GoConstantDeclaration;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.statement.GoReturn;
import it.unive.golisa.cfg.statement.GoVariableDeclaration;
import it.unive.golisa.cfg.statement.method.GoMethod;
import it.unive.golisa.cfg.statement.method.GoMethodSpecification;
import it.unive.golisa.cfg.statement.method.GoReceiver;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoQualifiedType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoAliasType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoRawType;
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
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
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
import it.unive.lisa.cfg.statement.UnresolvedCall;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;
import it.unive.lisa.logging.IterationLogger;

/**
 * @GoFrontEnd manages the translation from a Go program to the
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
		String file = "src/test/resources/go-tutorial/type/type003.go";
		GoFrontEnd translator = new GoFrontEnd(file);

		for (CFG cfg : translator.toLiSACFG()) {
			//			System.err.println(cfg);
			System.err.println(cfg.getEdges());
		}

		LiSA lisa = new LiSA();
		Collection<CFG> cfgs = translator.toLiSACFG();

		cfgs.forEach(lisa::addCFG);
		lisa.setDumpCFGs(true);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
		}
	}

	/**
	 * Stack of loop exit points (used for break statements)
	 */
	private List<Statement> exitPoints = new ArrayList<Statement>();

	/**
	 * Stack of loop entry points (used for continues statements)
	 */
	private List<Statement> entryPoints = new ArrayList<Statement>();



	private SourceFileContext source;

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
	public Collection<CFG> visitSourceFile(SourceFileContext ctx) {
		source = ctx;

		for (DeclarationContext decl : IterationLogger.iterate(log, ctx.declaration(), "Parsing global declarations...", "Global declarations")) 
			visitDeclaration(decl);

		for (MethodDeclContext decl : IterationLogger.iterate(log, ctx.methodDecl(), "Parsing method declarations...", "Method declarations"))
			visitMethodDecl(decl);

		// Visit of each FunctionDeclContext appearing in the source code
		// and creating the corresponding CFG object (we do this to handle CFG calls)
		for (FunctionDeclContext funcDecl : 
			IterationLogger.iterate(log, ctx.functionDecl(), "Parsing function declarations...", "Function declarations")) 
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

		Parameter[] cfgArgs = new Parameter[]{};

		for (int i = 0; i < formalPars.parameterDecl().size(); i++)
			ArrayUtils.addAll(cfgArgs, visitParameterDecl(formalPars.parameterDecl(i)));

		return new CFGDescriptor(filePath, line, col, funcName, getGoReturnType(funcDecl.signature()), cfgArgs);
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
		return visitResult(signature.result());
	}

	@Override
	public Statement visitPackageClause(PackageClauseContext ctx) {
		// TODO: package clause
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportDecl(ImportDeclContext ctx) {
		// TODO: import declaration
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportSpec(ImportSpecContext ctx) {
		// TODO: import specification
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitImportPath(ImportPathContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@SuppressWarnings("unchecked")
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
	public Expression[] visitIdentifierList(IdentifierListContext ctx) {
		Variable[] result = new Variable[] {};
		for (int i = 0; i < ctx.IDENTIFIER().size(); i++)
			result = ArrayUtils.addAll(result, new Variable(currentCFG, ctx.IDENTIFIER(i).getText()));
		return result;
	}

	@Override
	public Expression[] visitExpressionList(ExpressionListContext ctx) {
		Expression[] result = new Expression[] {};
		for (int i = 0; i < ctx.expression().size(); i++)
			result = ArrayUtils.addAll(result, visitExpression(ctx.expression(i)));
		return result;
	}

	@Override
	public Statement visitTypeDecl(TypeDeclContext ctx) {
		for (TypeSpecContext typeSpec : ctx.typeSpec())
			visitTypeSpec(typeSpec);
		return null;
	}

	@Override
	public GoType visitTypeSpec(TypeSpecContext ctx) {
		String typeName = ctx.IDENTIFIER().getText();
		GoType type = visitType_(ctx.type_());

		if (ctx.ASSIGN() == null) {
			if (type instanceof GoStructType)
				GoStructType.lookup(typeName, (GoStructType) type);
			else if (type instanceof GoInterfaceType)
				GoInterfaceType.lookup(typeName, (GoInterfaceType) type);
			else 
				GoAliasType.lookup(typeName, new GoAliasType(typeName, type));

		} else {
			// Alias type
			GoAliasType.lookup(typeName, new GoAliasType(typeName, type));
		}

		return null;
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {
		Pair<Statement, Statement> result = visitBlock(ctx.block());	
		currentCFG.getEntrypoints().add(result.getLeft());
		return result;
	}

	@Override
	public GoMethod visitMethodDecl(MethodDeclContext ctx) {
		int line = getLine(ctx);
		int col = getCol(ctx);

		GoReceiver receiver = visitReceiver(ctx.receiver());
		String name = ctx.IDENTIFIER().getText();
		Parameter[] params = visitParameters(ctx.signature().parameters());
		Type returnType = ctx.signature().result() == null ? Untyped.INSTANCE : visitResult(ctx.signature().result());

		CFGDescriptor desc = new CFGDescriptor(filePath, line, col, name, returnType, params);
		GoMethod method = new GoMethod(desc, receiver);
		cfgs.add(method);
		currentCFG = method;
		Pair<Statement, Statement> body = visitBlock(ctx.block());

		currentCFG.getEntrypoints().add(body.getLeft());
		currentCFG.simplify();
		return method;
	}

	@Override
	public GoReceiver visitReceiver(ReceiverContext ctx) {
		Parameter[] params = visitParameters(ctx.parameters());
		if (params.length != 1)
			throw new IllegalStateException("Go receiver must have a single parameter");

		return new GoReceiver(params[0].getName(), (GoType) params[0].getStaticType());
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
		Type type = ctx.type_() == null ? Untyped.INSTANCE : visitType_(ctx.type_());

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			int line = getLine(ids.IDENTIFIER(i).getSymbol());
			int col = getCol(exps.expression(i));

			Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);
			//TODO: check if exp is null
			Expression exp = exps.expression(i) == null && !type.isUntyped() ? ((GoType) type).defaultValue(currentCFG) : visitExpression(exps.expression(i));

			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, target, exp);
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

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Statement, Statement> visitStatement(StatementContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Statement, Statement>  visitSimpleStmt(SimpleStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;	
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Statement, Statement> visitExpressionStmt(ExpressionStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (result instanceof Expression) {
			currentCFG.addNode((Expression) result);
			return Pair.of((Expression) result, (Expression) result);
		} else if (!(result instanceof Pair<?,?>)) {
			throw new IllegalStateException("Pair of Statements expected");
		} else 
			return (Pair<Statement, Statement>) result;		
	}

	@Override
	public Statement visitSendStmt(SendStmtContext ctx) {
		// TODO: send statement
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Pair<Statement, Statement> visitIncDecStmt(IncDecStmtContext ctx) {

		Expression exp = visitExpression(ctx.expression());
		Statement asg = null;

		int line = getLine(ctx.expression());
		int col = getCol(ctx.expression());

		// increment and decrement statements are syntactic sugar
		// e.g., x++ -> x = x + 1 and x-- -> x = x - 1
		if (ctx.PLUS_PLUS() != null)
			asg = new GoAssignment(currentCFG, filePath, line, col,  exp, 
					new GoSum(currentCFG,  exp,  new GoInteger(currentCFG, filePath, line, col, 1)));		
		else
			asg = new GoAssignment(currentCFG, filePath, line, col, exp, 
					new GoSubtraction(currentCFG, exp,  new GoInteger(currentCFG, filePath, line, col, 1)));

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

			int line = getLine(ids.expression(i));
			int col = getLine(exps.expression(i));

			Expression lhs = visitExpression(ids.expression(i));
			Expression exp = buildExpressionFromAssignment(lhs, ctx.assign_op(), visitExpression(exps.expression(i)));

			GoAssignment asg = new GoAssignment(currentCFG, filePath, line, col, lhs, exp);
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
		// This method should never be visited
		throw new UnsupportedOperationException("Assign_op should never be visited");
	}

	@Override
	public Pair<Statement, Statement> visitShortVarDecl(ShortVarDeclContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		int sizeIds = ids.IDENTIFIER().size();
		int sizeExps = exps.expression().size();

		Statement lastStmt = null;
		Statement entryNode = null;

		if (sizeIds != sizeExps) {
			int line = getLine(ids.IDENTIFIER(0).getSymbol());
			int col = getCol(exps.expression(0));

			GoRawValue left = new GoRawValue(currentCFG, visitIdentifierList(ctx.identifierList()));
			Expression right = visitExpression(exps.expression(0));
			GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, left, right);
			currentCFG.addNode(asg);
			return Pair.of(asg, asg);
		} else {

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp = visitExpression(exps.expression(i));

				int line = getLine(ids.IDENTIFIER(i).getSymbol());
				int col = getCol(exps.expression(i));

				// The type of the variable is implicit and it is retrieved from the type of exp
				Type type = exp.getStaticType();
				Variable target = new Variable(currentCFG, ids.IDENTIFIER(i).getText(), type);

				GoVariableDeclaration asg = new GoVariableDeclaration(currentCFG, filePath, line, col, target, exp);
				currentCFG.addNode(asg);

				if (lastStmt != null)
					currentCFG.addEdge(new SequentialEdge(lastStmt, asg));
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Pair.of(entryNode, lastStmt);
		}
	}

	@Override
	public Statement visitEmptyStmt(EmptyStmtContext ctx) {
		return new NoOp(currentCFG, filePath, getLine(ctx), getCol(ctx));
	}

	@Override
	public Statement visitLabeledStmt(LabeledStmtContext ctx) {
		// TODO: labeled statements
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Pair<Statement, Statement> visitReturnStmt(ReturnStmtContext ctx) {
		GoReturn ret;
		if (ctx.expressionList().expression().size() == 1) 
			ret =  new GoReturn(currentCFG, visitExpression(ctx.expressionList().expression(0)));
		else
			ret =  new GoReturn(currentCFG, new GoRawValue(currentCFG, visitExpressionList(ctx.expressionList())));

		currentCFG.addNode(ret);
		return Pair.of(ret, ret);
	}

	@Override
	public Pair<Statement, Statement> visitBreakStmt(BreakStmtContext ctx) {
		NoOp breakSt = new NoOp(currentCFG);
		currentCFG.addNode(breakSt);
		currentCFG.addEdge(new SequentialEdge(breakSt, exitPoints.get(entryPoints.size())));
		return Pair.of(breakSt, breakSt);
	}

	@Override
	public Pair<Statement, Statement> visitContinueStmt(ContinueStmtContext ctx) {		
		NoOp continueSt = new NoOp(currentCFG);
		currentCFG.addNode(continueSt);
		currentCFG.addEdge(new SequentialEdge(continueSt, entryPoints.get(entryPoints.size())));
		return Pair.of(continueSt, continueSt);
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
	public Pair<Statement, Statement> visitDeferStmt(DeferStmtContext ctx) {
		GoDefer defer = new GoDefer(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression()));
		currentCFG.addNode(defer);
		return Pair.of(defer, defer);
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

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Statement, Statement> visitSwitchStmt(SwitchStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?,?>))
			throw new IllegalStateException("Pair of Statements expected");
		else 
			return (Pair<Statement, Statement>) result;	
	}

	@Override
	public Pair<Statement, Statement> visitExprSwitchStmt(ExprSwitchStmtContext ctx) {

		Expression switchGuard = ctx.expression() == null ? new GoBoolean(currentCFG, true) :  visitExpression(ctx.expression());
		NoOp exitNode = new NoOp(currentCFG);		

		currentCFG.addNode(switchGuard);
		currentCFG.addNode(exitNode);

		for (int i = 0; i < ctx.exprCaseClause().size(); i++)  {
			ExprCaseClauseContext switchCase = ctx.exprCaseClause(i);
			// Need to check if it is default/case
			Pair<Statement, Statement> caseBlock = visitStatementList(switchCase.statementList());
			Expression[] expsCase = visitExpressionList(switchCase.exprSwitchCase().expressionList());

			Expression caseBooleanGuard = null;

			for (int j = 0; j < expsCase.length; j++) {
				if (caseBooleanGuard == null)
					caseBooleanGuard = new GoEqual(currentCFG, expsCase[j], switchGuard);
				else
					caseBooleanGuard = new GoLogicalOr(currentCFG, caseBooleanGuard, new GoEqual(currentCFG, expsCase[j], switchGuard));
			}

			currentCFG.addNode(caseBooleanGuard);

			currentCFG.addEdge(new SequentialEdge(switchGuard, caseBooleanGuard));
			currentCFG.addEdge(new SequentialEdge(caseBooleanGuard, caseBlock.getLeft()));
			currentCFG.addEdge(new SequentialEdge(caseBlock.getRight(), exitNode));
		}

		Statement entryNode = switchGuard;
		if (ctx.simpleStmt() != null) {
			Pair<Statement, Statement> simpleStmt = visitSimpleStmt(ctx.simpleStmt());
			currentCFG.addEdge(new SequentialEdge(simpleStmt.getRight(), switchGuard));
			entryNode = simpleStmt.getLeft();
		}


		return Pair.of(entryNode, exitNode);
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
	public Pair<Statement, Statement> visitSelectStmt(SelectStmtContext ctx) {
		// TODO Auto-generated method stub
		Statement noop = new NoOp(currentCFG);
		currentCFG.addNode(noop);
		return Pair.of(noop, noop);
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
		// TODO: for ... range
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

			// Checking if condition is missing: if so, true is the boolean guard
			Statement cond = null;
			if (hasCondition) 
				cond = visitExpression(ctx.forClause().expression());
			else 
				cond = new GoBoolean(currentCFG, true);
			currentCFG.addNode(cond);
			entryPoints.add(cond);

			// Checking if post statement is missing
			Pair<Statement, Statement> post = null;
			if (hasPostStmt) {
				post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
				currentCFG.addNode(post.getLeft());
			}

			Statement exitNode = new NoOp(currentCFG);
			exitPoints.add(exitNode);
			currentCFG.addNode(exitNode);

			Pair<Statement, Statement> block;
			if (ctx.block() == null) {
				NoOp emptyBlock = new NoOp(currentCFG);
				block = Pair.of(emptyBlock, emptyBlock);
			} else
				block = visitBlock(ctx.block());

			Statement exitNodeBlock = block.getRight();
			Statement entryNodeOfBlock = block.getLeft();

			currentCFG.addEdge(new TrueEdge(cond, entryNodeOfBlock));
			currentCFG.addEdge(new FalseEdge(cond, exitNode));

			if (hasInitStmt) 
				currentCFG.addEdge(new SequentialEdge(init.getRight(), cond));
			else
				entryNode = cond; 

			if (hasPostStmt) {
				currentCFG.addEdge(new SequentialEdge(exitNodeBlock, post.getRight()));
				currentCFG.addEdge(new SequentialEdge(post.getLeft(), cond));
			} else 
				currentCFG.addEdge(new SequentialEdge(exitNodeBlock, cond));

			entryPoints.remove(entryPoints.size()-1);
			exitPoints.remove(exitPoints.size()-1);
			return Pair.of(entryNode, exitNode);
		}

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
	public GoType visitType_(Type_Context ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof GoType))
			throw new IllegalStateException("Type expected: " + result + " " + ctx.getText());
		else 
			return (GoType) result;		
	}

	@Override
	public GoType visitTypeName(TypeNameContext ctx) {
		if (ctx.IDENTIFIER() != null)
			return getGoType(ctx);
		else {
			Pair<String, String> pair = visitQualifiedIdent(ctx.qualifiedIdent());
			return GoQualifiedType.lookup(new GoQualifiedType(pair.getLeft(), pair.getRight()));
		}
	}

	@Override
	public GoType visitTypeLit(TypeLitContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof GoType))
			throw new IllegalStateException("Type expected: " + result + " " + ctx.getText());
		else 
			return (GoType) result;
	}

	@Override
	public GoType visitArrayType(ArrayTypeContext ctx) {
		GoType contentType = visitElementType(ctx.elementType());
		GoInteger length = visitArrayLength(ctx.arrayLength());
		return GoArrayType.lookup(new GoArrayType(contentType, length));
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
	public GoType visitElementType(ElementTypeContext ctx) {
		return visitType_(ctx.type_());
	}

	@Override
	public GoType visitPointerType(PointerTypeContext ctx) {
		return GoPointerType.lookup(new GoPointerType(visitType_(ctx.type_())));
	}

	@Override
	public GoType visitInterfaceType(InterfaceTypeContext ctx) {
		if (ctx.methodSpec().isEmpty())
			return GoInterfaceType.EMPTY_INTERFACE;
		else {
			Set<GoMethodSpecification> specs = new HashSet<>();
			for (MethodSpecContext methSpec : ctx.methodSpec())
				specs.add(visitMethodSpec(methSpec));

			return new GoInterfaceType(specs);				
		}
	}

	@Override
	public GoType visitSliceType(SliceTypeContext ctx) {
		return GoSliceType.lookup(new GoSliceType(visitElementType(ctx.elementType())));
	}

	@Override
	public GoType visitMapType(MapTypeContext ctx) {
		return GoMapType.lookup(new GoMapType(visitType_(ctx.type_()), visitElementType(ctx.elementType())));
	}

	@Override
	public GoType visitChannelType(ChannelTypeContext ctx) {
		GoType contentType = visitElementType(ctx.elementType());
		if (ctx.RECEIVE() == null)
			return GoChannelType.lookup(new GoChannelType(contentType));
		else if (getCol(ctx.CHAN().getSymbol()) < getCol(ctx.RECEIVE().getSymbol()))
			return GoChannelType.lookup(new GoChannelType(contentType, true, false));

		return GoChannelType.lookup(new GoChannelType(contentType, false, true));
	}

	@Override
	public GoMethodSpecification visitMethodSpec(MethodSpecContext ctx) {
		if (ctx.typeName() == null) {
			Type returnType = ctx.result() == null? Untyped.INSTANCE : visitResult(ctx.result());
			String name = ctx.IDENTIFIER().getText();
			Parameter[] params = visitParameters(ctx.parameters());
			return new GoMethodSpecification(name, returnType, params);
		} 

		throw new UnsupportedOperationException("Method specification not supported yet:  " + ctx.getText());
	}

	@Override
	public GoType visitFunctionType(FunctionTypeContext ctx) {
		SignatureContext sign = ctx.signature();
		Type returnType = visitResult(sign.result()); 
		Parameter[] params = visitParameters(sign.parameters());

		return GoFunctionType.lookup(new GoFunctionType(params, returnType));
	}

	@Override
	public Statement visitSignature(SignatureContext ctx) {
		// This method shold never be visited
		throw new IllegalStateException("Signature should never be visited");
	}

	@Override
	public Type visitResult(ResultContext ctx) {
		if (ctx.type_() != null)
			return visitType_(ctx.type_());
		else
			return new GoRawType(visitParameters(ctx.parameters()));
	}

	@Override
	public Parameter[] visitParameters(ParametersContext ctx) {
		Parameter[] result = new Parameter[]{};
		for (int i = 0; i < ctx.parameterDecl().size(); i++) 
			result = ArrayUtils.addAll(result, visitParameterDecl(ctx.parameterDecl(i)));

		return result;
	}

	@Override
	public Parameter[] visitParameterDecl(ParameterDeclContext ctx) {
		Parameter[] result = new Parameter[]{};
		GoType type = visitType_(ctx.type_());

		if (ctx.identifierList() == null)
			result = ArrayUtils.add(result, new Parameter("_", type));
		else 
			for (int i = 0; i < ctx.identifierList().IDENTIFIER().size(); i++) 
				result = ArrayUtils.addAll(result, new Parameter(ctx.identifierList().IDENTIFIER(i).getText(), type));

		return result;
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
			return new GoEqual(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go less (<)
		if (ctx.LESS() != null)
			return new GoLess(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater (>)
		if (ctx.GREATER() != null)
			return new GoGreater(currentCFG, filePath, getLine(ctx), getCol(ctx), visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

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

		if (ctx.conversion() != null) 
			return visitConversion(ctx.conversion());

		if (ctx.primaryExpr() != null) {

			// Check built-in functions
			if (ctx.primaryExpr().getText().equals("new")) {
				// new requires a type as input
				if (ctx.arguments().type_() != null)
					return new GoNew(currentCFG, visitType_(ctx.arguments().type_()));
				else {
					// TODO: this is a workaround...
					return new GoNew(currentCFG, parseType(ctx.arguments().expressionList().getText()));
				}
			}

			Expression primary = visitPrimaryExpr(ctx.primaryExpr());

			// Function call (e.g., f(1,2,3), x.f() )
			if (ctx.arguments() != null) {
				Expression[] args = visitArguments(ctx.arguments());
				if (primary instanceof Variable) {
					String funName = ((Variable) primary).getName();
					/* If the function name is found in cfgs, this call corresponds to a {@link CFGCall}
					 * otherwise it is an {@link OpenCall}.
					 */
					if (containsCFG(funName))
						return new CFGCall(currentCFG, "", getCFGByName(funName), args);
					else
						return new OpenCall(currentCFG, funName, args);
				}

				return new UnresolvedCall(currentCFG, primary.toString(), args);
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
		if (ctx.primaryExpr() != null)
			return visitPrimaryExpr(ctx.primaryExpr());

		Expression exp = visitExpression(ctx.expression());
		if (ctx.PLUS() != null)
			return new GoPlus(currentCFG, exp);

		if (ctx.MINUS() != null)
			return new GoMinus(currentCFG, exp);

		if (ctx.EXCLAMATION() != null)
			return new GoNot(currentCFG, exp);

		if (ctx.STAR() != null)
			return new GoRef(currentCFG, exp);

		if (ctx.AMPERSAND() != null)
			return new GoDeref(currentCFG, exp);

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
		int line = getLine(ctx);
		int col = getCol(ctx);

		//TODO: for the moment, we skip any other integer literal format (e.g., octal, imaginary)
		if (ctx.DECIMAL_LIT() != null)
			return new GoInteger(currentCFG, filePath, line, col, Integer.parseInt(ctx.DECIMAL_LIT().getText()));

		// TODO: 0 matched as octacl literal and not decimal literal
		if (ctx.OCTAL_LIT() != null)
			return new GoInteger(currentCFG, filePath, line, col, Integer.parseInt(ctx.OCTAL_LIT().getText()));

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
	public Pair<String, String> visitQualifiedIdent(QualifiedIdentContext ctx) {
		return Pair.of(ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Statement visitCompositeLit(CompositeLitContext ctx) {

		GoType type = visitLiteralType(ctx.literalType());
		Object raw = visitLiteralValue(ctx.literalValue());

		if (raw instanceof Map<?, ?>)
			return new GoKeyedLiteral(currentCFG, (Map<String, Expression>) raw,  type);
		else
			return new GoNonKeyedLiteral(currentCFG, (List<Expression>) raw, type);
	}

	@Override
	public GoType visitLiteralType(LiteralTypeContext ctx) {
		Object child = visitChildren(ctx);
		if (!(child instanceof Type))
			throw new IllegalStateException("Type expected");
		else
			return (GoType) child;
	}

	@Override
	public Object visitLiteralValue(LiteralValueContext ctx) {
		if (ctx.elementList() == null)
			return new HashMap<Expression, Expression>();
		return visitElementList(ctx.elementList());
	}

	@Override
	public Object visitElementList(ElementListContext ctx) {
		// All keyed or all without key
		Object firstElement = visitKeyedElement(ctx.keyedElement(0));

		if (firstElement instanceof Pair<?,?>) {
			Map<Expression, Expression> result = new HashMap<Expression, Expression>();
			@SuppressWarnings("unchecked")
			Pair<Expression, Expression> firstKeyed = (Pair<Expression, Expression>) firstElement;
			result.put(firstKeyed.getLeft(), firstKeyed.getRight());
			for (int i = 1; i < ctx.keyedElement().size(); i++) {
				@SuppressWarnings("unchecked")
				Pair<Expression, Expression> keyed = (Pair<Expression, Expression>) visitKeyedElement(ctx.keyedElement(i));
				result.put(keyed.getLeft(), keyed.getRight());
			}

			return result;
		} else {
			List<Expression> result = new ArrayList<Expression>();
			result.add((Expression) firstElement);
			for (int i = 1; i < ctx.keyedElement().size(); i++) {
				Expression elem = (Expression) visitKeyedElement(ctx.keyedElement(i));
				result.add(elem);
			}

			return result;
		}
	}

	@Override
	public Object visitKeyedElement(KeyedElementContext ctx) {
		if (ctx.key() != null)
			return Pair.of(visitKey(ctx.key()), visitElement(ctx.element()));
		else
			return visitElement(ctx.element());
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
		Map<String, GoType> fields = new HashMap<String, GoType>();

		for (FieldDeclContext field : ctx.fieldDecl()) 
			for (Pair<String, GoType> fd : visitFieldDecl(field))
				fields.put(fd.getLeft(), fd.getRight());

		return new GoStructType(fields);
	}

	@Override
	public List<Pair<String, GoType>> visitFieldDecl(FieldDeclContext ctx) {
		List<Pair<String, GoType>> result = new ArrayList<>();

		if (ctx.anonymousField() != null)
			result.add(visitAnonymousField(ctx.anonymousField()));
		else {
			GoType fieldType = visitType_(ctx.type_());
			for (TerminalNode f : ctx.identifierList().IDENTIFIER())
				result.add(Pair.of(f.getText(), fieldType));
		}

		return result;
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
	public Pair<String, GoType> visitAnonymousField(AnonymousFieldContext ctx) {
		GoType type = visitTypeName(ctx.typeName());
		if (ctx.STAR() != null)
			type = new GoPointerType(type);
		return Pair.of(ctx.typeName().getText(), type);
	}

	@Override
	public Expression visitFunctionLit(FunctionLitContext ctx) {
		// TODO: function literal
		return new GoInteger(currentCFG, 1);
	}

	@Override
	public Expression visitIndex(IndexContext ctx) {
		return visitExpression(ctx.expression());
	}

	@Override
	public Statement visitSlice(SliceContext ctx) {
		// TODO: slice
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitTypeAssertion(TypeAssertionContext ctx) {
		// TODO: type assertion
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression[] visitArguments(ArgumentsContext ctx) {
		Expression[] exps = new Expression[]{};
		if (ctx.expressionList() != null)
			for (int i = 0; i < ctx.expressionList().expression().size(); i++)
				exps = ArrayUtils.addAll(exps, visitExpression(ctx.expressionList().expression(i)));
		return exps;
	}

	@Override
	public Statement visitMethodExpr(MethodExprContext ctx) {
		// TODO: method expression
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO: receiver type
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitEos(EosContext ctx) {
		// This method should never be visited
		throw new UnsupportedOperationException("Eos should never be visited");
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
	private GoType getGoType(TypeNameContext ctx) {

		if (ctx.IDENTIFIER() != null) {

			String type = ctx.IDENTIFIER().getText();
			switch(type) { 
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
			case "error":
				return GoErrorType.INSTANCE;
			default: 
				if (GoStructType.hasStructType(type))
					return GoStructType.get(type);
				else if (GoAliasType.hasAliasType(type))
					return GoAliasType.get(type);
				else if (GoInterfaceType.hasStructType(type))
					return GoInterfaceType.get(type);
				else {
					parseTypeDeclaration(type);
					return getGoType(ctx);
				}
			} 
		} 

		Pair<String, String> pair = visitQualifiedIdent(ctx.qualifiedIdent());
		return GoQualifiedType.lookup(new GoQualifiedType(pair.getLeft(), pair.getRight()));
	}

	private void parseTypeDeclaration(String id) {
		for (DeclarationContext d : source.declaration())
			if (d.typeDecl() != null) {
				for (TypeSpecContext ts : d.typeDecl().typeSpec())
					if (ts.IDENTIFIER().getText().equals(id))
						visitTypeSpec(ts);
			}
	}

	private GoType parseType(String type) {
		InputStream stream = new ByteArrayInputStream(type.getBytes());
		GoLexer lexer = null;

		try {
			lexer = new GoLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GoParser parser = new GoParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.type_();

		GoType t = visitType_((Type_Context) tree);
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}
}
