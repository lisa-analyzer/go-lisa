package it.unive.golisa.frontend;

import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParser.ArgumentsContext;
import it.unive.golisa.antlr.GoParser.Assign_opContext;
import it.unive.golisa.antlr.GoParser.AssignmentContext;
import it.unive.golisa.antlr.GoParser.BasicLitContext;
import it.unive.golisa.antlr.GoParser.BlockContext;
import it.unive.golisa.antlr.GoParser.BreakStmtContext;
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
import it.unive.golisa.antlr.GoParser.EmptyStmtContext;
import it.unive.golisa.antlr.GoParser.EosContext;
import it.unive.golisa.antlr.GoParser.ExprCaseClauseContext;
import it.unive.golisa.antlr.GoParser.ExprSwitchCaseContext;
import it.unive.golisa.antlr.GoParser.ExprSwitchStmtContext;
import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.ExpressionListContext;
import it.unive.golisa.antlr.GoParser.ExpressionStmtContext;
import it.unive.golisa.antlr.GoParser.FallthroughStmtContext;
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
import it.unive.golisa.antlr.GoParser.KeyContext;
import it.unive.golisa.antlr.GoParser.KeyedElementContext;
import it.unive.golisa.antlr.GoParser.LabeledStmtContext;
import it.unive.golisa.antlr.GoParser.LiteralContext;
import it.unive.golisa.antlr.GoParser.LiteralValueContext;
import it.unive.golisa.antlr.GoParser.MethodDeclContext;
import it.unive.golisa.antlr.GoParser.MethodExprContext;
import it.unive.golisa.antlr.GoParser.MethodSpecContext;
import it.unive.golisa.antlr.GoParser.OperandContext;
import it.unive.golisa.antlr.GoParser.OperandNameContext;
import it.unive.golisa.antlr.GoParser.ParameterDeclContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.PrimaryExprContext;
import it.unive.golisa.antlr.GoParser.QualifiedIdentContext;
import it.unive.golisa.antlr.GoParser.RangeClauseContext;
import it.unive.golisa.antlr.GoParser.ReceiverContext;
import it.unive.golisa.antlr.GoParser.RecvStmtContext;
import it.unive.golisa.antlr.GoParser.ResultContext;
import it.unive.golisa.antlr.GoParser.ReturnStmtContext;
import it.unive.golisa.antlr.GoParser.SelectStmtContext;
import it.unive.golisa.antlr.GoParser.SendStmtContext;
import it.unive.golisa.antlr.GoParser.ShortVarDeclContext;
import it.unive.golisa.antlr.GoParser.SignatureContext;
import it.unive.golisa.antlr.GoParser.SimpleStmtContext;
import it.unive.golisa.antlr.GoParser.SliceContext;
import it.unive.golisa.antlr.GoParser.StatementContext;
import it.unive.golisa.antlr.GoParser.StatementListContext;
import it.unive.golisa.antlr.GoParser.String_Context;
import it.unive.golisa.antlr.GoParser.SwitchStmtContext;
import it.unive.golisa.antlr.GoParser.TypeAssertionContext;
import it.unive.golisa.antlr.GoParser.TypeCaseClauseContext;
import it.unive.golisa.antlr.GoParser.TypeDeclContext;
import it.unive.golisa.antlr.GoParser.TypeListContext;
import it.unive.golisa.antlr.GoParser.TypeSpecContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchCaseContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchGuardContext;
import it.unive.golisa.antlr.GoParser.TypeSwitchStmtContext;
import it.unive.golisa.antlr.GoParser.Type_Context;
import it.unive.golisa.antlr.GoParser.UnaryExprContext;
import it.unive.golisa.antlr.GoParser.VarDeclContext;
import it.unive.golisa.antlr.GoParser.VarSpecContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.expression.GoMake;
import it.unive.golisa.cfg.expression.GoNew;
import it.unive.golisa.cfg.expression.GoNotEqual;
import it.unive.golisa.cfg.expression.GoTypeConversion;
import it.unive.golisa.cfg.expression.binary.GoBitwiseAnd;
import it.unive.golisa.cfg.expression.binary.GoBitwiseNAnd;
import it.unive.golisa.cfg.expression.binary.GoBitwiseOr;
import it.unive.golisa.cfg.expression.binary.GoBitwiseXOr;
import it.unive.golisa.cfg.expression.binary.GoChannelSend;
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
import it.unive.golisa.cfg.expression.literal.GoExpressionsTuple;
import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.expression.literal.GoFunctionLiteral;
import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.expression.literal.GoKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoRune;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.golisa.cfg.expression.ternary.GoSimpleSlice;
import it.unive.golisa.cfg.expression.unary.GoBitwiseNot;
import it.unive.golisa.cfg.expression.unary.GoChannelReceive;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.expression.unary.GoLength;
import it.unive.golisa.cfg.expression.unary.GoMinus;
import it.unive.golisa.cfg.expression.unary.GoNot;
import it.unive.golisa.cfg.expression.unary.GoPlus;
import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.expression.unary.GoRef;
import it.unive.golisa.cfg.expression.unknown.GoUnknown;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.statement.GoFallThrough;
import it.unive.golisa.cfg.statement.GoReturn;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.cfg.statement.GoTo;
import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoConstantDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.golisa.cfg.statement.assignment.GoMultiShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.BlockInfo.DeclarationType;
import it.unive.golisa.cfg.statement.block.CloseBlock;
import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.composite.GoVariadicType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.SyntheticLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.FalseEdge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.edge.TrueEdge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.global.AccessInstanceGlobal;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A {@link GoParserBaseVisitor} that will parse the code of an Go method
 */
public class GoCodeMemberVisitor extends GoParserBaseVisitor<Object> {

	protected final String file;

	protected final Collection<Statement> entrypoints;

	protected final Collection<ControlFlowStructure> cfs;

	private final Map<String, Set<IdInfo>> visibleIds;

	protected final Map<Statement, String> gotos;

	protected final Map<String, Statement> labeledStmt;

	protected VariableScopingCFG cfg;

	protected CFGDescriptor descriptor;

	protected final Program program;

	protected static int c = 0;

	protected int blockDeep;

	/**
	 * Stack of loop exit points (used for break statements)
	 */
	private List<Statement> exitPoints = new ArrayList<Statement>();

	/**
	 * Stack of loop entry points (used for continues statements)
	 */
	private List<Statement> entryPoints = new ArrayList<Statement>();

	protected final Map<String, ExpressionContext> constants;

	/**
	 * Current compilation unit to parse
	 */
	protected CompilationUnit currentUnit;

	public GoCodeMemberVisitor(CompilationUnit unit, String file, Program program,
			Map<String, ExpressionContext> constants) {
		this.file = file;
		this.program = program;
		entrypoints = new HashSet<>();
		cfs = new LinkedList<>();
		visibleIds = new HashMap<>();
		this.blockDeep = 0;
		this.constants = constants;
		this.currentUnit = unit;
		this.gotos = new HashMap<>();
		this.labeledStmt = new HashMap<>();
		this.currentUnit = unit;
	}

	public GoCodeMemberVisitor(CompilationUnit packageUnit, MethodDeclContext ctx, String file, Program program,
			Map<String, ExpressionContext> constants) {
		this.file = file;
		this.program = program;
		this.descriptor = mkDescriptor(packageUnit, ctx);
		this.constants = constants;

		gotos = new HashMap<>();
		labeledStmt = new HashMap<>();
		entrypoints = new HashSet<>();
		cfs = new LinkedList<>();
		// side effects on entrypoints and matrix will affect the cfg
		cfg = new VariableScopingCFG(descriptor, entrypoints, new AdjacencyMatrix<>());

		visibleIds = new HashMap<>();
		this.blockDeep = 0;

		initializeVisibleIds();
	}

	protected void initializeVisibleIds() {
		for (VariableTableEntry par : descriptor.getVariables())
			if (!GoLangUtils.refersToBlankIdentifier(par.createReference(cfg))) {
				visibleIds.putIfAbsent(par.getName(), new HashSet<IdInfo>());
				visibleIds.get(par.getName()).add(new IdInfo(par.createReference(cfg), blockDeep));
			}

	}

	private CFGDescriptor mkDescriptor(CompilationUnit packageUnit, MethodDeclContext ctx) {
		return new CFGDescriptor(locationOf(ctx), packageUnit, false, ctx.IDENTIFIER().getText(),
				visitParameters(ctx.signature().parameters()));
	}

