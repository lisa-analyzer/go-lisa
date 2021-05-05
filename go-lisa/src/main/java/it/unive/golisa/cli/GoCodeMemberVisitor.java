package it.unive.golisa.cli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParser.AnonymousFieldContext;
import it.unive.golisa.antlr.GoParser.ArgumentsContext;
import it.unive.golisa.antlr.GoParser.ArrayLengthContext;
import it.unive.golisa.antlr.GoParser.ArrayTypeContext;
import it.unive.golisa.antlr.GoParser.Assign_opContext;
import it.unive.golisa.antlr.GoParser.AssignmentContext;
import it.unive.golisa.antlr.GoParser.BasicLitContext;
import it.unive.golisa.antlr.GoParser.BlockContext;
import it.unive.golisa.antlr.GoParser.BreakStmtContext;
import it.unive.golisa.antlr.GoParser.ChannelTypeContext;
import it.unive.golisa.antlr.GoParser.CommCaseContext;
import it.unive.golisa.antlr.GoParser.CommClauseContext;
import it.unive.golisa.antlr.GoParser.CompositeLitContext;
import it.unive.golisa.antlr.GoParser.ConstDeclContext;
import it.unive.golisa.antlr.GoParser.ConstSpecContext;
import it.unive.golisa.antlr.GoParser.ContinueStmtContext;
import it.unive.golisa.antlr.GoParser.ConversionContext;
import it.unive.golisa.antlr.GoParser.DeclarationContext;
import it.unive.golisa.antlr.GoParser.DeferStmtContext;
import it.unive.golisa.antlr.GoParser.ElementContext;
import it.unive.golisa.antlr.GoParser.ElementListContext;
import it.unive.golisa.antlr.GoParser.ElementTypeContext;
import it.unive.golisa.antlr.GoParser.EmptyStmtContext;
import it.unive.golisa.antlr.GoParser.EosContext;
import it.unive.golisa.antlr.GoParser.ExprCaseClauseContext;
import it.unive.golisa.antlr.GoParser.ExprSwitchCaseContext;
import it.unive.golisa.antlr.GoParser.ExprSwitchStmtContext;
import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.ExpressionListContext;
import it.unive.golisa.antlr.GoParser.ExpressionStmtContext;
import it.unive.golisa.antlr.GoParser.FallthroughStmtContext;
import it.unive.golisa.antlr.GoParser.FieldDeclContext;
import it.unive.golisa.antlr.GoParser.ForClauseContext;
import it.unive.golisa.antlr.GoParser.ForStmtContext;
import it.unive.golisa.antlr.GoParser.FunctionLitContext;
import it.unive.golisa.antlr.GoParser.GoStmtContext;
import it.unive.golisa.antlr.GoParser.GotoStmtContext;
import it.unive.golisa.antlr.GoParser.IdentifierListContext;
import it.unive.golisa.antlr.GoParser.IfStmtContext;
import it.unive.golisa.antlr.GoParser.IncDecStmtContext;
import it.unive.golisa.antlr.GoParser.IndexContext;
import it.unive.golisa.antlr.GoParser.IntegerContext;
import it.unive.golisa.antlr.GoParser.InterfaceTypeContext;
import it.unive.golisa.antlr.GoParser.KeyContext;
import it.unive.golisa.antlr.GoParser.KeyedElementContext;
import it.unive.golisa.antlr.GoParser.LabeledStmtContext;
import it.unive.golisa.antlr.GoParser.LiteralContext;
import it.unive.golisa.antlr.GoParser.LiteralTypeContext;
import it.unive.golisa.antlr.GoParser.LiteralValueContext;
import it.unive.golisa.antlr.GoParser.MapTypeContext;
import it.unive.golisa.antlr.GoParser.MethodDeclContext;
import it.unive.golisa.antlr.GoParser.MethodExprContext;
import it.unive.golisa.antlr.GoParser.MethodSpecContext;
import it.unive.golisa.antlr.GoParser.OperandContext;
import it.unive.golisa.antlr.GoParser.OperandNameContext;
import it.unive.golisa.antlr.GoParser.ParameterDeclContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.PointerTypeContext;
import it.unive.golisa.antlr.GoParser.PrimaryExprContext;
import it.unive.golisa.antlr.GoParser.QualifiedIdentContext;
import it.unive.golisa.antlr.GoParser.RangeClauseContext;
import it.unive.golisa.antlr.GoParser.ReceiverTypeContext;
import it.unive.golisa.antlr.GoParser.RecvStmtContext;
import it.unive.golisa.antlr.GoParser.ResultContext;
import it.unive.golisa.antlr.GoParser.ReturnStmtContext;
import it.unive.golisa.antlr.GoParser.SelectStmtContext;
import it.unive.golisa.antlr.GoParser.SendStmtContext;
import it.unive.golisa.antlr.GoParser.ShortVarDeclContext;
import it.unive.golisa.antlr.GoParser.SignatureContext;
import it.unive.golisa.antlr.GoParser.SimpleStmtContext;
import it.unive.golisa.antlr.GoParser.SliceContext;
import it.unive.golisa.antlr.GoParser.SliceTypeContext;
import it.unive.golisa.antlr.GoParser.SourceFileContext;
import it.unive.golisa.antlr.GoParser.StatementContext;
import it.unive.golisa.antlr.GoParser.StatementListContext;
import it.unive.golisa.antlr.GoParser.String_Context;
import it.unive.golisa.antlr.GoParser.StructTypeContext;
import it.unive.golisa.antlr.GoParser.SwitchStmtContext;
import it.unive.golisa.antlr.GoParser.TypeAssertionContext;
import it.unive.golisa.antlr.GoParser.TypeCaseClauseContext;
import it.unive.golisa.antlr.GoParser.TypeDeclContext;
import it.unive.golisa.antlr.GoParser.TypeListContext;
import it.unive.golisa.antlr.GoParser.TypeLitContext;
import it.unive.golisa.antlr.GoParser.TypeNameContext;
import it.unive.golisa.antlr.GoParser.TypeSpecContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchCaseContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchGuardContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchStmtContext;
import it.unive.golisa.antlr.GoParser.Type_Context;
import it.unive.golisa.antlr.GoParser.UnaryExprContext;
import it.unive.golisa.antlr.GoParser.VarDeclContext;
import it.unive.golisa.antlr.GoParser.VarSpecContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.expression.GoNew;
import it.unive.golisa.cfg.expression.GoTypeConversion;
import it.unive.golisa.cfg.expression.binary.GoBitwiseAnd;
import it.unive.golisa.cfg.expression.binary.GoBitwiseOr;
import it.unive.golisa.cfg.expression.binary.GoBitwiseXOr;
import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.expression.binary.GoEqual;
import it.unive.golisa.cfg.expression.binary.GoGreater;
import it.unive.golisa.cfg.expression.binary.GoGreaterOrEqual;
import it.unive.golisa.cfg.expression.binary.GoLeftShift;
import it.unive.golisa.cfg.expression.binary.GoLess;
import it.unive.golisa.cfg.expression.binary.GoLessOrEqual;
import it.unive.golisa.cfg.expression.binary.GoLogicalAnd;
import it.unive.golisa.cfg.expression.binary.GoLogicalOr;
import it.unive.golisa.cfg.expression.binary.GoModule;
import it.unive.golisa.cfg.expression.binary.GoMul;
import it.unive.golisa.cfg.expression.binary.GoRightShift;
import it.unive.golisa.cfg.expression.binary.GoSubtraction;
import it.unive.golisa.cfg.expression.binary.GoSum;
import it.unive.golisa.cfg.expression.binary.GoTypeAssertion;
import it.unive.golisa.cfg.expression.literal.GoBoolean;
import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.expression.literal.GoKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoRawValue;
import it.unive.golisa.cfg.expression.literal.GoRune;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.golisa.cfg.expression.ternary.GoSimpleSlice;
import it.unive.golisa.cfg.expression.unary.GoBitwiseNot;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.expression.unary.GoLength;
import it.unive.golisa.cfg.expression.unary.GoMinus;
import it.unive.golisa.cfg.expression.unary.GoNot;
import it.unive.golisa.cfg.expression.unary.GoPlus;
import it.unive.golisa.cfg.expression.unary.GoRef;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.statement.GoFallThrough;
import it.unive.golisa.cfg.statement.GoReturn;
import it.unive.golisa.cfg.statement.assignment.GoConstantDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoMultiShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.statement.method.GoMethod;
import it.unive.golisa.cfg.statement.method.GoReceiver;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoQualifiedType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoAliasType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
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
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.FalseEdge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.edge.TrueEdge;
import it.unive.lisa.program.cfg.statement.AccessUnitGlobal;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;