	/**
	 * Visits the code of a {@link BlockContext} representing the code block of
	 * a method or constructor.
	 * 
	 * @param ctx the block context
	 * 
	 * @return the {@link CFG} built from the block
	 */
	public CFG visitCodeMember(MethodDeclContext ctx) {
		Parameter receiver = visitReceiver(ctx.receiver());
		String unitName = receiver.getStaticType() instanceof GoPointerType
				? ((GoPointerType) receiver.getStaticType()).getInnerTypes().first().toString()
				: receiver.getStaticType().toString();

		SourceCodeLocation location = locationOf(ctx);
		if (program.getUnit(unitName) == null)
			// TODO: unknown unit
			currentUnit = new CompilationUnit(location, unitName, false);
		else
			currentUnit = program.getUnit(unitName);

		String methodName = ctx.IDENTIFIER().getText();
		Parameter[] params = visitParameters(ctx.signature().parameters());
		params = ArrayUtils.insert(0, params, receiver);
		Type returnType = ctx.signature().result() == null ? Untyped.INSTANCE : visitResult(ctx.signature().result());

		if (returnType == null)
			returnType = Untyped.INSTANCE;

		cfg = new VariableScopingCFG(new CFGDescriptor(location, currentUnit, true, methodName, returnType, params));

		Pair<Statement, Statement> body = visitMethodBlock(ctx.block());

		for (Entry<Statement, String> gotoStmt : gotos.entrySet())
			// we must call cfg.addEdge, and not addEdge
			cfg.addEdge(new SequentialEdge(gotoStmt.getKey(), labeledStmt.get(gotoStmt.getValue())));

		cfg.getEntrypoints().add(body.getLeft());

		// If the method body does not have exit points
		// a return statement is added
		AdjacencyMatrix<Statement, Edge, CFG> matrix = cfg.getAdjacencyMatrix();
		if (cfg.getAllExitpoints().isEmpty()) {
			Ret ret = new Ret(cfg, descriptor.getLocation());
			if (cfg.getNodesCount() == 0) {
				// empty method, so the ret is also the entrypoint
				matrix.addNode(ret);
				entrypoints.add(ret);
			} else {
				// every non-throwing instruction that does not have a follower
				// is ending the method
				Collection<Statement> preExits = new LinkedList<>();
				for (Statement st : matrix.getNodes())
					if (!st.stopsExecution() && matrix.followersOf(st).isEmpty())
						preExits.add(st);
				matrix.addNode(ret);
				for (Statement st : preExits)
					matrix.addEdge(new SequentialEdge(st, ret));

				for (VariableTableEntry entry : descriptor.getVariables())
					if (preExits.contains(entry.getScopeEnd()))
						entry.setScopeEnd(ret);
			}
		}

		for (Statement st : matrix.getExits())
			if (st instanceof NoOp && !matrix.getIngoingEdges(st).isEmpty()) {
				Ret ret = new Ret(cfg, descriptor.getLocation());
				if (!st.stopsExecution() && matrix.followersOf(st).isEmpty())
					matrix.addNode(ret);
				matrix.addEdge(new SequentialEdge(st, ret));
			}

		cfg.simplify();

		currentUnit.addInstanceCFG(cfg);
		return cfg;
	}

	protected Pair<Statement, Statement> visitMethodBlock(BlockContext ctx) {
		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		if (ctx.statementList() == null) {
			NoOp noop = new NoOp(cfg, locationOf(ctx.R_CURLY()));
			cfg.addNode(noop);
			updateVisileIds(backup, noop);
			return Pair.of(noop, noop);
		}

		// we build the block information to the block list
		// but it is not added as node being a method block
		OpenBlock open = new OpenBlock(cfg, locationOf(ctx.L_CURLY()));
		blockList.addLast(new BlockInfo(open));

		Pair<Statement, Statement> res = visitStatementList(ctx.statementList());
		updateVisileIds(backup, res.getRight());
		return res;
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
	public Parameter visitReceiver(ReceiverContext ctx) {
		Parameter[] params = visitParameters(ctx.parameters());
		if (params.length != 1)
			throw new IllegalStateException("Go receiver must be a single parameter");

		return params[0];
	}

	@Override
	public Parameter[] visitParameters(ParametersContext ctx) {
		Parameter[] result = new Parameter[] {};
		for (int i = 0; i < ctx.parameterDecl().size(); i++)
			result = ArrayUtils.addAll(result, visitParameterDecl(ctx.parameterDecl(i)));

		return result;
	}

	@Override
	public Parameter[] visitParameterDecl(ParameterDeclContext ctx) {
		Parameter[] result = new Parameter[] {};
		Type type = visitType_(ctx.type_());
		type = type == null ? Untyped.INSTANCE : type;

		// the parameter's type is variadic (e.g., ...string)
		if (ctx.ELLIPSIS() != null)
			type = GoVariadicType.lookup(new GoVariadicType(type));

		if (ctx.identifierList() == null)
			result = ArrayUtils.add(result, new Parameter(locationOf(ctx), "_", type));
		else
			for (int i = 0; i < ctx.identifierList().IDENTIFIER().size(); i++) {
				TerminalNode par = ctx.identifierList().IDENTIFIER(i);
				result = ArrayUtils.addAll(result, new Parameter(locationOf(par), par.getText(), type));
			}
		return result;
	}

	@Override
	public Pair<String, String> visitQualifiedIdent(QualifiedIdentContext ctx) {
		return Pair.of(ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
	}

	@Override
	public Type visitResult(ResultContext ctx) {
		if (ctx == null)
			return Untyped.INSTANCE;
		if (ctx.type_() != null)
			return visitType_(ctx.type_());
		else {
			return new GoTypesTuple(visitParameters(ctx.parameters()));
		}
	}

	LinkedList<BlockInfo> blockList = new LinkedList<>();

	@Override
	public Pair<Statement, Statement> visitBlock(BlockContext ctx) {
		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		if (ctx.statementList() == null) {
			NoOp noop = new NoOp(cfg, locationOf(ctx.R_CURLY()));
			cfg.addNode(noop);
			updateVisileIds(backup, noop);
			return Pair.of(noop, noop);
		}

		blockDeep++;

		OpenBlock open = new OpenBlock(cfg, locationOf(ctx.L_CURLY()));
		cfg.addNode(open);

		CloseBlock close = new CloseBlock(cfg, locationOf(ctx.R_CURLY()), open);

		blockList.addLast(new BlockInfo(open));

		Pair<Statement, Statement> res = visitStatementList(ctx.statementList());
		updateVisileIds(backup, res.getRight());
		if (isReturnStmt(res.getRight())) {
			addEdge(new SequentialEdge(open, res.getLeft()));
			return Pair.of(open, res.getRight());
		}

		cfg.addNode(close);
		addEdge(new SequentialEdge(res.getRight(), close));
		addEdge(new SequentialEdge(open, res.getLeft()));

		blockDeep--;

		blockList.removeLast();
		return Pair.of(open, close);
	}

	protected void updateVisileIds(Map<String, Set<IdInfo>> backup, Statement last) {

		Map<String, Set<IdInfo>> toRemove = new HashMap<>();
		for (Entry<String, Set<IdInfo>> id : visibleIds.entrySet())
			if (!backup.containsKey(id.getKey())) {
				for (IdInfo idInfo : id.getValue()) {
					VariableRef ref = idInfo.getRef();
					descriptor.addVariable(new VariableTableEntry(ref.getLocation(),
							0, ref.getRootStatement(), last, id.getKey(), Untyped.INSTANCE));
					toRemove.putIfAbsent(id.getKey(), new HashSet<IdInfo>());
					toRemove.get(id.getKey()).add(idInfo);
				}
			}

		if (!toRemove.isEmpty()) {
			for (String k : toRemove.keySet()) {
				if (visibleIds.containsKey(k)) {
					Set<IdInfo> visibleInfoSet = visibleIds.get(k);
					Set<IdInfo> removeInfoSet = toRemove.get(k);
					if (visibleInfoSet != null && removeInfoSet != null)
						visibleInfoSet.removeAll(removeInfoSet);

					if (visibleInfoSet == null || (visibleInfoSet != null && visibleInfoSet.isEmpty()))
						visibleIds.remove(k);
				}

			}

		}
	}

	@Override
	public Pair<Statement, Statement> visitStatementList(StatementListContext ctx) {
		// It is an empty statement
		if (ctx == null || ctx.statement().size() == 0) {
			NoOp nop = new NoOp(cfg, SyntheticLocation.INSTANCE);
			cfg.addNode(nop);
			return Pair.of(nop, nop);
		}

		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ctx.statement().size(); i++) {
			Pair<Statement, Statement> currentStmt = visitStatement(ctx.statement(i));

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		return Pair.of(entryNode, lastStmt);
	}

	public Pair<Statement, Statement> visitStatementListOfSwitchCase(StatementListContext ctx) {
		// It is an empty statement
		if (ctx == null || ctx.statement().size() == 0) {
			NoOp nop = new NoOp(cfg, SyntheticLocation.INSTANCE);
			cfg.addNode(nop);
			return Pair.of(nop, nop);
		}

		Statement lastStmt = null;
		Statement entryNode = null;

		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);

		for (int i = 0; i < ctx.statement().size(); i++) {
			Pair<Statement, Statement> currentStmt = visitStatement(ctx.statement(i));

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()));
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();

			// scoping must be updated for each case
			updateVisileIds(backup, lastStmt);
		}

		return Pair.of(entryNode, lastStmt);
	}

	@Override
	public Pair<Statement, Statement> visitStatement(StatementContext ctx) {
		if (ctx.declaration() != null)
			return visitDeclaration(ctx.declaration());
		if (ctx.labeledStmt() != null)
			return visitLabeledStmt(ctx.labeledStmt());
		if (ctx.simpleStmt() != null)
			return visitSimpleStmt(ctx.simpleStmt());
		if (ctx.goStmt() != null)
			return visitGoStmt(ctx.goStmt());
		if (ctx.returnStmt() != null)
			return visitReturnStmt(ctx.returnStmt());
		if (ctx.breakStmt() != null)
			return visitBreakStmt(ctx.breakStmt());
		if (ctx.continueStmt() != null)
			return visitContinueStmt(ctx.continueStmt());
		if (ctx.gotoStmt() != null)
			return visitGotoStmt(ctx.gotoStmt());
		if (ctx.fallthroughStmt() != null)
			return visitFallthroughStmt(ctx.fallthroughStmt());
		if (ctx.block() != null)
			return visitBlock(ctx.block());
		if (ctx.ifStmt() != null)
			return visitIfStmt(ctx.ifStmt());
		if (ctx.switchStmt() != null)
			return visitSwitchStmt(ctx.switchStmt());
		if (ctx.selectStmt() != null)
			return visitSelectStmt(ctx.selectStmt());
		if (ctx.forStmt() != null)
			return visitForStmt(ctx.forStmt());
		if (ctx.deferStmt() != null)
			return visitDeferStmt(ctx.deferStmt());

		throw new IllegalStateException("Illegal state: statement rule has no other productions.");
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

		// Number of identifiers to be assigned
		int sizeIds = ids.IDENTIFIER().size();
		// Number of expressions to be assigned
		int sizeExps = exps == null ? 0 : exps.expression().size();

		// Multi variable declaration
		if (sizeIds != sizeExps && sizeExps != 0) {
			int line = getLine(ctx);
			int col = getCol(ctx);

			VariableRef[] left = visitIdentifierList(ctx.identifierList());

			for (int i = 0; i < left.length; i++)
				if (visibleIds.containsKey(left[i].getName()))
					throw new GoSyntaxException(
							"Duplicate variable '" + left[i].getName() + "' declared at " + left[i].getLocation());
				else if (!GoLangUtils.refersToBlankIdentifier(left[i])) {
					visibleIds.putIfAbsent(left[i].getName(), new HashSet<IdInfo>());
					visibleIds.get(left[i].getName()).add(new IdInfo(left[i], blockDeep));
					blockList.getLast().addVarDeclaration(left[i], DeclarationType.MULTI_SHORT_VARIABLE);
				}

			Expression right = visitExpression(exps.expression(0));

			// We can safely reause the multi-short variable declaration class
			GoMultiShortVariableDeclaration asg = new GoMultiShortVariableDeclaration(cfg, file, line, col, left,
					right, blockList,
					getContainingBlock());
			cfg.addNode(asg, visibleIds);

			return Pair.of(asg, asg);
		} else {

			Statement lastStmt = null;
			Statement entryNode = null;
			Type_Context typeContext = ctx.type_();

			Type type = typeContext == null ? Untyped.INSTANCE : visitType_(typeContext);
			type = type == null ? Untyped.INSTANCE : type;
			type = type.isInMemoryType() ? new ReferenceType(type) : type;

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp;
				if (type.isUntyped())
					exp = new GoUnknown(cfg, locationOf(ctx));
				else {

					if ((exps == null || exps.expression(i) == null) && !type.isUntyped()) {
						if (type instanceof ReferenceType)
							exp = ((GoType) ((ReferenceType) type).getInnerTypes().first()).defaultValue(cfg,
									locationOf(ctx));
						else
							exp = ((GoType) type).defaultValue(cfg, locationOf(ctx));
					} else
						exp = visitExpression(exps.expression(i));
				}

				int line = getLine(ids.IDENTIFIER(i));
				int col = (exps == null || exps.expression(i) == null) ? getCol(ids.IDENTIFIER(i))
						: getCol(exps.expression(i));

				VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(),
						type);
				GoVariableDeclaration asg = new GoVariableDeclaration(cfg, new SourceCodeLocation(file, line, col),
						type, target, exp);
				cfg.addNode(asg, visibleIds);

				if (visibleIds.containsKey(target.getName()))
					if (visibleIds.get(target.getName()).stream()
							.anyMatch(info -> info.equals(new IdInfo(target, blockDeep))))
						throw new GoSyntaxException(
								"Duplicate variable '" + target.getName() + "' declared at " + target.getLocation());

				if (!GoLangUtils.refersToBlankIdentifier(target)) {
					visibleIds.putIfAbsent(target.getName(), new HashSet<IdInfo>());
					visibleIds.get(target.getName()).add(new IdInfo(target, blockDeep));
					blockList.getLast().addVarDeclaration(target, DeclarationType.VARIABLE);
				}

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
			return new GoSubtraction(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go and (&&)
		if (ctx.LOGICAL_AND() != null)
			return new GoLogicalAnd(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go and (||)
		if (ctx.LOGICAL_OR() != null)
			return new GoLogicalOr(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go equal (==)
		if (ctx.EQUALS() != null)
			return new GoEqual(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go not equal (!=)
		if (ctx.NOT_EQUALS() != null)
			return new GoNotEqual(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go less (<)
		if (ctx.LESS() != null)
			return new GoLess(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater (>)
		if (ctx.GREATER() != null)
			return new GoGreater(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go greater or equals (>=)
		if (ctx.GREATER_OR_EQUALS() != null)
			return new GoGreaterOrEqual(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go less or equals (>=)
		if (ctx.LESS_OR_EQUALS() != null)
			return new GoLessOrEqual(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go module (%)
		if (ctx.MOD() != null)
			return new GoModule(cfg, location, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));

		// Go right shift (>>)
		if (ctx.RSHIFT() != null)
			return new GoRightShift(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go left shift (<<)
		if (ctx.LSHIFT() != null)
			return new GoLeftShift(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go XOR (^)
		if (ctx.CARET() != null)
			return new GoBitwiseXOr(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go or (|)
		if (ctx.OR() != null)
			return new GoBitwiseOr(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go and (&)
		if (ctx.AMPERSAND() != null)
			return new GoBitwiseAnd(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		// Go nand (&^)
		if (ctx.BIT_CLEAR() != null)
			return new GoBitwiseNAnd(cfg, location, visitExpression(ctx.expression(0)),
					visitExpression(ctx.expression(1)));

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Pair<Statement, Statement> visitDeclaration(DeclarationContext ctx) {
		if (ctx.constDecl() != null)
			return visitConstDecl(ctx.constDecl());
		if (ctx.typeDecl() != null) {
			for (CompilationUnit unit : visitTypeDecl(ctx.typeDecl()))
				program.addCompilationUnit(unit);
			NoOp noop = new NoOp(cfg, locationOf(ctx.typeDecl().TYPE()));
			cfg.addNode(noop);
			return Pair.of(noop, noop);
		}

		if (ctx.varDecl() != null)
			return visitVarDecl(ctx.varDecl());

		throw new IllegalStateException("Illegal state: declaration rule has no other productions.");
	}

	@Override
	public Collection<CompilationUnit> visitTypeDecl(TypeDeclContext ctx) {
		HashSet<CompilationUnit> units = new HashSet<>();
		for (TypeSpecContext typeSpec : ctx.typeSpec()) {
			String unitName = typeSpec.IDENTIFIER().getText();
			CompilationUnit unit = new CompilationUnit(
					new SourceCodeLocation(file, getLine(typeSpec), getCol(typeSpec)), unitName, false);
			units.add(unit);
			new GoTypeVisitor(file, unit, program, constants).visitTypeSpec(typeSpec);
		}
		return units;
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
			cfg.addNode(asg, visibleIds);

			if (visibleIds.containsKey(target.getName()))
				if (visibleIds.get(target.getName()).stream()
						.anyMatch(info -> info.equals(new IdInfo(target, blockDeep))))
					throw new GoSyntaxException(
							"Duplicate variable '" + target.getName() + "' declared at " + target.getLocation());
			if (!GoLangUtils.refersToBlankIdentifier(target)) {
				visibleIds.putIfAbsent(target.getName(), new HashSet<IdInfo>());
				visibleIds.get(target.getName()).add(new IdInfo(target, blockDeep));
				blockList.getLast().addVarDeclaration(target, DeclarationType.CONSTANT);
			}

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
	public Pair<Statement, Statement> visitSimpleStmt(SimpleStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Pair<?, ?>))
			throw new IllegalStateException("Pair of Statements expected");
		else
			return (Pair<Statement, Statement>) result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Statement, Statement> visitExpressionStmt(ExpressionStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (result instanceof Expression) {
			cfg.addNode((Expression) result, visibleIds);
			return Pair.of((Expression) result, (Expression) result);
		} else if (!(result instanceof Pair<?, ?>)) {
			throw new IllegalStateException("Pair of Statements expected");
		} else
			return (Pair<Statement, Statement>) result;
	}

	@Override
	public Pair<Statement, Statement> visitIncDecStmt(IncDecStmtContext ctx) {

		Expression exp = visitExpression(ctx.expression());
		SourceCodeLocation location = locationOf(ctx.expression());
		Statement asg = null;

		// increment and decrement statements are syntactic sugar
		// e.g., x++ -> x = x + 1 and x-- -> x = x - 1
		if (ctx.PLUS_PLUS() != null)
			asg = new GoAssignment(cfg, location, exp,
					new GoSum(cfg, location, exp, new GoInteger(cfg, location, 1)), blockList, getContainingBlock());
		else
			asg = new GoAssignment(cfg, location, exp,
					new GoSubtraction(cfg, location, exp, new GoInteger(cfg, location, 1)), blockList,
					getContainingBlock());

		cfg.addNode(asg, visibleIds);
		return Pair.of(asg, asg);
	}

	private OpenBlock getContainingBlock() {
		return blockList.getLast().getOpen();
	}

	@Override
	public Pair<Statement, Statement> visitAssignment(AssignmentContext ctx) {

		ExpressionListContext ids = ctx.expressionList(0);
		ExpressionListContext exps = ctx.expressionList(1);

		// Number of identifiers to be assigned
		int sizeIds = ids.expression().size();
		// Number of expressions to be assigned
		int sizeExps = exps.expression().size();

		// Multi variable short declaration
		if (sizeIds != sizeExps) {
			int line = getLine(ctx);
			int col = getCol(ctx);

			Expression[] left = visitExpressionList(ids);
			Expression right = visitExpression(exps.expression(0));

			GoMultiAssignment asg = new GoMultiAssignment(cfg, file, line, col, left, right, blockList,
					getContainingBlock());
			cfg.addNode(asg, visibleIds);
			return Pair.of(asg, asg);
		} else {

			Statement lastStmt = null;
			Statement entryNode = null;

			for (int i = 0; i < ids.expression().size(); i++) {

				int line = getLine(ids.expression(i));
				int col = getCol(exps.expression(i));

				Expression lhs = visitExpression(ids.expression(i));
				Expression exp = buildExpressionFromAssignment(new SourceCodeLocation(file, line, col), lhs,
						ctx.assign_op(), visitExpression(exps.expression(i)));

				GoAssignment asg = new GoAssignment(cfg, locationOf(ctx), lhs, exp, blockList, getContainingBlock());
				cfg.addNode(asg, visibleIds);

				if (lastStmt != null)
					addEdge(new SequentialEdge(lastStmt, asg));
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Pair.of(entryNode, lastStmt);
		}
	}

	private Expression buildExpressionFromAssignment(SourceCodeLocation location, Expression lhs, Assign_opContext op,
			Expression exp) {

		// +=
		if (op.PLUS() != null)
			return new GoSum(cfg, location, lhs, exp);

		// -=
		if (op.MINUS() != null)
			return new GoSubtraction(cfg, location, lhs, exp);

		// *=
		if (op.STAR() != null)
			return new GoMul(cfg, location, lhs, exp);

		// /=
		if (op.DIV() != null)
			return new GoDiv(cfg, location, lhs, exp);

		// %=
		if (op.MOD() != null)
			return new GoModule(cfg, location, lhs, exp);

		// >>=
		if (op.RSHIFT() != null)
			return new GoRightShift(cfg, location, lhs, exp);

		// <<=
		if (op.LSHIFT() != null)
			return new GoLeftShift(cfg, location, lhs, exp);

		// &^=
		if (op.BIT_CLEAR() != null)
			return new GoBitwiseNAnd(cfg, location, lhs, exp);

		// ^=
		if (op.CARET() != null)
			return new GoBitwiseXOr(cfg, location, lhs, exp);

		// &=
		if (op.AMPERSAND() != null)
			return new GoBitwiseAnd(cfg, location, lhs, exp);

		// |=
		if (op.OR() != null)
			return new GoBitwiseOr(cfg, location, lhs, exp);

		// Return exp if the assignment operator is null
		return exp;
	}

	@Override
	public Statement visitAssign_op(Assign_opContext ctx) {
		// This method should never be visited
		throw new UnsupportedOperationException("Assign_op should never be visited");
	}

	/*
	 * Following the Go specification {@link
	 * https://golang.org/ref/spec#Short_variable_declarations} Unlike regular
	 * variable declarations, a short variable declaration may redeclare
	 * variables provided they were originally declared earlier in the same
	 * block (or the parameter lists if the block is the function body) with the
	 * same type, and at least one of the non-blank variables is new. As a
	 * consequence, redeclaration can only appear in a multi-variable short
	 * declaration. Redeclaration does not introduce a new variable; it just
	 * assigns a new value to the original.
	 */
	@Override
	public Pair<Statement, Statement> visitShortVarDecl(ShortVarDeclContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		// Number of identifiers to be assigned
		int sizeIds = ids.IDENTIFIER().size();
		// Number of expressions to be assigned
		int sizeExps = exps.expression().size();

		Statement lastStmt = null;
		Statement entryNode = null;

		// Multi variable short declaration
		if (sizeIds != sizeExps) {
			int line = getLine(ctx);
			int col = getCol(ctx);

			VariableRef[] left = visitIdentifierList(ctx.identifierList());

			for (int i = 0; i < left.length; i++)
				// if (visibleIds.containsKey(left[i].getName()))
				// throw new GoSyntaxException(
				// "Duplicate variable '" + left[i].getName() + "' declared at "
				// + left[i].getLocation());
				// else
				if (!GoLangUtils.refersToBlankIdentifier(left[i])) {
					visibleIds.putIfAbsent(left[i].getName(), new HashSet<IdInfo>());
					visibleIds.get(left[i].getName()).add(new IdInfo(left[i], blockDeep));
				}

			Expression right = visitExpression(exps.expression(0));

			GoMultiShortVariableDeclaration asg = new GoMultiShortVariableDeclaration(cfg, file, line, col, left,
					right, blockList, getContainingBlock());

			for (VariableRef ref : left)
				if (!GoLangUtils.refersToBlankIdentifier(ref))
					blockList.getLast().addVarDeclaration(ref, DeclarationType.MULTI_SHORT_VARIABLE);

			cfg.addNode(asg, visibleIds);
			return Pair.of(asg, asg);
		} else {

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp = visitExpression(exps.expression(i));

				int line = getLine(ids.IDENTIFIER(i));
				int col = getCol(exps.expression(i));

				// The type of the variable is implicit and it is retrieved from
				// the type of exp
				Type type = exp.getStaticType().isInMemoryType() ? new ReferenceType(exp.getStaticType())
						: exp.getStaticType();
				VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(),
						type);

				// if (visibleIds.containsKey(target.getName()))
				// throw new GoSyntaxException(
				// "Duplicate variable '" + target.getName() + "' declared at "
				// + target.getLocation());

				if (!GoLangUtils.refersToBlankIdentifier(target)) {
					visibleIds.putIfAbsent(target.getName(), new HashSet<IdInfo>());
					visibleIds.get(target.getName()).add(new IdInfo(target, blockDeep));
				}

				GoShortVariableDeclaration asg = new GoShortVariableDeclaration(cfg, file, line, col, target, exp);
				cfg.addNode(asg, visibleIds);

				if (!GoLangUtils.refersToBlankIdentifier(target))
					blockList.getLast().addVarDeclaration(target, DeclarationType.SHORT_VARIABLE);

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
			result = ArrayUtils.addAll(result,
					new VariableRef(cfg, locationOf(ctx.IDENTIFIER(i)), ctx.IDENTIFIER(i).getText()));
		return result;
	}

	@Override
	public Statement visitEmptyStmt(EmptyStmtContext ctx) {
		return new NoOp(cfg, locationOf(ctx));
	}

	@Override
	public Pair<Statement, Statement> visitReturnStmt(ReturnStmtContext ctx) {
		SourceCodeLocation location = locationOf(ctx);

		ExpressionListContext expressionList = ctx.expressionList();
		if (expressionList != null) {
			GoReturn ret;
			if (expressionList.expression().size() == 1)
				ret = new GoReturn(cfg, location, visitExpression(expressionList.expression(0)));
			else {
				GoExpressionsTuple tupleExp = new GoExpressionsTuple(cfg, location,
						visitExpressionList(expressionList));
				ret = new GoReturn(cfg, location, tupleExp);
			}
			cfg.addNode(ret, visibleIds);
			return Pair.of(ret, ret);
		} else {
			Type returnType = cfg.getDescriptor().getReturnType();
			if (returnType instanceof GoTypesTuple) {
				GoTypesTuple tuple = (GoTypesTuple) returnType;

				if (tuple.isNamedValues()) {
					Expression[] result = new Expression[tuple.size()];
					for (int i = 0; i < tuple.size(); i++)
						result[i] = new VariableRef(cfg, location, tuple.get(i).getName(), Untyped.INSTANCE);

					GoReturn ret = new GoReturn(cfg, location, new GoExpressionsTuple(cfg, location, result));
					cfg.addNode(ret, visibleIds);
					return Pair.of(ret, ret);
				}
			}

			Ret ret = new Ret(cfg, location);
			cfg.addNode(ret, visibleIds);
			return Pair.of(ret, ret);
		}
	}

	@Override
	public Pair<Statement, Statement> visitBreakStmt(BreakStmtContext ctx) {
		NoOp breakSt = new NoOp(cfg, locationOf(ctx));
		cfg.addNode(breakSt, visibleIds);

		addEdge(new SequentialEdge(breakSt, exitPoints.get(entryPoints.size() - 1)));
		return Pair.of(breakSt, breakSt);
	}

	@Override
	public Pair<Statement, Statement> visitContinueStmt(ContinueStmtContext ctx) {
		NoOp continueSt = new NoOp(cfg, locationOf(ctx));
		cfg.addNode(continueSt, visibleIds);

		addEdge(new SequentialEdge(continueSt, entryPoints.get(entryPoints.size() - 1)));
		return Pair.of(continueSt, continueSt);
	}

	@Override
	public Pair<Statement, Statement> visitLabeledStmt(LabeledStmtContext ctx) {
		Pair<Statement, Statement> stmt = visitStatement(ctx.statement());
		labeledStmt.put(ctx.IDENTIFIER().getText(), stmt.getLeft());
		return stmt;
	}

	@Override
	public Pair<Statement, Statement> visitGotoStmt(GotoStmtContext ctx) {
		GoTo nop = new GoTo(cfg, locationOf(ctx));
		cfg.addNode(nop);
		gotos.put(nop, ctx.IDENTIFIER().getText());
		return Pair.of(nop, nop);
	}

	@Override
	public Pair<Statement, Statement> visitFallthroughStmt(FallthroughStmtContext ctx) {
		GoFallThrough ft = new GoFallThrough(cfg, locationOf(ctx));
		cfg.addNode(ft, visibleIds);
		return Pair.of(ft, ft);
	}

	@Override
	public Pair<Statement, Statement> visitDeferStmt(DeferStmtContext ctx) {
		GoDefer defer = new GoDefer(cfg, file, getLine(ctx), getCol(ctx), visitExpression(ctx.expression()));
		cfg.addNode(defer, visibleIds);
		return Pair.of(defer, defer);
	}

	@Override
	public Pair<Statement, Statement> visitIfStmt(IfStmtContext ctx) {
		// Visit if statement Boolean Guard
		Statement booleanGuard = visitExpression(ctx.expression());
		cfg.addNode(booleanGuard, visibleIds);
		NoOp ifExitNode = new NoOp(cfg, locationOf(ctx));
		cfg.addNode(ifExitNode, visibleIds);

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

	private boolean isGoTo(Statement stmt) {
		return stmt instanceof GoTo;
	}

	protected void addEdge(Edge edge) {
		if (!isReturnStmt(edge.getSource()) && !isGoTo(edge.getSource()))
			cfg.addEdge(edge);
	}

	@Override
	public Pair<Statement, Statement> visitSwitchStmt(SwitchStmtContext ctx) {
		if (ctx.exprSwitchStmt() != null)
			return visitExprSwitchStmt(ctx.exprSwitchStmt());
		else if (ctx.typeSwitchStmt() != null)
			return visitTypeSwitchStmt(ctx.typeSwitchStmt());

		throw new IllegalStateException("Illegal state: switchStmt rule has no other productions.");
	}

	@Override
	public Pair<Statement, Statement> visitExprSwitchStmt(ExprSwitchStmtContext ctx) {
		SourceCodeLocation location = locationOf(ctx);
		Expression switchGuard = ctx.expression() == null ? new GoBoolean(cfg, location, true)
				: visitExpression(ctx.expression());
		NoOp exitNode = new NoOp(cfg, location);
		Statement entryNode = null;
		Statement previousGuard = null;
		Pair<Statement, Statement> defaultBlock = null;
		Pair<Statement, Statement> lastCaseBlock = null;
		cfg.addNode(exitNode, visibleIds);

		for (int i = 0; i < ctx.exprCaseClause().size(); i++) {
			ExprCaseClauseContext switchCase = ctx.exprCaseClause(i);
			Pair<Statement, Statement> caseBlock = visitStatementListOfSwitchCase(switchCase.statementList());
			Expression caseBooleanGuard = null;

			// Check if the switch case is not the default case
			if (switchCase.exprSwitchCase().expressionList() != null) {
				Expression[] expsCase = visitExpressionList(switchCase.exprSwitchCase().expressionList());
				for (int j = 0; j < expsCase.length; j++)
					if (caseBooleanGuard == null)
						caseBooleanGuard = new GoEqual(cfg, (SourceCodeLocation) expsCase[j].getLocation(), expsCase[j],
								switchGuard);
					else
						caseBooleanGuard = new GoLogicalOr(cfg, (SourceCodeLocation) expsCase[j].getLocation(),
								caseBooleanGuard, new GoEqual(cfg, (SourceCodeLocation) expsCase[j].getLocation(),
										expsCase[j], switchGuard));

				cfg.addNode(caseBooleanGuard, visibleIds);
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
			addEdge(new FalseEdge(previousGuard, defaultBlock.getLeft()));
			addEdge(new SequentialEdge(defaultBlock.getRight(), exitNode));
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
		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		NoOp exitNode = new NoOp(cfg, locationOf(ctx.block().R_CURLY()));
		cfg.addNode(exitNode, visibleIds);

		exitPoints.add(exitNode);

		if (ctx.forClause() != null) {
			boolean hasInitStmt = ctx.forClause().init != null;
			boolean hasCondition = ctx.forClause().guard != null;
			boolean hasPostStmt = ctx.forClause().inc != null;

			// Checking if initialization is missing
			Pair<Statement, Statement> init = null;
			Statement entryNode = null;
			if (hasInitStmt) {
				// TODO: variables declared here should be only visible in the
				// for block
				init = visitSimpleStmt(ctx.forClause().simpleStmt(0));
				cfg.addNode(entryNode = init.getLeft(), visibleIds);
			}

			// Checking if condition is missing: if so, true is the boolean
			// guard
			Statement cond = null;
			if (hasCondition)
				cond = visitExpression(ctx.forClause().expression());
			else
				cond = new GoBoolean(cfg, location, true);
			cfg.addNode(cond, visibleIds);

			entryPoints.add(cond);

			// Checking if post statement is missing
			Pair<Statement, Statement> post = null;
			if (hasPostStmt) {
				post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
				cfg.addNode(post.getLeft(), visibleIds);
			}

			Pair<Statement, Statement> block;
			if (ctx.block().statementList() == null) {
				NoOp emptyBlock = new NoOp(cfg, location);
				cfg.addNode(emptyBlock, visibleIds);
				block = Pair.of(emptyBlock, emptyBlock);
			} else
				block = visitBlock(ctx.block());

			Statement exitNodeBlock = block.getRight();
			Statement entryNodeOfBlock = block.getLeft();

			restoreVisibleIdsAfterForLoop(backup);

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

			entryPoints.remove(entryPoints.size() - 1);
			exitPoints.remove(exitPoints.size() - 1);
			return Pair.of(entryNode, exitNode);
		}

		/*
		 * For range
		 */
		else if (ctx.rangeClause() != null) {
			RangeClauseContext range = ctx.rangeClause();
			Expression rangedCollection = visitExpression(range.expression());
			VariableRef idxRange = null;
			VariableRef valRange = null;
			Statement idxInit = null;
			Statement idxPost = null;

			Statement valInit = new NoOp(cfg, SyntheticLocation.INSTANCE);
			Statement valPost = new NoOp(cfg, SyntheticLocation.INSTANCE);

			GoInteger zero = new GoInteger(cfg, locationOf(ctx), 0);
			GoInteger one = new GoInteger(cfg, locationOf(ctx), 1);

			if (range.identifierList() != null) {
				VariableRef[] rangeIds = visitIdentifierList(range.identifierList());

				if (rangeIds.length == 0) {
					throw new UnsupportedOperationException("empty range variables not supported yet.");
				} else {
					idxRange = rangeIds[0];
					idxInit = new GoShortVariableDeclaration(cfg, location, idxRange, zero);
					if (!GoLangUtils.refersToBlankIdentifier(idxRange))
						blockList.getLast().addVarDeclaration(idxRange, DeclarationType.SHORT_VARIABLE);
					idxPost = new GoAssignment(cfg, location, idxRange,
							new GoSum(cfg, location, idxRange, one), blockList, getContainingBlock());

					// Index and values are used in range
					if (rangeIds.length == 2) {
						valRange = rangeIds[1];

						// Creates the initialization statement for val range
						// variable
						valInit = new GoShortVariableDeclaration(cfg, location, valRange,
								new GoCollectionAccess(cfg, location, rangedCollection, zero));

						if (!GoLangUtils.refersToBlankIdentifier(valRange))
							blockList.getLast().addVarDeclaration(valRange, DeclarationType.SHORT_VARIABLE);

						valPost = new GoAssignment(cfg, location, valRange,
								new GoCollectionAccess(cfg, location, rangedCollection, idxRange), blockList,
								getContainingBlock());
					}
				}
			} else if (range.expressionList() != null) {
				Expression[] rangeIds = visitExpressionList(range.expressionList());

				if (rangeIds.length == 0) {
					throw new UnsupportedOperationException("empty range variables not supported yet.");
				} else {
					if (!(rangeIds[0] instanceof VariableRef))
						throw new IllegalStateException("range variables must  be identifiers.");

					idxRange = (VariableRef) rangeIds[0];
					idxInit = new GoAssignment(cfg, locationOf(ctx), idxRange, zero, blockList, getContainingBlock());

					if (rangeIds.length == 2) {
						valRange = (VariableRef) rangeIds[1];

						// Creates the initialization statements for idx and val
						// range variable
						valInit = new GoAssignment(cfg, location, valRange,
								new GoCollectionAccess(cfg, location, rangedCollection, zero), blockList,
								getContainingBlock());

						valPost = new GoAssignment(cfg, location, valRange,
								new GoCollectionAccess(cfg, location, rangedCollection, idxRange), blockList,
								getContainingBlock());
					}
				}
			} else
				throw new UnsupportedOperationException("empty range variables not supported yet.");

			entryPoints.add(idxInit);

			cfg.addNode(idxInit, visibleIds);
			cfg.addNode(valInit, visibleIds);
			cfg.addNode(idxPost, visibleIds);
			cfg.addNode(valPost, visibleIds);

			Pair<Statement, Statement> block = visitBlock(ctx.block());

			// Build the range condition
			GoLess rangeCondition = new GoLess(cfg, location, idxRange,
					new GoLength(cfg, location, rangedCollection));
			GoRange rangeNode = new GoRange(cfg, location, rangeCondition);
			cfg.addNode(rangeNode, visibleIds);

			addEdge(new SequentialEdge(idxInit, valInit));
			addEdge(new SequentialEdge(valInit, rangeNode));
			addEdge(new TrueEdge(rangeNode, block.getLeft()));
			addEdge(new FalseEdge(rangeNode, exitNode));
			addEdge(new SequentialEdge(block.getRight(), idxPost));
			addEdge(new SequentialEdge(idxPost, valPost));
			addEdge(new SequentialEdge(valPost, rangeNode));
			restoreVisibleIdsAfterForLoop(backup);

			entryPoints.remove(entryPoints.size() - 1);
			exitPoints.remove(exitPoints.size() - 1);
			return Pair.of(idxInit, exitNode);
		}

		// for i < n (corresponding to the classical while loop)
		if (ctx.expression() != null) {
			Expression guard = visitExpression(ctx.expression());
			cfg.addNode(guard, visibleIds);

			entryPoints.add(guard);

			Pair<Statement, Statement> block = visitBlock(ctx.block());

			restoreVisibleIdsAfterForLoop(backup);

			addEdge(new SequentialEdge(guard, block.getLeft()));
			addEdge(new SequentialEdge(guard, exitNode));
			addEdge(new SequentialEdge(block.getRight(), guard));

			entryPoints.remove(entryPoints.size() - 1);
			exitPoints.remove(exitPoints.size() - 1);
			return Pair.of(guard, exitNode);
		}

		// for { }
		NoOp entry = new NoOp(cfg, location);
		cfg.addNode(entry, visibleIds);

		entryPoints.add(entry);

		Pair<Statement, Statement> block = visitBlock(ctx.block());

		restoreVisibleIdsAfterForLoop(backup);

		addEdge(new SequentialEdge(block.getRight(), block.getLeft()));
		addEdge(new SequentialEdge(entry, block.getLeft()));
		addEdge(new SequentialEdge(block.getLeft(), exitNode));

		entryPoints.remove(entryPoints.size() - 1);
		exitPoints.remove(exitPoints.size() - 1);
		return Pair.of(entry, exitNode);
	}

	private void restoreVisibleIdsAfterForLoop(Map<String, Set<IdInfo>> backup) {

		Map<String, Set<IdInfo>> toRemove = new HashMap<>();
		for (Entry<String, Set<IdInfo>> id : visibleIds.entrySet())
			if (!backup.containsKey(id.getKey())) {
				for (IdInfo idInfo : id.getValue()) {
					toRemove.putIfAbsent(id.getKey(), new HashSet<IdInfo>());
					toRemove.get(id.getKey()).add(idInfo);
				}
			}

		if (!toRemove.isEmpty()) {
			for (String k : toRemove.keySet()) {
				if (visibleIds.containsKey(k)) {
					Set<IdInfo> visibleInfoSet = visibleIds.get(k);
					Set<IdInfo> removeInfoSet = toRemove.get(k);
					if (visibleInfoSet != null && removeInfoSet != null)
						visibleInfoSet.removeAll(removeInfoSet);

					if (visibleInfoSet == null || (visibleInfoSet != null && visibleInfoSet.isEmpty()))
						visibleIds.remove(k);
				}

			}

		}
	}

	@Override
	public CFGDescriptor visitMethodSpec(MethodSpecContext ctx) {
		if (ctx.typeName() == null) {
			Type returnType = ctx.result() == null ? Untyped.INSTANCE : visitResult(ctx.result());
			String name = ctx.IDENTIFIER().getText();

			Parameter[] params = visitParameters(ctx.parameters());
			// return new GoMethodSpecification(name, returnType, params);

			return new CFGDescriptor(locationOf(ctx), currentUnit, false, name, returnType, params);
		}

		throw new UnsupportedOperationException("Method specification not supported yet:  " + ctx.getText());
	}

	@Override
	public Expression visitPrimaryExpr(PrimaryExprContext ctx) {

		if (ctx.operand() != null)
			return visitOperand(ctx.operand());

		if (ctx.conversion() != null)
			return visitConversion(ctx.conversion());

		if (ctx.primaryExpr() != null) {

			// Check built-in functions
			String funcName = ctx.primaryExpr().getText();

			switch (funcName) {
			case "new":
				// new requires a type as input
				if (ctx.arguments().type_() != null)
					return new GoNew(cfg, locationOf(ctx.primaryExpr()), visitType_(ctx.arguments().type_()));
				else {
					// TODO: this is a workaround...
					return new GoNew(cfg, locationOf(ctx.primaryExpr()),
							parseType(ctx.arguments().expressionList().getText()));
				}
			case "len":
				Expression[] args = visitArguments(ctx.arguments());
				return new GoLength(cfg, locationOf(ctx.primaryExpr()), args[0]);

			case "make":
				args = visitArguments(ctx.arguments());
				if (ctx.arguments().type_() != null) {
					Type typeToAllocate = visitType_(ctx.arguments().type_());
					return new GoMake(cfg, locationOf(ctx.primaryExpr()), typeToAllocate, args);
				} else {
					return new GoMake(cfg, locationOf(ctx.primaryExpr()), Untyped.INSTANCE, args);
				}
			}

			Expression primary = visitPrimaryExpr(ctx.primaryExpr());

			// Function/method call (e.g., f(1,2,3), x.f(), rand.Intv(50))
			if (ctx.arguments() != null) {
				Expression[] args = visitArguments(ctx.arguments());

				if (primary instanceof VariableRef)
					// Function call (e.g., f(1,2,3))
					// this call is not an instance call
					// the callee's name is concatenated to the function name
					return new UnresolvedCall(cfg, locationOf(ctx), GoFrontEnd.PARAMETER_ASSIGN_STRATEGY,
							GoFrontEnd.FUNCTION_MATCHING_STRATEGY, GoFrontEnd.HIERARCY_TRAVERSAL_STRATEGY,
							CallType.STATIC,
							currentUnit.getName(), primary.toString(),
							visitArguments(ctx.arguments()));

				else if (primary instanceof AccessInstanceGlobal) {
					Expression receiver = (Expression) getReceiver(ctx.primaryExpr());

					if (program.getUnit(receiver.toString()) != null)
						// static method call (e.g., math.Intv(50))
						// this call is not an instance call
						// the callee's name is concatenated to the function
						// name
						return new UnresolvedCall(cfg, locationOf(ctx), GoFrontEnd.PARAMETER_ASSIGN_STRATEGY,
								GoFrontEnd.FUNCTION_MATCHING_STRATEGY, GoFrontEnd.HIERARCY_TRAVERSAL_STRATEGY,
								CallType.STATIC,
								receiver.toString(), getMethodName(ctx.primaryExpr()), args);
					else {
						// method call (e.g., x.f(1))
						// this call is an instance call
						// the callee needs to be resolved and it is put as
						// first argument (e.g., f(x, 1))
						args = ArrayUtils.insert(0, args, receiver);
						return new UnresolvedCall(cfg, locationOf(ctx), GoFrontEnd.PARAMETER_ASSIGN_STRATEGY,
								GoFrontEnd.METHOD_MATCHING_STRATEGY, GoFrontEnd.HIERARCY_TRAVERSAL_STRATEGY,
								CallType.INSTANCE, "", getMethodName(ctx.primaryExpr()), args);
					}
				}

				// Anonymous function
				else if (primary instanceof GoFunctionLiteral) {
					CFG cfgLiteral = (CFG) ((GoFunctionLiteral) primary).getValue();
					return new CFGCall(cfg, locationOf(ctx), GoFrontEnd.PARAMETER_ASSIGN_STRATEGY, CallType.STATIC, "",
							funcName,
							Collections.singleton(cfgLiteral),
							args);
				} else {
					// need to resolve also the caller
					args = ArrayUtils.insert(0, args, primary);
					return new UnresolvedCall(cfg, locationOf(ctx), GoFrontEnd.PARAMETER_ASSIGN_STRATEGY,
							GoFrontEnd.METHOD_MATCHING_STRATEGY, GoFrontEnd.HIERARCY_TRAVERSAL_STRATEGY,
							CallType.INSTANCE,
							"", primary.toString(), args);
				}
			}

			// Array/slice/map access e1[e2]
			else if (ctx.index() != null) {
				Expression index = visitIndex(ctx.index());
				return new GoCollectionAccess(cfg, locationOf(ctx), primary, index);
			}

			// Field access x.f
			else if (ctx.IDENTIFIER() != null) {
				Global index = new Global(locationOf(ctx.IDENTIFIER()), ctx.IDENTIFIER().getText(), Untyped.INSTANCE);
				return new AccessInstanceGlobal(cfg, locationOf(ctx), (Expression) primary, index);
			}

			// Simple slice expression a[l:h]
			else if (ctx.slice() != null) {
				Pair<Expression, Expression> args = visitSlice(ctx.slice());

				if (args.getRight() == null)
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(),
							new GoLength(cfg, locationOf(ctx), (Expression) primary));
				else
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(), args.getRight());
			}

			else if (ctx.typeAssertion() != null) {
				return new GoTypeAssertion(cfg, locationOf(ctx), primary, visitType_(ctx.typeAssertion().type_()));
			}
		}

		throw new IllegalStateException("Illegal state: primaryExpr rule has no other productions.");
	}

	private String getMethodName(PrimaryExprContext primary) {
		return primary.IDENTIFIER().getText();
	}

	private Object getReceiver(PrimaryExprContext primary) {
		return visitPrimaryExpr(primary.primaryExpr());
	}

	@Override
	public Object visitUnaryExpr(UnaryExprContext ctx) {
		if (ctx.primaryExpr() != null)
			return visitPrimaryExpr(ctx.primaryExpr());
		SourceCodeLocation location = locationOf(ctx);

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

		if (ctx.RECEIVE() != null)
			return new GoChannelReceive(cfg, location, exp);

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitConversion(ConversionContext ctx) {
		Type type = visitType_(ctx.type_());
		Expression exp = visitExpression(ctx.expression());
		return new GoTypeConversion(cfg, locationOf(ctx), type, exp);
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
		if (ctx.basicLit() != null)
			return visitBasicLit(ctx.basicLit());
		else if (ctx.compositeLit() != null)
			return visitCompositeLit(ctx.compositeLit());
		else if (ctx.functionLit() != null)
			return visitFunctionLit(ctx.functionLit());

		throw new IllegalStateException("Illegal state: literal rule has no other productions.");
	}

	@Override
	public Expression visitBasicLit(BasicLitContext ctx) {
		// Go decimal integer
		if (ctx.integer() != null)
			return visitInteger(ctx.integer());

		SourceCodeLocation location = locationOf(ctx);
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
		SourceCodeLocation location = locationOf(ctx);

		// TODO: for the moment, we skip any other integer literal format (e.g.,
		// imaginary)
		if (ctx.DECIMAL_LIT() != null)
			try {
				return new GoInteger(cfg, location, Integer.parseInt(ctx.DECIMAL_LIT().getText()));
			} catch (NumberFormatException e) {
				return new GoInteger(cfg, location, new BigInteger(ctx.DECIMAL_LIT().getText()));
			}

		if (ctx.RUNE_LIT() != null)
			return new GoRune(cfg, location, removeQuotes(ctx.getText()));

		if (ctx.HEX_LIT() != null)
			// removes '0x'
			return new GoInteger(cfg, location, Integer.parseInt(ctx.HEX_LIT().getText().substring(2), 16));

		if (ctx.OCTAL_LIT() != null)
			return new GoInteger(cfg, location, Integer.parseInt(ctx.OCTAL_LIT().getText(), 8));

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitOperandName(OperandNameContext ctx) {
		// TODO: qualified identifier not handled
		if (ctx.IDENTIFIER() != null) {
			SourceCodeLocation location = locationOf(ctx);

			// Boolean values (true, false) are matched as identifiers
			String stringId = ctx.IDENTIFIER().getText();
			if (stringId.equals("true") || stringId.equals("false"))
				return new GoBoolean(cfg, location, Boolean.parseBoolean(stringId));
			else if (constants.containsKey(stringId))
				return visitExpression(constants.get(stringId));
			else
				return new VariableRef(cfg, location, stringId);
		}

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Expression visitCompositeLit(CompositeLitContext ctx) {
		Type type = new GoTypeVisitor(file, currentUnit, program, constants).visitLiteralType(ctx.literalType());
		Object raw = visitLiteralValue(ctx.literalValue(), type);
		if (raw instanceof LinkedHashMap<?, ?>) {

			Object[] keysObj = ((Map<Expression, Expression>) raw).keySet().toArray();
			Object[] valuesObj = ((Map<Expression, Expression>) raw).values().toArray();

			Expression[] keys = new Expression[keysObj.length];
			Expression[] values = new Expression[valuesObj.length];

			for (int i = 0; i < keys.length; i++) {
				keys[i] = (Expression) keysObj[i];
				values[i] = (Expression) valuesObj[i];
			}
			if (type instanceof GoArrayType && ((GoArrayType) type).getLength() == -1)
				type = GoArrayType
						.lookup(new GoArrayType(((GoArrayType) type).getContenType(), ((Expression[]) keys).length));
			return new GoKeyedLiteral(cfg, locationOf(ctx), keys, values, type == null ? Untyped.INSTANCE : type);
		} else {

			if (type instanceof GoArrayType && ((GoArrayType) type).getLength() == -1)
				type = GoArrayType
						.lookup(new GoArrayType(((GoArrayType) type).getContenType(), ((Expression[]) raw).length));
			return new GoNonKeyedLiteral(cfg, locationOf(ctx), (Expression[]) raw,
					type == null ? Untyped.INSTANCE : type);
		}
	}

	public Object visitLiteralValue(LiteralValueContext ctx, Type type) {
		if (ctx.elementList() == null)
			return new LinkedHashMap<Expression, Expression>();
		return visitElementList(ctx.elementList(), type);
	}

	@SuppressWarnings("unchecked")
	public Object visitElementList(ElementListContext ctx, Type type) {
		// All keyed or all without key
		Object firstElement = visitKeyedElement(ctx.keyedElement(0), type);

		if (firstElement instanceof Pair<?, ?>) {
			LinkedHashMap<Expression, Expression> result = new LinkedHashMap<>();
			Pair<Expression, Expression> firstKeyed = (Pair<Expression, Expression>) firstElement;
			result.put(firstKeyed.getLeft(), firstKeyed.getRight());
			for (int i = 1; i < ctx.keyedElement().size(); i++) {
				Pair<Expression,
						Expression> keyed = (Pair<Expression, Expression>) visitKeyedElement(ctx.keyedElement(i), type);
				result.put(keyed.getLeft(), keyed.getRight());
			}

			return result;
		} else {
			Expression[] result = new Expression[ctx.keyedElement().size()];
			result[0] = (Expression) firstElement;
			for (int i = 1; i < ctx.keyedElement().size(); i++)
				result[i] = (Expression) visitKeyedElement(ctx.keyedElement(i), type);
			return result;
		}
	}

	public Object visitKeyedElement(KeyedElementContext ctx, Type type) {
		if (ctx.key() != null)
			return Pair.of(visitKey(ctx.key()), visitElement(ctx.element(), type));
		else
			return visitElement(ctx.element(), type);
	}

	@Override
	public Expression visitKey(KeyContext ctx) {

		if (ctx.IDENTIFIER() != null)
			return new VariableRef(cfg, locationOf(ctx), ctx.IDENTIFIER().getText());

		Object child = visitChildren(ctx);
		if (!(child instanceof Expression))
			throw new IllegalStateException("Expression expected, found Statement instead");
		else
			return (Expression) child;
	}

	@Override
	public Pair<Statement, Statement> visitSendStmt(SendStmtContext ctx) {
		GoChannelSend send = new GoChannelSend(cfg, locationOf(ctx), visitExpression(ctx.expression(0)),
				visitExpression(ctx.expression(1)));
		cfg.addNode(send, visibleIds);
		return Pair.of(send, send);
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

	@SuppressWarnings("unchecked")
	public Expression visitElement(ElementContext ctx, Type type) {

		if (ctx.expression() != null)
			return visitExpression(ctx.expression());
		else {
			Object lit = visitLiteralValue(ctx.literalValue(), type);

			if (lit instanceof Expression)
				return (Expression) lit;
			else if (lit instanceof Expression[])
				return new GoNonKeyedLiteral(cfg, locationOf(ctx), (Expression[]) lit, getContentType(type));
			else if (lit instanceof Map<?, ?>) {
				Object[] keysObj = ((Map<Expression, Expression>) lit).keySet().toArray();
				Object[] valuesObj = ((Map<Expression, Expression>) lit).values().toArray();

				Expression[] keys = new Expression[keysObj.length];
				Expression[] values = new Expression[valuesObj.length];

				for (int i = 0; i < keys.length; i++) {
					keys[i] = (Expression) keysObj[i];
					values[i] = (Expression) valuesObj[i];
				}

				if (type instanceof GoArrayType && ((GoArrayType) type).getLength() == -1)
					type = GoArrayType.lookup(
							new GoArrayType(((GoArrayType) type).getContenType(), ((Expression[]) keys).length));
				return new GoKeyedLiteral(cfg, locationOf(ctx), keys, values, type);
			} else
				throw new IllegalStateException(
						"Expression, Expression[] or  LinkedHashMap expected, found Statement instead");
		}
	}

	private GoType getContentType(Type type) {
		if (type instanceof GoArrayType)
			return (GoType) ((GoArrayType) type).getContenType();
		if (type instanceof GoSliceType)
			return (GoType) ((GoSliceType) type).getContentType();

		throw new IllegalStateException(type + " has no content type");
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
	public Expression visitIndex(IndexContext ctx) {
		return visitExpression(ctx.expression());
	}

	@Override
	public Expression visitFunctionLit(FunctionLitContext ctx) {
		CFG funcLit = new GoFunctionVisitor(ctx, currentUnit, file, program, constants).buildAnonymousCFG(ctx);
		Type funcType = GoFunctionType
				.lookup(new GoFunctionType(funcLit.getDescriptor().getFormals(),
						funcLit.getDescriptor().getReturnType()));
		return new GoFunctionLiteral(cfg, locationOf(ctx), funcLit, funcType);
	}

	@Override
	public Statement visitTypeAssertion(TypeAssertionContext ctx) {
		// This method should never be visited
		throw new UnsupportedOperationException("Type assertion should never be visited");
	}

	@Override
	public Expression[] visitArguments(ArgumentsContext ctx) {
		Expression[] exps = new Expression[] {};
		if (ctx.expressionList() != null)
			for (int i = 0; i < ctx.expressionList().expression().size(); i++)
				exps = ArrayUtils.addAll(exps, visitExpression(ctx.expressionList().expression(i)));
		return exps;
	}

	@Override
	public Pair<Statement, Statement> visitGoStmt(GoStmtContext ctx) {
		Expression call = visitExpression(ctx.expression());

		if (!(call instanceof Call))
			throw new IllegalStateException("Only method and function calls can be spawn as go routines.");

		GoRoutine routine = new GoRoutine(cfg, locationOf(ctx), (Call) call);
		cfg.addNode(routine, visibleIds);
		return Pair.of(routine, routine);
	}

	private String removeQuotes(String str) {
		return str.substring(1, str.length() - 1);
	}

	private Type parseType(String type) {
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

		Type t = visitType_((Type_Context) tree);
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t == null ? Untyped.INSTANCE : t;
	}

	@Override
	public Type visitType_(Type_Context ctx) {
		return new GoTypeVisitor(file, currentUnit, program, constants).visitType_(ctx);
	}

	@Override
	public Pair<Statement, Statement> visitTypeSwitchStmt(TypeSwitchStmtContext ctx) {

		SourceCodeLocation location = locationOf(ctx);
		Expression typeSwitchExp = visitPrimaryExpr(ctx.typeSwitchGuard().primaryExpr());

		NoOp exitNode = new NoOp(cfg, location);
		cfg.addNode(exitNode, visibleIds);

		Statement entryNode = null;
		Statement previousGuard = null;
		Pair<Statement, Statement> defaultBlock = null;

		for (int i = 0; i < ctx.typeCaseClause().size(); i++) {
			TypeCaseClauseContext typeSwitchCase = ctx.typeCaseClause(i);
			Pair<Statement, Statement> caseBlock = visitStatementList(typeSwitchCase.statementList());
			Expression caseBooleanGuard = null;

			// Check if the switch case is not the default case
			if (typeSwitchCase.typeSwitchCase().typeList() != null) {
				Type[] types = visitTypeList(typeSwitchCase.typeSwitchCase().typeList());
				for (int j = 0; j < types.length; j++) {
					SourceCodeLocation typeLoc = locationOf(typeSwitchCase.typeSwitchCase().typeList().type_(j));

					VariableRef typeSwitchVar;

					// If the switch identifier is missing, we create a fake
					// identifier
					if (ctx.typeSwitchGuard().IDENTIFIER() == null)
						typeSwitchVar = new VariableRef(cfg, typeLoc, "switchId");
					else
						typeSwitchVar = new VariableRef(cfg, typeLoc, ctx.typeSwitchGuard().IDENTIFIER().getText());

					VariableRef typeSwitchCheck = new VariableRef(cfg, typeLoc, "ok");
					VariableRef[] ids = new VariableRef[] { typeSwitchVar, typeSwitchCheck };

					GoMultiShortVariableDeclaration shortDecl = new GoMultiShortVariableDeclaration(cfg,
							typeLoc.getSourceFile(), typeLoc.getLine(), typeLoc.getCol(), ids,
							new GoTypeAssertion(cfg, typeLoc, typeSwitchExp, types[j]), blockList,
							getContainingBlock());

					for (VariableRef id : ids)
						if (!GoLangUtils.refersToBlankIdentifier(id))
							blockList.getLast().addVarDeclaration(id, DeclarationType.MULTI_SHORT_VARIABLE);

					caseBooleanGuard = new GoEqual(cfg, typeLoc, typeSwitchCheck,
							new GoBoolean(cfg, typeLoc, true));

					cfg.addNode(shortDecl, visibleIds);
					cfg.addNode(caseBooleanGuard, visibleIds);
					addEdge(new SequentialEdge(shortDecl, caseBooleanGuard));

					addEdge(new TrueEdge(caseBooleanGuard, caseBlock.getLeft()));
					addEdge(new SequentialEdge(caseBlock.getRight(), exitNode));

					if (entryNode == null)
						entryNode = shortDecl;
					else
						addEdge(new FalseEdge(previousGuard, shortDecl));

					previousGuard = caseBooleanGuard;
				}

			} else
				defaultBlock = caseBlock;

		}

		if (defaultBlock != null) {
			addEdge(new FalseEdge(previousGuard, defaultBlock.getLeft()));
			addEdge(new SequentialEdge(defaultBlock.getRight(), exitNode));
		} else
			addEdge(new FalseEdge(previousGuard, exitNode));

		if (ctx.simpleStmt() != null) {
			Pair<Statement, Statement> simpleStmt = visitSimpleStmt(ctx.simpleStmt());
			addEdge(new SequentialEdge(simpleStmt.getRight(), entryNode));
			entryNode = simpleStmt.getLeft();
		}

		return Pair.of(entryNode, exitNode);
	}

	@Override
	public Type[] visitTypeList(TypeListContext ctx) {
		Type[] types = new Type[ctx.type_().size()];
		for (int i = 0; i < types.length; i++)
			types[i] = visitType_(ctx.type_(i));
		return types;
	}

	@Override
	public Statement visitEos(EosContext ctx) {
		throw new UnsupportedOperationException("eos should never be visited");
	}

	@Override
	public Statement visitSignature(SignatureContext ctx) {
		throw new IllegalStateException("signarure should never be visited");
	}

	@Override
	public Statement visitForClause(ForClauseContext ctx) {
		throw new UnsupportedOperationException("forClause should never be visited");
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
	public Statement visitTypeSwitchGuard(TypeSwitchGuardContext ctx) {
		throw new IllegalStateException("typeSwitchGuard should never be visited.");
	}

	@Override
	public Statement visitTypeCaseClause(TypeCaseClauseContext ctx) {
		throw new IllegalStateException("typeCaseClause should never be visited.");
	}

	@Override
	public Statement visitTypeSwitchCase(TypeSwitchCaseContext ctx) {
		throw new IllegalStateException("typeSwitchCase should never be visited.");
	}

	@Override
	public Pair<Statement, Statement> visitSelectStmt(SelectStmtContext ctx) {
		NoOp entry = new NoOp(cfg, locationOf(ctx.L_CURLY()));
		NoOp exit = new NoOp(cfg, locationOf(ctx.R_CURLY()));
		cfg.addNode(exit);
		cfg.addNode(entry);

		for (CommClauseContext clause : ctx.commClause()) {
			Pair<Statement, Statement> block = visitCommClause(clause);
			addEdge(new SequentialEdge(entry, block.getLeft()));
			addEdge(new SequentialEdge(block.getRight(), exit));
		}

		return Pair.of(entry, exit);
	}

	@Override
	public Pair<Statement, Statement> visitCommClause(CommClauseContext ctx) {
		// FIXME: we are currently skipping comm case
		return visitStatementList(ctx.statementList());
	}

	@Override
	public Statement visitCommCase(CommCaseContext ctx) {
		// TODO select statement (see issue #22)
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitRecvStmt(RecvStmtContext ctx) {
		// TODO select statement (see issue #22)
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Expression visitMethodExpr(MethodExprContext ctx) {
		// TODO: method expression
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	@Override
	public Statement visitRangeClause(RangeClauseContext ctx) {
		// TODO range clause (see issue #4)
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

}