/**
 * A {@link GoParserBaseVisitor} that will parse the code of an Go method 
 */
public class GoCodeMemberVisitor extends GoParserBaseVisitor<Object> {

	protected final String file;

	protected final AdjacencyMatrix<Statement, Edge, CFG> matrix;

	protected final Collection<Statement> entrypoints;

	protected final Collection<ControlFlowStructure> cfs;

	private final Map<String, VariableRef> visibleIds;

	protected CFG cfg;

	protected CFGDescriptor descriptor;
	
	protected final Program program;
	
	protected final SourceFileContext source;
	
	/**
	 * Stack of loop exit points (used for break statements)
	 */
	private List<Statement> exitPoints = new ArrayList<Statement>();

	/**
	 * Stack of loop entry points (used for continues statements)
	 */
	private List<Statement> entryPoints = new ArrayList<Statement>();
	

	/**
	 * Current compilation unit to parse
	 */
	protected CompilationUnit currentUnit;

	
	public GoCodeMemberVisitor(String file, Program program, SourceFileContext source) {
		this.file = file;
		this.program = program;
		this.source = source;
		matrix = new AdjacencyMatrix<>();
		entrypoints = new HashSet<>();
		cfs = new LinkedList<>();
		visibleIds = new HashMap<>();
	}
	

	public GoCodeMemberVisitor(CompilationUnit packageUnit, MethodDeclContext ctx, String file, Program program,
			SourceFileContext source) {
		this.file = file;
		this.descriptor = mkDescriptor(packageUnit,ctx);
		this.program = program;
		this.source = source;

		matrix = new AdjacencyMatrix<>();
		entrypoints = new HashSet<>();
		cfs = new LinkedList<>();
		// side effects on entrypoints and matrix will affect the cfg
		cfg = new CFG(descriptor, entrypoints, matrix);

		visibleIds = new HashMap<>();
		
		initializeVisibleIds();	
	}
	

	protected void initializeVisibleIds() {
		for (VariableTableEntry par : descriptor.getVariables())
			visibleIds.put(par.getName(), par.createReference(cfg));
	}
	
	private CFGDescriptor mkDescriptor(CompilationUnit packageUnit, MethodDeclContext ctx) {
		return new CFGDescriptor(locationOf(ctx), packageUnit, false, ctx.IDENTIFIER().getText(), visitParameters(ctx.signature().parameters()));
	}
	
	/**
	 * Visits the code of a {@link BlockContext} representing the code block of
	 * a method or constructor.
	 * 
	 * @param ctx the block context
	 * 
	 * @return the {@link CFG} built from the block
	 */
	CFG visitCodeMember(MethodDeclContext ctx) {

			GoReceiver receiver = (GoReceiver) visitReceiver(ctx.receiver());
			String methodName = ctx.IDENTIFIER().getText();
			Parameter[] params = visitParameters(ctx.signature().parameters());
			Type returnType = ctx.signature().result() == null ? Untyped.INSTANCE : visitResult(ctx.signature().result());

			String unitName = receiver.getStaticType().isPointerType() ? ((GoPointerType)receiver.getStaticType()).getBaseType().toString() : receiver.getStaticType().toString();

			SourceCodeLocation location = locationOf(ctx);
			if (program.getUnit(unitName) == null) 
				// TODO: unknown unit
				currentUnit = new CompilationUnit(location, unitName, false);
			else
				currentUnit = program.getUnit(unitName);

			GoMethod method = new GoMethod(new CFGDescriptor(location, currentUnit, true, methodName, returnType, params), receiver);
			cfg = method;
			Pair<Statement, Statement> body = visitBlock(ctx.block());

			cfg.getEntrypoints().add(body.getLeft());
			cfg.simplify();
			currentUnit.addInstanceCFG(method);
			return method;
	}


	static protected int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	} 

	static protected int getLine(TerminalNode ctx) {
		return ctx.getSymbol().getLine();
	} 
	
	static protected int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	} 
	
	static protected int getCol(TerminalNode ctx) {
		return ctx.getSymbol().getCharPositionInLine();
	} 
	
	protected SourceCodeLocation locationOf(ParserRuleContext ctx) {
		return new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
	}
	
	protected SourceCodeLocation locationOf(TerminalNode ctx) {
		return new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
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
			result = ArrayUtils.add(result, new Parameter(new SourceCodeLocation(file, getLine(ctx), getCol(ctx)), "_", type));
		else 
			for (int i = 0; i < ctx.identifierList().IDENTIFIER().size(); i++) { 
				int line = getLine(ctx.identifierList().IDENTIFIER(i));
				int col = getCol(ctx.identifierList().IDENTIFIER(i));
				result = ArrayUtils.addAll(result, new Parameter(new SourceCodeLocation(file, line, col), ctx.identifierList().IDENTIFIER(i).getText(), type));
			}

		return result;
	}
	
	@Override
	public GoType visitType_(Type_Context ctx) {

		if (ctx.typeName() != null)
			return visitTypeName(ctx.typeName());

		if (ctx.typeLit() != null)
			return visitTypeLit(ctx.typeLit());

		return (GoType) visitChildren(ctx);
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
	public Pair<String, String> visitQualifiedIdent(QualifiedIdentContext ctx) {
		return Pair.of(ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
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
	public Type visitResult(ResultContext ctx) {
		if (ctx == null)
			return Untyped.INSTANCE;
		if (ctx.type_() != null)
			return visitType_(ctx.type_());
		else
			return new GoRawType(visitParameters(ctx.parameters()));
	}
	
	@Override
	public Pair<Statement, Statement> visitBlock(BlockContext ctx) {
		Map<String, VariableRef> backup = new HashMap<>(visibleIds);

		if (ctx.statementList() == null) {
			SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
			NoOp entry = new NoOp(cfg, location);
			NoOp exit = new NoOp(cfg, location);
			cfg.addNode(entry);
			cfg.addNode(exit);
			addEdge(new SequentialEdge(entry, exit));
			updateVisileIds(backup, exit);
			
			return Pair.of(entry, exit);
		}
		
		Pair<Statement, Statement> res = visitStatementList(ctx.statementList());
		updateVisileIds(backup, res.getRight());
		
		return res;
	}
	private void updateVisileIds(Map<String, VariableRef> backup,Statement last) {
		
		Collection<String> toRemove = new HashSet<>();
		for (Entry<String, VariableRef> id : visibleIds.entrySet())
			if (!backup.containsKey(id.getKey())) {
				VariableRef ref = id.getValue();
				descriptor.addVariable(new VariableTableEntry(ref.getLocation(),
						0, ref.getRootStatement(), last, id.getKey(), Untyped.INSTANCE));
				toRemove.add(id.getKey());
			}

		if (!toRemove.isEmpty())
			toRemove.forEach(visibleIds::remove);
	}

	@Override
	public Pair<Statement, Statement> visitStatementList(StatementListContext ctx) {		
		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ctx.statement().size(); i++)  {
			Pair<Statement, Statement> currentStmt = visitStatement(ctx.statement(i));

			if (lastStmt != null) 
				addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));	
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

	@Override
	public Pair<Statement, Statement> visitVarDecl(VarDeclContext ctx) {
		Statement lastStmt = null;
		Statement entryNode = null;

		for (VarSpecContext varSpec : ctx.varSpec()) {
			Pair<Statement, Statement> currStmt = visitVarSpec(varSpec);

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()));
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

			Expression exp = (exps == null || exps.expression(i) == null) && !type.isUntyped() ? ((GoType) type).defaultValue(cfg, locationOf(ctx)) : visitExpression(exps.expression(i));

			int line = getLine(ids.IDENTIFIER(i));
			int col = (exps == null || exps.expression(i) == null) ? getCol(ids.IDENTIFIER(i)) : getCol(exps.expression(i));

			VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(), type);
			GoVariableDeclaration asg = new GoVariableDeclaration(cfg, new SourceCodeLocation(file, line, col), type, target, exp);
			cfg.addNode(asg);
			
//		 	if (visibleIds.containsKey(target.getName()))
//				throw new GoSyntaxException(
//						"Duplicate variable '" + target.getName() + "' declared at " + target.getLocation());
//
//			visibleIds.put(target.getName(), target);
			

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
	}
	
	@Override
	public Expression visitExpression(ExpressionContext ctx) {	
		SourceCodeLocation location = locationOf(ctx);

		// Go sum (+)
		if (ctx.PLUS() != null)
			return new GoSum(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go multiplication (*)
		if (ctx.STAR() != null) 
			return new GoMul(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go division (/)
		if (ctx.DIV() != null)
			return new GoDiv(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go minus (-)
		if (ctx.MINUS() != null)
			return new GoSubtraction(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&&)
		if (ctx.LOGICAL_AND() != null) 
			return new GoLogicalAnd(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (||)
		if (ctx.LOGICAL_OR() != null)
			return new GoLogicalOr(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go equal (==)
		if (ctx.EQUALS() != null)
			return new GoEqual(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go not equal (!=)
		if (ctx.NOT_EQUALS() != null)
			return new GoNot(cfg, location, new GoEqual(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1))));

		// Go less (<)
		if (ctx.LESS() != null)
			return new GoLess(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater (>)
		if (ctx.GREATER() != null)
			return new GoGreater(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater or equals (>=)
		if (ctx.GREATER_OR_EQUALS() != null)
			return new GoGreaterOrEqual(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go less or equals (>=)
		if (ctx.LESS_OR_EQUALS() != null)
			return new GoLessOrEqual(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go module (%)
		if (ctx.MOD() != null)
			return new GoModule(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go right shift (>>)
		if (ctx.RSHIFT() != null)
			return new GoRightShift(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go left shift (<<)
		if (ctx.LSHIFT() != null)
			return new GoLeftShift(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go XOR (^)
		if (ctx.CARET() != null)
			return new GoBitwiseXOr(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go or (|)
		if (ctx.OR() != null)
			return new GoBitwiseOr(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go and (&)
		if (ctx.AMPERSAND() != null)
			return new GoBitwiseAnd(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
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

					if (parseTypeDeclaration(type))
						return getGoType(ctx);
					else {
						CompilationUnit unit = new CompilationUnit(new SourceCodeLocation(file, getLine(ctx), getCol(ctx)), type, false);
						return GoStructType.lookup(type, unit);
					}
				}
			} 
		} 

		Pair<String, String> pair = visitQualifiedIdent(ctx.qualifiedIdent());
		return GoQualifiedType.lookup(new GoQualifiedType(pair.getLeft(), pair.getRight()));
	}
	
	private boolean parseTypeDeclaration(String id) {
		for (DeclarationContext d : source.declaration())
			if (d.typeDecl() != null) {
				for (TypeSpecContext ts : d.typeDecl().typeSpec())
					if (ts.IDENTIFIER().getText().equals(id)) {
						visitTypeSpec(ts);
						return true;
					}
			}
		return false;
	}

	@Override
	public Collection<CompilationUnit> visitTypeDecl(TypeDeclContext ctx) {
		HashSet<CompilationUnit> units = new HashSet<>();
		for (TypeSpecContext typeSpec : ctx.typeSpec()) {
			String unitName = typeSpec.IDENTIFIER().getText();
			CompilationUnit unit = new CompilationUnit(locationOf(typeSpec), unitName, false);
			units.add(unit);
			currentUnit = unit;
			visitTypeSpec(typeSpec);
		}
		return units;
	}
	
	@Override
	public GoType visitTypeSpec(TypeSpecContext ctx) {
		String typeName = ctx.IDENTIFIER().getText();
		GoType type = visitType_(ctx.type_());

		if (ctx.ASSIGN() == null) {
			if (type instanceof GoStructType) 
				return GoStructType.lookup(typeName, currentUnit);
			else if (type instanceof GoInterfaceType)
				return GoInterfaceType.lookup(typeName, currentUnit);
			else 
				return GoAliasType.lookup(typeName, new GoAliasType(typeName, type));

		} else {
			// Alias type
			GoAliasType.lookup(typeName, new GoAliasType(typeName, type));
		}

		return null;
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
				addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()));
			else
				entryNode = currStmt.getLeft();

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
			VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(), type);
			Expression exp = visitExpression(exps.expression(i));

			GoConstantDeclaration asg = new GoConstantDeclaration(cfg, locationOf(ctx), target, exp);
			cfg.addNode(asg);
			
			if (visibleIds.containsKey(target.getName()))
				throw new GoSyntaxException(
						"Duplicate variable '" + target.getName() + "' declared at " + target.getLocation());

			visibleIds.put(target.getName(), target);
			
			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Expression[] visitExpressionList(ExpressionListContext ctx) {
		Expression[] result = new Expression[] {};
		for (int i = 0; i < ctx.expression().size(); i++)
			result = ArrayUtils.addAll(result, visitExpression(ctx.expression(i)));
		return result;
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
			cfg.addNode((Expression) result);
			return Pair.of((Expression) result, (Expression) result);
		} else if (!(result instanceof Pair<?,?>)) {
			throw new IllegalStateException("Pair of Statements expected");
		} else 
			return (Pair<Statement, Statement>) result;		
	}
	

	@Override
	public Pair<Statement, Statement> visitIncDecStmt(IncDecStmtContext ctx) {

		Expression exp = visitExpression(ctx.expression());
		Statement asg = null;

		int line = getLine(ctx.expression());
		int col = getCol(ctx.expression());
		SourceCodeLocation location = new SourceCodeLocation(file, line, col);

		// increment and decrement statements are syntactic sugar
		// e.g., x++ -> x = x + 1 and x-- -> x = x - 1
		if (ctx.PLUS_PLUS() != null)
			asg = new Assignment(cfg, location, exp, 
					new GoSum(cfg, location, exp,  new GoInteger(cfg, location, 1)));		
		else
			asg = new Assignment(cfg, location, exp, 
					new GoSubtraction(cfg, location, exp, new GoInteger(cfg, location, 1)));

		cfg.addNode(asg);
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
			Expression exp = buildExpressionFromAssignment(file, line, col, lhs, ctx.assign_op(), visitExpression(exps.expression(i)));

			Assignment asg = new Assignment(cfg, new SourceCodeLocation(file, line, col), lhs, exp);
			cfg.addNode(asg);

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, asg));
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Pair.of(entryNode, lastStmt);
	}

	private Expression buildExpressionFromAssignment(String filePath, int line, int col, Expression lhs, Assign_opContext op, Expression exp) {

		// +=
		if (op.PLUS() != null)
			return new GoSum(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// -=	
		if (op.MINUS() != null)
			return new GoSubtraction(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// *=
		if (op.STAR() != null)
			return new GoMul(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// /=
		if (op.DIV() != null)
			return new GoDiv(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// %=
		if (op.MOD() != null)
			return new GoModule(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// >>=
		if (op.RSHIFT() != null)
			return new GoRightShift(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// <<=
		if (op.LSHIFT() != null)
			return new GoLeftShift(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// &^=
		if (op.BIT_CLEAR() != null)
			throw new UnsupportedOperationException("Unsupported assignment operator: " + op.getText());

		// ^=
		if (op.CARET() != null)
			return new GoBitwiseXOr(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// &=
		if (op.AMPERSAND() != null)
			return new GoBitwiseAnd(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

		// |=
		if (op.OR() != null)
			return new GoBitwiseOr(cfg, new SourceCodeLocation(filePath, line, col), lhs, exp);

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
				
		// Number of identifiers to be assigned
		int sizeIds = ids.IDENTIFIER().size();
		// Numbefr of expressions to be assigned
		int sizeExps = exps.expression().size();

		Statement lastStmt = null;
		Statement entryNode = null;

		// Multi variable short declaration
		if (sizeIds != sizeExps) {
			int line = getLine(ctx);
			int col = getCol(ctx);

			VariableRef[] left = visitIdentifierList(ctx.identifierList());
			Expression right = visitExpression(exps.expression(0));

			GoMultiShortVariableDeclaration asg = new GoMultiShortVariableDeclaration(cfg, file, line, col, Untyped.INSTANCE, left, right);
			cfg.addNode(asg);
			return Pair.of(asg, asg);
		} else {

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp = visitExpression(exps.expression(i));

				int line = getLine(ids.IDENTIFIER(i));
				int col = getCol(exps.expression(i));

				// The type of the variable is implicit and it is retrieved from the type of exp
				Type type = exp.getStaticType();
				VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(), type);

//				if (visibleIds.containsKey(target.getName()))
//					throw new GoSyntaxException(
//							"Duplicate variable '" + target.getName() + "' declared at " + target.getLocation());
//
//				visibleIds.put(target.getName(), target);
				
				GoShortVariableDeclaration asg = new GoShortVariableDeclaration(cfg, file, line, col, target, exp);
				cfg.addNode(asg);

				if (lastStmt != null)
					addEdge(new SequentialEdge(lastStmt, asg));
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Pair.of(entryNode, lastStmt);
		}
	}

	@Override
	public VariableRef[] visitIdentifierList(IdentifierListContext ctx) {
		VariableRef[] result = new VariableRef[] {};
		for (int i = 0; i < ctx.IDENTIFIER().size(); i++)
			result = ArrayUtils.addAll(result, new VariableRef(cfg, locationOf(ctx.IDENTIFIER(i)), ctx.IDENTIFIER(i).getText()));
		return result;
	}
	
	@Override
	public Statement visitEmptyStmt(EmptyStmtContext ctx) {
		return new NoOp(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)));
	}

	@Override
	public Pair<Statement, Statement> visitReturnStmt(ReturnStmtContext ctx) {		
		SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));

		if (ctx.expressionList() != null) {
			GoReturn ret;
			if (ctx.expressionList().expression().size() == 1) 
				ret =  new GoReturn(cfg, location, visitExpression(ctx.expressionList().expression(0)));
			else
				ret =  new GoReturn(cfg, location, new GoRawValue(cfg, location, visitExpressionList(ctx.expressionList())));

			cfg.addNode(ret);
			return Pair.of(ret, ret);
		} else {
			// TODO: we should check if cfg specifies the return values
			Ret ret = new Ret(cfg, location);
			cfg.addNode(ret);
			return Pair.of(ret, ret);
		}
	}

	@Override
	public Pair<Statement, Statement> visitBreakStmt(BreakStmtContext ctx) {
		NoOp breakSt = new NoOp(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)));
		cfg.addNode(breakSt);
		addEdge(new SequentialEdge(breakSt, exitPoints.get(entryPoints.size() -1)));
		return Pair.of(breakSt, breakSt);
	}

	@Override
	public Pair<Statement, Statement> visitContinueStmt(ContinueStmtContext ctx) {		
		NoOp continueSt = new NoOp(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)));
		cfg.addNode(continueSt);
		addEdge(new SequentialEdge(continueSt, entryPoints.get(entryPoints.size() -1)));
		return Pair.of(continueSt, continueSt);
	}

	@Override
	public Statement visitGotoStmt(GotoStmtContext ctx) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Pair<Statement, Statement> visitFallthroughStmt(FallthroughStmtContext ctx) {
		GoFallThrough ft = new GoFallThrough(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)));
		cfg.addNode(ft);
		return Pair.of(ft, ft);
	}

	@Override
	public Pair<Statement, Statement> visitDeferStmt(DeferStmtContext ctx) {
		GoDefer defer = new GoDefer(cfg, file, getLine(ctx), getCol(ctx), visitExpression(ctx.expression()));
		cfg.addNode(defer);
		return Pair.of(defer, defer);
	}

	@Override
	public Pair<Statement, Statement> visitIfStmt(IfStmtContext ctx) {
		SourceCodeLocation location = locationOf(ctx);
		
		// Visit if statement Boolean Guard
		Statement booleanGuard = visitExpression(ctx.expression());
		cfg.addNode(booleanGuard);

		NoOp ifExitNode = new NoOp(cfg, location);
		cfg.addNode(ifExitNode);

		Pair<Statement, Statement> trueBlock = visitBlock(ctx.block(0));
		Statement exitStatementTrueBranch = trueBlock.getRight();
		Statement entryStatementTrueBranch = trueBlock.getLeft();

		if (ctx.ELSE() == null) {
			// If statement without else branch
			addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));			
			addEdge(new FalseEdge(booleanGuard, ifExitNode));			
			addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
		} else {
			if (ctx.block(1) != null) {
				// If statement with else branch with no other if statements 
				Pair<Statement, Statement> falseBlock = visitBlock(ctx.block(1));
				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			} else {
				// If statement with else branch with other if statements 
				Pair<Statement, Statement> falseBlock = visitIfStmt(ctx.ifStmt());
				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch));
				addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch));

				addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode));
				addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode));
			}
		}

		// Checks whether the if-statement has an initial statement
		// e.g., if x := y; z < x block 
		Statement entryNode = booleanGuard;
		if (ctx.simpleStmt() != null) {
			Pair<Statement, Statement> initialStmt = visitSimpleStmt(ctx.simpleStmt());
			entryNode = initialStmt.getLeft();
			addEdge(new SequentialEdge(initialStmt.getRight(), booleanGuard));
		} 

		return Pair.of(entryNode, ifExitNode);
	}


	private boolean isReturnStmt(Statement stmt) {
		return stmt instanceof GoReturn || stmt instanceof Ret;
	}

	private void addEdge(Edge edge) {
		if (!isReturnStmt(edge.getSource()))
			cfg.addEdge(edge);
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
		SourceCodeLocation location = locationOf(ctx);
		Expression switchGuard = ctx.expression() == null ? new GoBoolean(cfg, location, true) :  visitExpression(ctx.expression());
		NoOp exitNode = new NoOp(cfg, location);		
		Statement entryNode = null;
		Statement previousGuard = null;
		Pair<Statement, Statement> defaultBlock = null;
		Pair<Statement, Statement> lastCaseBlock = null;
		cfg.addNode(exitNode);

		for (int i = 0; i < ctx.exprCaseClause().size(); i++)  {
			ExprCaseClauseContext switchCase = ctx.exprCaseClause(i);
			Pair<Statement, Statement> caseBlock = visitStatementList(switchCase.statementList());
			Expression caseBooleanGuard = null;

			// Check if the switch case is not the default case
			if (switchCase.exprSwitchCase().expressionList() != null) {
				Expression[] expsCase = visitExpressionList(switchCase.exprSwitchCase().expressionList());
				for (int j = 0; j < expsCase.length; j++) 
					if (caseBooleanGuard == null)
						caseBooleanGuard = new GoEqual(cfg, (SourceCodeLocation) expsCase[j].getLocation(), expsCase[j], switchGuard);
					else
						caseBooleanGuard = new GoLogicalOr(cfg, (SourceCodeLocation) expsCase[j].getLocation(), caseBooleanGuard, new GoEqual(cfg, (SourceCodeLocation) expsCase[j].getLocation(), expsCase[j], switchGuard));

				cfg.addNode(caseBooleanGuard);
				addEdge(new TrueEdge(caseBooleanGuard, caseBlock.getLeft()));

				if (!(caseBlock.getRight() instanceof GoFallThrough))
					addEdge(new SequentialEdge(caseBlock.getRight(), exitNode));

				if (lastCaseBlock != null)
					addEdge(new SequentialEdge(lastCaseBlock.getRight(), caseBlock.getLeft()));

				if (entryNode == null) {
					entryNode = caseBooleanGuard;
				} else {
					addEdge(new FalseEdge(previousGuard, caseBooleanGuard));
				}
				previousGuard = caseBooleanGuard;
			} else {				
				defaultBlock = caseBlock;
			}

			if (caseBlock.getRight() instanceof GoFallThrough)
				lastCaseBlock = caseBlock;
			else
				lastCaseBlock = null;
		}

		if (lastCaseBlock != null)
			addEdge(new SequentialEdge(lastCaseBlock.getRight(), exitNode));

		if (defaultBlock != null) {
			addEdge(new FalseEdge(previousGuard, defaultBlock.getRight()));
			addEdge(new SequentialEdge(defaultBlock.getLeft(), exitNode));
		} else {
			addEdge(new FalseEdge(previousGuard, exitNode));
		}

		if (ctx.simpleStmt() != null) {
			Pair<Statement, Statement> simpleStmt = visitSimpleStmt(ctx.simpleStmt());
			addEdge(new SequentialEdge(simpleStmt.getRight(), entryNode));
			entryNode = simpleStmt.getLeft();
		}

		return Pair.of(entryNode, exitNode);
	}

	@Override
	public Pair<Statement, Statement> visitForStmt(ForStmtContext ctx) {
		SourceCodeLocation location = locationOf(ctx);
		NoOp exitNode = new NoOp(cfg, location);
		cfg.addNode(exitNode);
		exitPoints.add(exitNode);

		if (ctx.forClause() != null) {
			boolean hasInitStmt = ctx.forClause().init != null;
			boolean hasCondition = ctx.forClause().guard != null;
			boolean hasPostStmt = ctx.forClause().inc != null;

			// Checking if initialization is missing
			Pair<Statement, Statement> init = null;
			Statement entryNode = null;
			if (hasInitStmt) {
				init = visitSimpleStmt(ctx.forClause().simpleStmt(0));
				cfg.addNode(entryNode = init.getLeft());
			}

			// Checking if condition is missing: if so, true is the boolean guard
			Statement cond = null;
			if (hasCondition) 
				cond = visitExpression(ctx.forClause().expression());
			else 
				cond = new GoBoolean(cfg, location, true);
			cfg.addNode(cond);
			entryPoints.add(cond);

			// Checking if post statement is missing
			Pair<Statement, Statement> post = null;
			if (hasPostStmt) {
				post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
				cfg.addNode(post.getLeft());
			}

			Pair<Statement, Statement> block;
			if (ctx.block() == null || ctx.block().statementList() == null) {
				NoOp emptyBlock = new NoOp(cfg, location);
				cfg.addNode(emptyBlock);
				block = Pair.of(emptyBlock, emptyBlock);
			} else
				block = visitBlock(ctx.block());

			Statement exitNodeBlock = block.getRight();
			Statement entryNodeOfBlock = block.getLeft();

			addEdge(new TrueEdge(cond, entryNodeOfBlock));
			addEdge(new FalseEdge(cond, exitNode));

			if (hasInitStmt) 
				addEdge(new SequentialEdge(init.getRight(), cond));
			else
				entryNode = cond; 

			if (hasPostStmt) {
				addEdge(new SequentialEdge(exitNodeBlock, post.getRight()));
				addEdge(new SequentialEdge(post.getLeft(), cond));
			} else 
				addEdge(new SequentialEdge(exitNodeBlock, cond));

			entryPoints.remove(entryPoints.size()-1);
			exitPoints.remove(exitPoints.size()-1);
			return Pair.of(entryNode, exitNode);
		}

		if (ctx.rangeClause() != null) {
			// TODO: SUPER UNSOUND
			NoOp entry = new NoOp(cfg, location);
			cfg.addNode(entry);
			entryPoints.add(entry);

			Pair<Statement, Statement> block = visitBlock(ctx.block());
			addEdge(new SequentialEdge(entry, block.getLeft()));
			addEdge(new SequentialEdge(block.getLeft(), exitNode));

			entryPoints.remove(entryPoints.size()-1);
			exitPoints.remove(exitPoints.size()-1);
			return Pair.of(entry, exitNode);
		}

		// for i < n (corresponding to the classical while loop)
		if (ctx.expression() != null) {
			Expression guard = visitExpression(ctx.expression());
			cfg.addNode(guard);

			entryPoints.add(guard);

			Pair<Statement, Statement> block = visitBlock(ctx.block());
			addEdge(new SequentialEdge(guard, block.getLeft()));
			addEdge(new SequentialEdge(guard, exitNode));
			addEdge(new SequentialEdge(block.getRight(), guard));

			entryPoints.remove(entryPoints.size()-1);
			exitPoints.remove(exitPoints.size()-1);
			return Pair.of(guard, exitNode);		
		}

		// for { }
		NoOp entry = new NoOp(cfg, location);
		cfg.addNode(entry);
		entryPoints.add(entry);

		Pair<Statement, Statement> block = visitBlock(ctx.block());
		addEdge(new SequentialEdge(block.getRight(), block.getLeft()));
		addEdge(new SequentialEdge(entry, block.getLeft()));
		addEdge(new SequentialEdge(block.getLeft(), exitNode));

		entryPoints.remove(entryPoints.size()-1);
		exitPoints.remove(exitPoints.size()-1);
		return Pair.of(entry, exitNode);
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
			return GoInterfaceType.getEmptyInterface();
		else 
			for (MethodSpecContext methSpec : ctx.methodSpec())
				currentUnit.addCFG(new CFG(visitMethodSpec(methSpec)));

		return GoInterfaceType.lookup(currentUnit.getName(), currentUnit);
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
		else if (getCol(ctx.CHAN()) < getCol(ctx.RECEIVE()))
			return GoChannelType.lookup(new GoChannelType(contentType, true, false));

		return GoChannelType.lookup(new GoChannelType(contentType, false, true));
	}

	@Override
	public CFGDescriptor visitMethodSpec(MethodSpecContext ctx) {
		if (ctx.typeName() == null) {
			Type returnType = ctx.result() == null? Untyped.INSTANCE : visitResult(ctx.result());
			String name = ctx.IDENTIFIER().getText();
			Parameter[] params = visitParameters(ctx.parameters());
			//			return new GoMethodSpecification(name, returnType, params);

			return new CFGDescriptor(locationOf(ctx), currentUnit, false, name, returnType, params);
		} 

		throw new UnsupportedOperationException("Method specification not supported yet:  " + ctx.getText());
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
					return new GoNew(cfg, file, getLine(ctx.primaryExpr()), getCol(ctx.primaryExpr()), visitType_(ctx.arguments().type_()));
				else {
					// TODO: this is a workaround...
					return new GoNew(cfg, file, getLine(ctx.primaryExpr()), getCol(ctx.primaryExpr()), parseType(ctx.arguments().expressionList().getText()));
				}
			}

			else if (ctx.primaryExpr().getText().equals("len")) {
				Expression[] args = visitArguments(ctx.arguments());
				return new GoLength(cfg, locationOf(ctx.primaryExpr()), args[0]);
			}

			Expression primary = visitPrimaryExpr(ctx.primaryExpr());

			// Function/method call (e.g., f(1,2,3), x.f())
			// TODO: need to check if it is instance or not
			if (ctx.arguments() != null) 
				return new UnresolvedCall(cfg, locationOf(ctx), GoFrontEnd.CALL_STRATEGY, false, primary.toString(), visitArguments(ctx.arguments()));

			// Array/slice/map access e1[e2]
			else if (ctx.index() != null) {
				Expression index = visitIndex(ctx.index());
				return new GoCollectionAccess(cfg, locationOf(ctx), primary, index);
			}

			// Field access x.f
			else if (ctx.IDENTIFIER() != null) {
				Global index = new Global(locationOf(ctx.IDENTIFIER()), ctx.IDENTIFIER().getText(), Untyped.INSTANCE);
				return new AccessUnitGlobal(cfg, locationOf(ctx), primary, index);
			}

			// Simple slice expression a[l:h]
			else if (ctx.slice() != null) {
				Pair<Expression, Expression> args = visitSlice(ctx.slice());

				if (args.getRight() == null)
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(), new GoLength(cfg, locationOf(ctx), primary));
				else
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(), args.getRight());
			}
			
			else if (ctx.typeAssertion() != null) {
				return new GoTypeAssertion(cfg, primary, visitType_(ctx.typeAssertion().type_()));
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
		SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));

		Expression exp = visitExpression(ctx.expression());
		if (ctx.PLUS() != null)
			return new GoPlus(cfg, location, exp);

		if (ctx.MINUS() != null)
			return new GoMinus(cfg, location, exp);

		if (ctx.EXCLAMATION() != null)
			return new GoNot(cfg, location, exp);

		if (ctx.STAR() != null)
			return new GoRef(cfg, location, exp);

		if (ctx.AMPERSAND() != null)
			return new GoDeref(cfg, location, exp);

		if (ctx.CARET() != null)
			return new GoBitwiseNot(cfg, location, exp);

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitConversion(ConversionContext ctx) {
		Type type = visitType_(ctx.type_());
		Expression exp = visitExpression(ctx.expression());
		return new GoTypeConversion(cfg, type, exp);
	}

	@Override
	public Expression visitOperand(OperandContext ctx) {
		if (ctx.expression() != null)
			return visitExpression(ctx.expression());

		if (ctx.literal() != null)
			return visitLiteral(ctx.literal());

		if (ctx.operandName() != null)
			return visitOperandName(ctx.operandName());

		if (ctx.methodExpr() != null)
			return visitMethodExpr(ctx.methodExpr());

		throw new IllegalStateException("Illegal state: operand rule has no other productions.");
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

		SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
		// Go nil value
		if (ctx.NIL_LIT() != null)
			return new GoNil(cfg, location);

		// Go float value
		if (ctx.FLOAT_LIT() != null) 
			return new GoFloat(cfg, location, Double.parseDouble(ctx.FLOAT_LIT().getText()));

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Expression visitInteger(IntegerContext ctx) {
		SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
		
		//TODO: for the moment, we skip any other integer literal format (e.g., octal, imaginary)
		if (ctx.DECIMAL_LIT() != null)
			return new GoInteger(cfg, location, Integer.parseInt(ctx.DECIMAL_LIT().getText()));

		// TODO: 0 matched as octacl literal and not decimal literal
		if (ctx.OCTAL_LIT() != null)
			return new GoInteger(cfg, location, Integer.parseInt(ctx.OCTAL_LIT().getText()));

		if (ctx.RUNE_LIT() != null) 
			return new GoRune(cfg, location, removeQuotes(ctx.getText()));

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());	
	}

	@Override
	public Expression visitOperandName(OperandNameContext ctx) {
		// TODO: qualified identifier not handled
		if (ctx.IDENTIFIER() != null) {
			SourceCodeLocation location = new SourceCodeLocation(file, getLine(ctx), getCol(ctx));

			// Boolean values (true, false) are matched as identifiers
			if (ctx.IDENTIFIER().getText().equals("true") || ctx.IDENTIFIER().getText().equals("false"))
				return new GoBoolean(cfg, location, Boolean.parseBoolean(ctx.IDENTIFIER().getText()));
			else
				return new VariableRef(cfg, location, ctx.IDENTIFIER().getText());
		}

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Statement visitCompositeLit(CompositeLitContext ctx) {

		GoType type = visitLiteralType(ctx.literalType());
		Object raw = visitLiteralValue(ctx.literalValue());

		if (raw instanceof Map<?, ?>)
			return new GoKeyedLiteral(cfg, file, getLine(ctx), getCol(ctx), (Map<Expression, Expression>) raw,  type);
		else {
			Expression[] exps = new Expression[((List<Expression>) raw).size()];
			exps = ((List<Expression>) raw).toArray(exps);
			return new GoNonKeyedLiteral(cfg, file, getLine(ctx), getCol(ctx), exps, type);
		}
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
			return new VariableRef(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)), ctx.IDENTIFIER().getText());

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
		for (FieldDeclContext field : ctx.fieldDecl()) 
			for (Pair<String, Type> fd : visitFieldDecl(field))
				currentUnit.addInstanceGlobal(new Global(new SourceCodeLocation(file, getLine(field), getCol(field)), fd.getLeft(), fd.getRight()));

		return GoStructType.lookup(currentUnit.getName(), currentUnit);
	}

	@Override
	public List<Pair<String, Type>> visitFieldDecl(FieldDeclContext ctx) {
		List<Pair<String, Type>> result = new ArrayList<>();

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
		SourceCodeLocation location = locationOf(ctx);
		if (ctx.RAW_STRING_LIT() != null)
			return new GoString(cfg, location, removeQuotes(ctx.RAW_STRING_LIT().getText()));

		if (ctx.INTERPRETED_STRING_LIT() != null)
			return new GoString(cfg, location, removeQuotes(ctx.INTERPRETED_STRING_LIT().getText()));

		throw new IllegalStateException("Illegal state: string rule has no other productions.");
	}


	@Override
	public GoType visitArrayType(ArrayTypeContext ctx) {
		GoType contentType = visitElementType(ctx.elementType());
		Integer length = visitArrayLength(ctx.arrayLength());
		return GoArrayType.lookup(new GoArrayType(contentType, length));
	}

	@Override
	public Integer visitArrayLength(ArrayLengthContext ctx) {
		// The array length must be an integer value, not required to be an expression.
		try {
			return Integer.parseInt(ctx.expression().getText());
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Go error: non-constant array bound " + ctx.getText());
		}
	}
	
	@Override
	public Pair<String, Type> visitAnonymousField(AnonymousFieldContext ctx) {
		Type type = visitTypeName(ctx.typeName());
		if (ctx.STAR() != null)
			type = new GoPointerType(type);
		return Pair.of(ctx.typeName().getText(), type);
	}

	@Override
	public Expression visitIndex(IndexContext ctx) {
		return visitExpression(ctx.expression());
	}

	@Override
	public Pair<Expression, Expression> visitSlice(SliceContext ctx) {
		SourceCodeLocation location = locationOf(ctx);
		// [:]
		if (ctx.expression(0) == null) 
			return Pair.of(new GoInteger(cfg, location, 0), null);
	
		// [n:] or [:n]
		if (ctx.expression(1) == null) {
			Expression n = visitExpression(ctx.expression(0));

			if (getCol(ctx.expression(0)) < getCol(ctx.COLON(0)))
				return Pair.of(n, null);
			else
				return Pair.of(new GoInteger(cfg, location, 0), n);

		} 

		return Pair.of(visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
	}

	@Override
	public Statement visitTypeAssertion(TypeAssertionContext ctx) {
		// This method should never be visited
		throw new UnsupportedOperationException("Type assertion should never be visited");
	}

	@Override
	public Expression[] visitArguments(ArgumentsContext ctx) {
		Expression[] exps = new Expression[]{};
		if (ctx.expressionList() != null)
			for (int i = 0; i < ctx.expressionList().expression().size(); i++)
				exps = ArrayUtils.addAll(exps, visitExpression(ctx.expressionList().expression(i)));
		return exps;
	}
	
	private String removeQuotes(String str) {
		return str.substring(1, str.length() -1);
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
	

	@Override
	public Statement visitSendStmt(SendStmtContext ctx) {
		// TODO: send statement
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitExprCaseClause(ExprCaseClauseContext ctx) {
		throw new IllegalStateException("exprCaseClause should never be visited.");
	}

	@Override
	public Statement visitExprSwitchCase(ExprSwitchCaseContext ctx) {
		throw new IllegalStateException("exprSwitchCase should never be visited.");
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
	public Pair<Statement, Statement> visitSelectStmt(SelectStmtContext ctx) {
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
	public Statement visitForClause(ForClauseContext ctx) {
		// TODO for clause
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitLabeledStmt(LabeledStmtContext ctx) {
		// TODO: labeled statements
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}
	
	@Override
	public Statement visitRangeClause(RangeClauseContext ctx) {
		// TODO range clause
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitGoStmt(GoStmtContext ctx) {
		// TODO go stmt
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitSignature(SignatureContext ctx) {
		// This method shold never be visited
		throw new IllegalStateException("Signature should never be visited");
	}

	@Override
	public Expression visitMethodExpr(MethodExprContext ctx) {
		// TODO: method expression
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO: receiver type
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}
	
	@Override
	public Expression visitFunctionLit(FunctionLitContext ctx) {
		// TODO: function literal
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}
	
	@Override
	public Statement visitEos(EosContext ctx) {
		// This method should never be visited
		throw new UnsupportedOperationException("Eos should never be visited");
	}
}
