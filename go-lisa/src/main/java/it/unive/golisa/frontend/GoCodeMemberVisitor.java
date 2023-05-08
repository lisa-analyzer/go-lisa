package it.unive.golisa.frontend;

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
import org.apache.commons.lang3.tuple.Triple;

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
import it.unive.golisa.cfg.DefaultSwitchCase;
import it.unive.golisa.cfg.Switch;
import it.unive.golisa.cfg.SwitchCase;
import it.unive.golisa.cfg.TypeSwitch;
import it.unive.golisa.cfg.TypeSwitchCase;
import it.unive.golisa.cfg.VarArgsParameter;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.expression.GoForRange;
import it.unive.golisa.cfg.expression.GoMake;
import it.unive.golisa.cfg.expression.GoNew;
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
import it.unive.golisa.cfg.expression.binary.GoNotEqual;
import it.unive.golisa.cfg.expression.binary.GoRightShift;
import it.unive.golisa.cfg.expression.binary.GoSubtraction;
import it.unive.golisa.cfg.expression.binary.GoSum;
import it.unive.golisa.cfg.expression.binary.GoTypeAssertion;
import it.unive.golisa.cfg.expression.literal.GoBoolean;
import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.expression.literal.GoFunctionLiteral;
import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.expression.literal.GoKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoRune;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.expression.ternary.GoSimpleSlice;
import it.unive.golisa.cfg.expression.unary.GoBitwiseNot;
import it.unive.golisa.cfg.expression.unary.GoChannelReceive;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.expression.unary.GoLength;
import it.unive.golisa.cfg.expression.unary.GoMinus;
import it.unive.golisa.cfg.expression.unary.GoNot;
import it.unive.golisa.cfg.expression.unary.GoPlus;
import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextIndex;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextValue;
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
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.SyntheticLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.controlFlow.IfThenElse;
import it.unive.lisa.program.cfg.controlFlow.Loop;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.FalseEdge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.edge.TrueEdge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Return;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.literal.TrueLiteral;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

/**
 * A {@link GoParserBaseVisitor} that will parse the code of an Go method.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoCodeMemberVisitor extends GoParserBaseVisitor<Object> {

	/**
	 * Singleton of a {@link SequentialEdge}.
	 */
	static final SequentialEdge SEQUENTIAL_SINGLETON = new SequentialEdge();

	/**
	 * The path file.
	 */
	protected final String file;

	/**
	 * The entry points.
	 */
	protected final Collection<Statement> entrypoints;

	/**
	 * Mapping between goto statements and label to which they have to jump to.
	 */
	protected final Map<Statement, Pair<List<BlockInfo>, String>> gotos;

	/**
	 * Mapping between statements and their labels.
	 */
	protected final Map<String, Pair<List<BlockInfo>, Statement>> labeledStmt;

	/**
	 * The current cfg.
	 */
	protected VariableScopingCFG cfg;

	/**
	 * The current program.
	 */
	protected final Program program;

	/**
	 * Counter used to give a name to anonymous functions.
	 */
	protected static int c = 0;

	/**
	 * Mapping from constant names to their expression contexts.
	 */
	protected final Map<String, ExpressionContext> constants;

	/**
	 * Globals of program.
	 */
	protected final List<Global> globals;

	/**
	 * Current compilation unit to parse.
	 */
	protected CompilationUnit currentUnit;

	/**
	 * Block deep.
	 */
	private int blockDeep;

	/**
	 * Stack of loop exit points (used for break statements).
	 */
	private final List<Statement> exitPoints = new ArrayList<>();

	/**
	 * Stack of loop entry points (used for continue statements).
	 */
	private final List<Statement> entryPoints = new ArrayList<>();

	private NodeList<CFG, Statement, Edge> matrix;

	protected final LinkedList<BlockInfo> blockList = new LinkedList<>();

	private final Collection<ControlFlowStructure> cfs;

	private final Map<String, Set<IdInfo>> visibleIds;

	/**
	 * Builds the code member visitor.
	 * 
	 * @param unit      the current unit
	 * @param file      the file path
	 * @param program   the program
	 * @param constants constant mapping
	 * @param globals   global variables
	 */
	public GoCodeMemberVisitor(CompilationUnit unit, String file, Program program,
			Map<String, ExpressionContext> constants, List<Global> globals) {
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
		this.globals = globals;
	}

	/**
	 * Builds the code member visitor.
	 * 
	 * @param unit      the current unit
	 * @param ctx       the method declaration context to visit
	 * @param file      the file path
	 * @param program   the program
	 * @param constants constant mapping
	 * @param globals   the global variables
	 */
	public GoCodeMemberVisitor(CompilationUnit unit, MethodDeclContext ctx, String file, Program program,
			Map<String, ExpressionContext> constants, List<Global> globals) {
		this.file = file;
		this.program = program;
		this.constants = constants;

		gotos = new HashMap<>();
		labeledStmt = new HashMap<>();
		entrypoints = new HashSet<>();
		cfs = new LinkedList<>();
		// side effects on entrypoints and matrix will affect the cfg
		cfg = new VariableScopingCFG(mkDescriptor(unit, ctx), entrypoints, new NodeList<>(SEQUENTIAL_SINGLETON));

		visibleIds = new HashMap<>();
		this.blockDeep = 0;
		this.globals = globals;

		initializeVisibleIds();
	}

	private void addGlobalsToVisibleIds() {
		for (Global g : globals) {
			visibleIds.putIfAbsent(g.getName(), new HashSet<>());
			visibleIds.get(g.getName())
					.add(new IdInfo(new VariableRef(cfg, g.getLocation(), g.getName(), g.getStaticType()), -1));
		}
	}

	/**
	 * Initializes the visible identifiers.
	 */
	protected void initializeVisibleIds() {
		for (VariableTableEntry par : cfg.getDescriptor().getVariables())
			if (!GoLangUtils.refersToBlankIdentifier(par.createReference(cfg))) {
				visibleIds.putIfAbsent(par.getName(), new HashSet<IdInfo>());
				visibleIds.get(par.getName()).add(new IdInfo(par.createReference(cfg), blockDeep));
			}

	}

	private CodeMemberDescriptor mkDescriptor(CompilationUnit packageUnit, MethodDeclContext ctx) {
		return new CodeMemberDescriptor(locationOf(ctx), packageUnit, false, ctx.IDENTIFIER().getText(),
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
				? ((GoPointerType) receiver.getStaticType()).getInnerType().toString()
				: receiver.getStaticType().toString();

		SourceCodeLocation location = locationOf(ctx);
		if (program.getUnit(unitName) == null) {
			// TODO: unknown unit
			currentUnit = new ClassUnit(location, program, unitName, false);
			program.addUnit(currentUnit);
		} else
			currentUnit = (CompilationUnit) program.getUnit(unitName);

		String methodName = ctx.IDENTIFIER().getText();
		Parameter[] params = visitParameters(ctx.signature().parameters());
		params = ArrayUtils.insert(0, params, receiver);
		Type returnType;
		if (ctx.signature().result() == null)
			returnType = Untyped.INSTANCE;
		else {
			Type t = visitResult(ctx.signature().result());
			if (t.isInMemoryType())
				returnType = GoPointerType.lookup(t);
			else
				returnType = t;
		}

		if (returnType == null)
			returnType = Untyped.INSTANCE;

		cfg = new VariableScopingCFG(
				new CodeMemberDescriptor(location, currentUnit, true, methodName, returnType, params));

		addGlobalsToVisibleIds();

		Triple<Statement, NodeList<CFG, Statement, Edge>,
				Statement> body = visitMethodBlock(ctx.block());

		processGotos();

		cfg.getEntrypoints().add(body.getLeft());

		addReturnStatement(cfg.getNodeList());
		addFinalRet(matrix);

		cfg.simplify();

		currentUnit.addInstanceCodeMember(cfg);
		return cfg;
	}

	/**
	 * Adds a return statement is not present in the cfg.
	 * 
	 * @param matrix the adjacency matrix
	 */
	protected void addReturnStatement(NodeList<CFG, Statement, Edge> matrix) {
		// If the method body does not have exit points
		// a return statement is added
		if (cfg.getAllExitpoints().isEmpty()) {
			Ret ret = new Ret(cfg, cfg.getDescriptor().getLocation());
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

				for (VariableTableEntry entry : cfg.getDescriptor().getVariables())
					if (preExits.contains(entry.getScopeEnd()))
						entry.setScopeEnd(ret);
			}
		}
	}

	/**
	 * Adds a return statement is not present in matrix given as argument.
	 * 
	 * @param matrix the adjacency matrix
	 */
	protected void addFinalRet(NodeList<CFG, Statement, Edge> matrix) {
		for (Statement st : matrix.getExits())
			if (st instanceof NoOp && !matrix.getIngoingEdges(st).isEmpty()) {
				Ret ret = new Ret(cfg, cfg.getDescriptor().getLocation());
				if (!st.stopsExecution() && matrix.followersOf(st).isEmpty())
					matrix.addNode(ret);
				matrix.addEdge(new SequentialEdge(st, ret));
			}
	}

	/**
	 * Resolves goto jumps.
	 * 
	 * @throws IllegalStateException if a goto jumps into other scopes
	 */
	protected void processGotos() {
		for (Entry<Statement, Pair<List<BlockInfo>, String>> gotoStmt : gotos.entrySet()) {
			// we must call cfg.addEdge, and not addEdge
			String label = gotoStmt.getValue().getRight();
			List<BlockInfo> scope = gotoStmt.getValue().getLeft();
			Statement labeled = labeledStmt.get(label).getRight();
			List<BlockInfo> targetScope = labeledStmt.get(label).getLeft();

			// from https://go.dev/ref/spec#Goto_statements:
			// "[...] Executing the 'goto' statement must not
			// cause any variables to come into scope that
			// were not already in scope at the point of the goto.
			// [...] A 'goto' statement outside a block
			// cannot jump to a label inside that block."
			// From this I understand that the goto can only
			// jump inside the same scope, or to a containing one.
			// this means that 'targetScope' must be a prefix of 'scope'
			// and every other scope can be closed.

			if (scope.size() < targetScope.size())
				throw new IllegalStateException("goto cannot jump to a scope that it is not already part of");
			for (int i = 0; i < targetScope.size(); i++)
				if (!scope.get(i).equals(targetScope.get(i)))
					throw new IllegalStateException("goto cannot jump to a scope that it is not already part of");

			Statement last = gotoStmt.getKey();
			// remove any followers
			for (Statement follow : cfg.followersOf(last))
				cfg.getNodeList().removeEdge(cfg.getEdgeConnecting(last, follow));
			// add closing blocks
			for (int i = scope.size() - 1; i >= targetScope.size(); i--) {
				BlockInfo info = scope.get(i);
				CloseBlock close = new CloseBlock(cfg, gotoStmt.getKey().getLocation(), info.getOpen());
				cfg.addNode(close);
				cfg.addEdge(new SequentialEdge(last, close));
				last = close;
			}
			// add the goto
			cfg.addEdge(new SequentialEdge(last, labeled));
		}
	}

	/**
	 * Visits a method block.
	 * 
	 * @param ctx the block context to visit
	 * 
	 * @return the adjacency matrix behind the visited block, together with the
	 *             entry and the exit nodes
	 */
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitMethodBlock(BlockContext ctx) {
		this.matrix = this.cfg.getNodeList();

		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		if (ctx.statementList() == null) {
			NoOp noop = new NoOp(cfg, locationOf(ctx.R_CURLY()));
			block.addNode(noop);
			updateVisileIds(backup, noop);
			matrix.mergeWith(block);
			return Triple.of(noop, block, noop);
		}

		// we build the block information to the block list
		// but it is not added as node being a method block
		OpenBlock open = new OpenBlock(cfg, locationOf(ctx.L_CURLY()));
		blockList.addLast(new BlockInfo(open));

		// in the open block of the method, we register that the parameters has
		// been declarared
		Parameter[] formals = this.cfg.getDescriptor().getFormals();
		for (int i = 0; i < formals.length; i++) {
			VariableRef v = new VariableRef(cfg, formals[i].getLocation(), formals[i].getName(),
					formals[i].getStaticType());
			blockList.getLast().addVarDeclaration(v, DeclarationType.VARIABLE);
		}

		Triple<Statement, NodeList<CFG, Statement, Edge>,
				Statement> res = visitStatementList(ctx.statementList());
		updateVisileIds(backup, res.getRight());

		cfs.forEach(cfg::addControlFlowStructure);
		matrix.mergeWith(res.getMiddle());

		return res;
	}

	/**
	 * Yields the line of a parse rule context.
	 * 
	 * @param ctx the parse rule context
	 * 
	 * @return the line of a parse rule context
	 */
	static protected int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	/**
	 * Yields the line of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return the line of a terminal node
	 */
	static protected int getLine(TerminalNode ctx) {
		return ctx.getSymbol().getLine();
	}

	/**
	 * Yields the column of a parse rule context.
	 * 
	 * @param ctx the parse rule context
	 * 
	 * @return the column of a parse rule context
	 */
	static protected int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	}

	/**
	 * Yields the column of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return the column of a terminal node
	 */
	static protected int getCol(TerminalNode ctx) {
		return ctx.getSymbol().getCharPositionInLine();
	}

	/**
	 * Yields the source code location of a parse rule context.
	 * 
	 * @param ctx the parse rule context
	 * 
	 * @return the source code location of a parse rule context
	 */
	protected SourceCodeLocation locationOf(ParserRuleContext ctx) {
		return new SourceCodeLocation(file, getLine(ctx), getCol(ctx));
	}

	/**
	 * Yields the source code location of a terminal node.
	 * 
	 * @param ctx the terminal node
	 * 
	 * @return the source code location of a terminal node
	 */
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
		boolean variadic = ctx.ELLIPSIS() != null; 

		// the parameter's type is variadic (e.g., ...string)
		if (variadic)
			type = GoSliceType.lookup(type);

		if (ctx.identifierList() == null)
			if (variadic)
				result = ArrayUtils.add(result, new VarArgsParameter(locationOf(ctx), "_", type));
			else
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
			return GoTupleType.lookup(visitParameters(ctx.parameters()));
		}
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitBlock(BlockContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		if (ctx.statementList() == null) {
			NoOp noop = new NoOp(cfg, locationOf(ctx.R_CURLY()));
			block.addNode(noop);
			updateVisileIds(backup, noop);
			return Triple.of(noop, block, noop);
		}

		blockDeep++;

		OpenBlock open = new OpenBlock(cfg, locationOf(ctx.L_CURLY()));
		block.addNode(open);

		CloseBlock close = new CloseBlock(cfg, locationOf(ctx.R_CURLY()), open);

		blockList.addLast(new BlockInfo(open));

		Triple<Statement, NodeList<CFG, Statement, Edge>,
				Statement> res = visitStatementList(ctx.statementList());
		block.mergeWith(res.getMiddle());
		addEdge(new SequentialEdge(open, res.getLeft()), block);

		Statement last = res.getRight();
		updateVisileIds(backup, last);
		if (isReturnStmt(last)) {
			blockDeep--;
			blockList.removeLast();
			return Triple.of(open, block, last);
		}
		if (isGoTo(last)) {
			// we still decrement as the actual closing
			// blocks will be added in the post processing
			blockDeep--;
			blockList.removeLast();
			return Triple.of(open, block, last);
		}

		block.addNode(close);
		addEdge(new SequentialEdge(last, close), block);

		blockDeep--;
		blockList.removeLast();

		return Triple.of(open, block, close);
	}

	/**
	 * Updates the visible ids.
	 * 
	 * @param backup the current visible ids
	 * @param last   the statement
	 */
	protected void updateVisileIds(Map<String, Set<IdInfo>> backup, Statement last) {

		Map<String, Set<IdInfo>> toRemove = new HashMap<>();
		for (Entry<String, Set<IdInfo>> id : visibleIds.entrySet())
			if (!backup.containsKey(id.getKey())) {
				for (IdInfo idInfo : id.getValue()) {
					VariableRef ref = idInfo.getRef();
					cfg.getDescriptor().addVariable(new VariableTableEntry(ref.getLocation(),
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitStatementList(
			StatementListContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		// It is an empty statement
		if (ctx == null || ctx.statement().size() == 0) {
			NoOp nop = new NoOp(cfg, SyntheticLocation.INSTANCE);
			block.addNode(nop);
			return Triple.of(nop, block, nop);
		}

		Statement lastStmt = null;
		Statement entryNode = null;

		for (int i = 0; i < ctx.statement().size(); i++) {
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> currentStmt = visitStatement(ctx.statement(i));
			block.mergeWith(currentStmt.getMiddle());

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()), block);
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();
		}

		return Triple.of(entryNode, block, lastStmt);
	}

	private Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitStatementListOfSwitchCase(
			StatementListContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		// It is an empty statement
		if (ctx == null || ctx.statement().size() == 0) {
			NoOp nop = new NoOp(cfg, SyntheticLocation.INSTANCE);
			block.addNode(nop);
			return Triple.of(nop, block, nop);
		}

		Statement lastStmt = null;
		Statement entryNode = null;

		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);

		for (int i = 0; i < ctx.statement().size(); i++) {
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> currentStmt = visitStatement(ctx.statement(i));
			block.mergeWith(currentStmt.getMiddle());

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currentStmt.getLeft()), block);
			else
				entryNode = currentStmt.getLeft();

			lastStmt = currentStmt.getRight();

			// scoping must be updated for each case
			updateVisileIds(backup, lastStmt);
		}

		return Triple.of(entryNode, block, lastStmt);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitStatement(StatementContext ctx) {
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitVarDecl(VarDeclContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		Statement lastStmt = null;
		Statement entryNode = null;

		for (VarSpecContext varSpec : ctx.varSpec()) {
			Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> currStmt = visitVarSpec(varSpec);
			block.mergeWith(currStmt.getMiddle());

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()), block);
			else
				entryNode = currStmt.getLeft();
			lastStmt = currStmt.getRight();
		}

		return Triple.of(entryNode, block, lastStmt);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitVarSpec(VarSpecContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

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
			GoMultiShortVariableDeclaration asg = new GoMultiShortVariableDeclaration(cfg,
					new SourceCodeLocation(file, line, col), left,
					right, blockList,
					getContainingBlock());
			block.addNode(asg);
			storeIds(asg);

			return Triple.of(asg, block, asg);
		} else {

			Statement lastStmt = null;
			Statement entryNode = null;
			Type_Context typeContext = ctx.type_();

			Type type = typeContext == null ? Untyped.INSTANCE : visitType_(typeContext);
			type = type == null ? Untyped.INSTANCE : type;

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp;
				if (type.isUntyped() && exps == null)
					exp = new GoUnknown(cfg, locationOf(ctx));
				else {

					if ((exps == null || exps.expression(i) == null) && !type.isUntyped()) {
						exp = ((GoType) type).defaultValue(cfg, locationOf(ctx));
					} else
						exp = visitExpression(exps.expression(i));
				}

				int line = getLine(ids.IDENTIFIER(i));
				int col = (exps == null || exps.expression(i) == null) ? getCol(ids.IDENTIFIER(i))
						: getCol(exps.expression(i));

				VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(),
						Untyped.INSTANCE);
				GoVariableDeclaration asg = new GoVariableDeclaration(cfg, new SourceCodeLocation(file, line, col),
						type, target, exp);
				block.addNode(asg);

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

				storeIds(asg);

				if (lastStmt != null)
					addEdge(new SequentialEdge(lastStmt, asg), block);
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Triple.of(entryNode, block, lastStmt);
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitDeclaration(
			DeclarationContext ctx) {
		if (ctx.constDecl() != null)
			return visitConstDecl(ctx.constDecl());
		if (ctx.typeDecl() != null) {
			for (CompilationUnit unit : visitTypeDecl(ctx.typeDecl()))
				program.addUnit(unit);
			NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
			NoOp noop = new NoOp(cfg, locationOf(ctx.typeDecl().TYPE()));
			block.addNode(noop);
			return Triple.of(noop, block, noop);
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
			ClassUnit unit = new ClassUnit(
					new SourceCodeLocation(file, getLine(typeSpec), getCol(typeSpec)), program, unitName, false);
			units.add(unit);
			new GoTypeVisitor(file, unit, program, constants, globals).visitTypeSpec(typeSpec);
		}
		return units;
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitConstDecl(ConstDeclContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		Statement lastStmt = null;
		Statement entryNode = null;

		for (ConstSpecContext constSpec : ctx.constSpec()) {
			Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> currStmt = visitConstSpec(constSpec);
			block.mergeWith(currStmt.getMiddle());

			if (lastStmt != null)
				addEdge(new SequentialEdge(lastStmt, currStmt.getLeft()), block);
			else
				entryNode = currStmt.getLeft();

			lastStmt = currStmt.getRight();
		}

		return Triple.of(entryNode, block, lastStmt);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitConstSpec(ConstSpecContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		Statement lastStmt = null;
		Statement entryNode = null;
		Type type = ctx.type_() == null ? Untyped.INSTANCE : visitType_(ctx.type_());

		for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
			VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(), type);
			Expression exp = visitExpression(exps.expression(i));

			GoConstantDeclaration asg = new GoConstantDeclaration(cfg, locationOf(ctx), target, exp);
			block.addNode(asg);
			storeIds(asg);

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
				addEdge(new SequentialEdge(lastStmt, asg), block);
			else
				entryNode = asg;
			lastStmt = asg;
		}

		return Triple.of(entryNode, block, lastStmt);
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitSimpleStmt(SimpleStmtContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof Triple<?, ?, ?>))
			throw new IllegalStateException("Pair of Statements expected");
		else
			return (Triple<Statement, NodeList<CFG, Statement, Edge>, Statement>) result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitExpressionStmt(
			ExpressionStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		Object result = visitChildren(ctx);
		if (result instanceof Expression) {
			Expression e = (Expression) result;
			block.addNode(e);
			storeIds(e);
			return Triple.of(e, block, e);
		} else if (!(result instanceof Triple<?, ?, ?>)) {
			throw new IllegalStateException("Triple of Statements expected");
		} else
			return (Triple<Statement, NodeList<CFG, Statement, Edge>, Statement>) result;
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitIncDecStmt(IncDecStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

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

		block.addNode(asg);
		storeIds(asg);
		return Triple.of(asg, block, asg);
	}

	private OpenBlock getContainingBlock() {
		return blockList.getLast().getOpen();
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitAssignment(AssignmentContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

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

			GoMultiAssignment asg = new GoMultiAssignment(cfg, new SourceCodeLocation(file, line, col), left, right,
					blockList,
					getContainingBlock());
			block.addNode(asg);
			storeIds(asg);
			return Triple.of(asg, block, asg);
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
				block.addNode(asg);
				storeIds(asg);

				if (lastStmt != null)
					addEdge(new SequentialEdge(lastStmt, asg), block);
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Triple.of(entryNode, block, lastStmt);
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitShortVarDecl(
			ShortVarDeclContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

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

			GoMultiShortVariableDeclaration asg = new GoMultiShortVariableDeclaration(cfg,
					new SourceCodeLocation(file, line, col), left,
					right, blockList, getContainingBlock());

			for (VariableRef ref : left)
				if (!GoLangUtils.refersToBlankIdentifier(ref))
					blockList.getLast().addVarDeclaration(ref, DeclarationType.MULTI_SHORT_VARIABLE);

			block.addNode(asg);
			storeIds(asg);
			return Triple.of(asg, block, asg);
		} else {

			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Expression exp = visitExpression(exps.expression(i));

				int line = getLine(ids.IDENTIFIER(i));
				int col = getCol(exps.expression(i));

				// The type of the variable is implicit and it is retrieved from
				// the type of exp
				VariableRef target = new VariableRef(cfg, locationOf(ids.IDENTIFIER(i)), ids.IDENTIFIER(i).getText(),
						Untyped.INSTANCE);

				// if (visibleIds.containsKey(target.getName()))
				// throw new GoSyntaxException(
				// "Duplicate variable '" + target.getName() + "' declared at "
				// + target.getLocation());

				if (!GoLangUtils.refersToBlankIdentifier(target)) {
					visibleIds.putIfAbsent(target.getName(), new HashSet<IdInfo>());
					visibleIds.get(target.getName()).add(new IdInfo(target, blockDeep));
				}

				GoShortVariableDeclaration asg = new GoShortVariableDeclaration(cfg,
						new SourceCodeLocation(file, line, col), target, exp);
				block.addNode(asg);
				storeIds(asg);

				if (!GoLangUtils.refersToBlankIdentifier(target))
					blockList.getLast().addVarDeclaration(target, DeclarationType.SHORT_VARIABLE);

				if (lastStmt != null)
					addEdge(new SequentialEdge(lastStmt, asg), block);
				else
					entryNode = asg;
				lastStmt = asg;
			}

			return Triple.of(entryNode, block, lastStmt);
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitReturnStmt(ReturnStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		SourceCodeLocation location = locationOf(ctx);

		ExpressionListContext expressionList = ctx.expressionList();
		if (expressionList != null) {
			GoReturn ret;
			if (expressionList.expression().size() == 1)
				ret = new GoReturn(cfg, location, visitExpression(expressionList.expression(0)));
			else {
				GoTupleExpression tupleExp = new GoTupleExpression(cfg, location,
						visitExpressionList(expressionList));
				ret = new GoReturn(cfg, location, tupleExp);
			}
			block.addNode(ret);
			storeIds(ret);
			return Triple.of(ret, block, ret);
		} else {
			Type returnType = cfg.getDescriptor().getReturnType();
			if (returnType.isPointerType() && returnType.asPointerType().getInnerType() instanceof GoTupleType) {
				GoTupleType tuple = (GoTupleType) returnType.asPointerType().getInnerType();

				if (tuple.isNamedValues()) {
					Expression[] result = new Expression[tuple.size()];
					for (int i = 0; i < tuple.size(); i++)
						result[i] = new VariableRef(cfg, location, tuple.get(i).getName(), Untyped.INSTANCE);

					GoReturn ret = new GoReturn(cfg, location, new GoTupleExpression(cfg, location, result));
					block.addNode(ret);
					storeIds(ret);
					return Triple.of(ret, block, ret);
				}
			}

			Ret ret = new Ret(cfg, location);
			block.addNode(ret);
			storeIds(ret);
			return Triple.of(ret, block, ret);
		}
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitBreakStmt(BreakStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		NoOp breakSt = new NoOp(cfg, locationOf(ctx));
		block.addNode(breakSt);
		storeIds(breakSt);

		Statement exit = exitPoints.get(exitPoints.size() - 1);
		block.addNode(exit);
		addEdge(new SequentialEdge(breakSt, exit), block);
		return Triple.of(breakSt, block, breakSt);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitContinueStmt(
			ContinueStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		NoOp continueSt = new NoOp(cfg, locationOf(ctx));
		block.addNode(continueSt);
		storeIds(continueSt);

		Statement entry = entryPoints.get(entryPoints.size() - 1);
		block.addNode(entry);
		addEdge(new SequentialEdge(continueSt, entry), block);
		return Triple.of(continueSt, block, continueSt);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitLabeledStmt(
			LabeledStmtContext ctx) {
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> stmt = visitStatement(ctx.statement());
		labeledStmt.put(ctx.IDENTIFIER().getText(), Pair.of(new LinkedList<>(blockList), stmt.getLeft()));
		return stmt;
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitGotoStmt(GotoStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		GoTo nop = new GoTo(cfg, locationOf(ctx));
		block.addNode(nop);
		gotos.put(nop, Pair.of(new LinkedList<>(blockList), ctx.IDENTIFIER().getText()));
		return Triple.of(nop, block, nop);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitFallthroughStmt(
			FallthroughStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		GoFallThrough ft = new GoFallThrough(cfg, locationOf(ctx));
		block.addNode(ft);
		storeIds(ft);
		return Triple.of(ft, block, ft);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitDeferStmt(DeferStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		GoDefer defer = new GoDefer(cfg, new SourceCodeLocation(file, getLine(ctx), getCol(ctx)),
				visitExpression(ctx.expression()));
		block.addNode(defer);
		storeIds(defer);
		return Triple.of(defer, block, defer);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitIfStmt(IfStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		// Visit if statement Boolean Guard
		Statement booleanGuard = visitExpression(ctx.expression());
		block.addNode(booleanGuard);
		storeIds(booleanGuard);
		NoOp ifExitNode = new NoOp(cfg, locationOf(ctx));
		block.addNode(ifExitNode);
		storeIds(ifExitNode);

		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> trueBlock = visitBlock(ctx.block(0));
		block.mergeWith(trueBlock.getMiddle());
		Collection<Statement> trueBlockNodes = trueBlock.getMiddle().getNodes();
		Collection<Statement> falseBlockNodes = Collections.emptySet();

		Statement exitStatementTrueBranch = trueBlock.getRight();
		Statement entryStatementTrueBranch = trueBlock.getLeft();

		if (ctx.ELSE() == null) {
			// If statement without else branch
			addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch), block);
			addEdge(new FalseEdge(booleanGuard, ifExitNode), block);
			addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode), block);
		} else {
			if (ctx.block(1) != null) {
				// If statement with else branch with no other if statements
				Triple<Statement, NodeList<CFG, Statement, Edge>,
						Statement> falseBlock = visitBlock(ctx.block(1));
				block.mergeWith(falseBlock.getMiddle());
				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch), block);
				addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch), block);

				addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode), block);
				addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode), block);

				falseBlockNodes = falseBlock.getMiddle().getNodes();
			} else {
				// If statement with else branch with other if statements
				Triple<Statement, NodeList<CFG, Statement, Edge>,
						Statement> falseBlock = visitIfStmt(ctx.ifStmt());
				block.mergeWith(falseBlock.getMiddle());

				Statement exitStatementFalseBranch = falseBlock.getRight();
				Statement entryStatementFalseBranch = falseBlock.getLeft();

				addEdge(new TrueEdge(booleanGuard, entryStatementTrueBranch), block);
				addEdge(new FalseEdge(booleanGuard, entryStatementFalseBranch), block);

				addEdge(new SequentialEdge(exitStatementTrueBranch, ifExitNode), block);
				addEdge(new SequentialEdge(exitStatementFalseBranch, ifExitNode), block);

				falseBlockNodes = falseBlock.getMiddle().getNodes();
			}
		}

		// Checks whether the if-statement has an initial statement
		// e.g., if x := y; z < x block
		Statement entryNode = booleanGuard;
		if (ctx.simpleStmt() != null) {
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> initialStmt = visitSimpleStmt(ctx.simpleStmt());
			block.mergeWith(initialStmt.getMiddle());
			entryNode = initialStmt.getLeft();
			addEdge(new SequentialEdge(initialStmt.getRight(), booleanGuard), block);
		}

		cfs.add(new IfThenElse(matrix, booleanGuard, ifExitNode, trueBlockNodes, falseBlockNodes));

		return Triple.of(entryNode, block, ifExitNode);
	}

	private static boolean isReturnStmt(Statement stmt) {
		return stmt instanceof GoReturn || stmt instanceof Ret;
	}

	private static boolean isGoTo(Statement stmt) {
		return stmt instanceof GoTo;
	}

	/**
	 * Adds the edge iff the source is not an instance of {@link Return} of
	 * {@link GoTo} statements.
	 * 
	 * @param edge  the edge to be added
	 * @param block the current {@link AdjacencyMatrix}
	 */
	protected static void addEdge(Edge edge, NodeList<CFG, Statement, Edge> block) {
		if (!isReturnStmt(edge.getSource()) && !isGoTo(edge.getSource()))
			block.addEdge(edge);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitSwitchStmt(SwitchStmtContext ctx) {
		if (ctx.exprSwitchStmt() != null)
			return visitExprSwitchStmt(ctx.exprSwitchStmt());
		else if (ctx.typeSwitchStmt() != null)
			return visitTypeSwitchStmt(ctx.typeSwitchStmt());

		throw new IllegalStateException("Illegal state: switchStmt rule has no other productions.");
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitExprSwitchStmt(
			ExprSwitchStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		NodeList<CFG, Statement, Edge> body = new NodeList<>(SEQUENTIAL_SINGLETON);

		SourceCodeLocation location = locationOf(ctx);
		Expression switchGuard = ctx.expression() == null ? new GoBoolean(cfg, location, true)
				: visitExpression(ctx.expression());
		NoOp exitNode = new NoOp(cfg, location);
		Statement entryNode = null;
		Statement previousGuard = null;
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> defaultBlock = null;
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> lastCaseBlock = null;
		block.addNode(exitNode);
		storeIds(exitNode);

		int ncases = ctx.exprCaseClause().size();
		List<SwitchCase> scases = new ArrayList<>(ncases);
		DefaultSwitchCase def = null;

		for (int i = 0; i < ncases; i++) {
			ExprCaseClauseContext switchCase = ctx.exprCaseClause(i);
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> caseBlock = visitStatementListOfSwitchCase(switchCase.statementList());
			body.mergeWith(caseBlock.getMiddle());

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

				body.addNode(caseBooleanGuard);
				storeIds(caseBooleanGuard);
				addEdge(new TrueEdge(caseBooleanGuard, caseBlock.getLeft()), body);

				block.mergeWith(body);
				if (!(caseBlock.getRight() instanceof GoFallThrough))
					addEdge(new SequentialEdge(caseBlock.getRight(), exitNode), block);

				if (lastCaseBlock != null)
					addEdge(new SequentialEdge(lastCaseBlock.getRight(), caseBlock.getLeft()), block);

				if (entryNode == null) {
					entryNode = caseBooleanGuard;
				} else {
					addEdge(new FalseEdge(previousGuard, caseBooleanGuard), block);
				}
				previousGuard = caseBooleanGuard;
				scases.add(new SwitchCase(caseBooleanGuard, caseBlock.getMiddle().getNodes()));
			} else {
				defaultBlock = caseBlock;
				block.mergeWith(body);
				def = new DefaultSwitchCase(caseBlock.getLeft(), caseBlock.getMiddle().getNodes());
			}

			if (caseBlock.getRight() instanceof GoFallThrough)
				lastCaseBlock = caseBlock;
			else
				lastCaseBlock = null;
		}

		if (lastCaseBlock != null)
			addEdge(new SequentialEdge(lastCaseBlock.getRight(), exitNode), block);

		if (defaultBlock != null) {
			addEdge(new FalseEdge(previousGuard, defaultBlock.getLeft()), block);
			addEdge(new SequentialEdge(defaultBlock.getRight(), exitNode), block);
		} else {
			addEdge(new FalseEdge(previousGuard, exitNode), block);
		}

		if (ctx.simpleStmt() != null) {
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> simpleStmt = visitSimpleStmt(ctx.simpleStmt());
			block.mergeWith(simpleStmt.getMiddle());
			addEdge(new SequentialEdge(simpleStmt.getRight(), entryNode), block);
			entryNode = simpleStmt.getLeft();
		}

		cfs.add(new Switch(matrix, entryNode, exitNode, scases.toArray(SwitchCase[]::new), def));

		return Triple.of(entryNode, block, exitNode);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitForStmt(ForStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		SourceCodeLocation location = locationOf(ctx);
		Map<String, Set<IdInfo>> backup = new HashMap<>(visibleIds);
		NoOp exitNode = new NoOp(cfg, locationOf(ctx.block().R_CURLY()));
		block.addNode(exitNode);
		storeIds(exitNode);

		exitPoints.add(exitNode);

		if (ctx.forClause() != null)
			return regularFor(ctx, block, location, backup, exitNode);
		else if (ctx.rangeClause() != null)
			return forRange(ctx, block, location, backup, exitNode);
		else if (ctx.expression() != null)
			return whileLoop(ctx, block, backup, exitNode);
		else
			return forTrue(ctx, block, location, backup, exitNode);
	}

	private Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> forTrue(ForStmtContext ctx,
			NodeList<CFG, Statement, Edge> block, SourceCodeLocation location, Map<String, Set<IdInfo>> backup,
			NoOp exitNode) {
		Statement cond = new TrueLiteral(cfg, location);
		block.addNode(cond);
		storeIds(cond);

		entryPoints.add(cond);

		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> body = visitBlock(ctx.block());
		block.mergeWith(body.getMiddle());

		restoreVisibleIdsAfterForLoop(backup);

		addEdge(new TrueEdge(cond, body.getLeft()), block);
		addEdge(new FalseEdge(cond, exitNode), block);
		addEdge(new SequentialEdge(body.getRight(), cond), block);

		entryPoints.remove(entryPoints.size() - 1);
		exitPoints.remove(exitPoints.size() - 1);

		cfs.add(new Loop(matrix, cond, exitNode, body.getMiddle().getNodes()));

		return Triple.of(cond, block, exitNode);
	}

	private Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> whileLoop(ForStmtContext ctx,
			NodeList<CFG, Statement, Edge> block, Map<String, Set<IdInfo>> backup, NoOp exitNode) {
		Expression guard = visitExpression(ctx.expression());
		block.addNode(guard);
		storeIds(guard);

		entryPoints.add(guard);

		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> body = visitBlock(ctx.block());
		block.mergeWith(body.getMiddle());

		restoreVisibleIdsAfterForLoop(backup);

		addEdge(new TrueEdge(guard, body.getLeft()), block);
		addEdge(new FalseEdge(guard, exitNode), block);
		addEdge(new SequentialEdge(body.getRight(), guard), block);

		entryPoints.remove(entryPoints.size() - 1);
		exitPoints.remove(exitPoints.size() - 1);

		cfs.add(new Loop(matrix, guard, exitNode, body.getMiddle().getNodes()));

		return Triple.of(guard, block, exitNode);
	}

	private Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> forRange(ForStmtContext ctx,
			NodeList<CFG, Statement, Edge> block, SourceCodeLocation location, Map<String, Set<IdInfo>> backup,
			NoOp exitNode) {
		RangeClauseContext range = ctx.rangeClause();
		Expression rangedCollection = visitExpression(range.expression());

		GoRange rangeNode = new GoRange(cfg, location, rangedCollection);

		VariableRef idxRange = null;
		VariableRef valRange = null;
		Statement idxAssign = null;
		Statement valueAssign = null;

		if (range.identifierList() != null) {
			VariableRef[] rangeIds = visitIdentifierList(range.identifierList());

			if (rangeIds.length == 0) {
				throw new UnsupportedOperationException("empty range variables not supported yet.");
			} else {
				idxRange = rangeIds[0];
				idxAssign = new GoShortVariableDeclaration(cfg, location, idxRange, new GoRangeGetNextIndex(cfg,
						new SourceCodeLocation(location.getSourceFile(), location.getLine(), 0), rangedCollection));
				if (!GoLangUtils.refersToBlankIdentifier(idxRange))
					blockList.getLast().addVarDeclaration(idxRange, DeclarationType.SHORT_VARIABLE);
				// Index and values are used in range
				if (rangeIds.length == 2) {
					valRange = rangeIds[1];
					valueAssign = new GoShortVariableDeclaration(cfg, location, valRange, new GoRangeGetNextValue(cfg,
							new SourceCodeLocation(location.getSourceFile(), location.getLine(), 1), rangedCollection));
					if (!GoLangUtils.refersToBlankIdentifier(valRange))
						blockList.getLast().addVarDeclaration(valRange, DeclarationType.SHORT_VARIABLE);
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
				idxAssign = new GoShortVariableDeclaration(cfg, location, idxRange,
						new GoRangeGetNextIndex(cfg, location, rangedCollection));
				if (rangeIds.length == 2) {
					valRange = (VariableRef) rangeIds[1];
					valueAssign = new GoShortVariableDeclaration(cfg, location, valRange,
							new GoRangeGetNextValue(cfg, location, rangedCollection));
				}
			}
		} else
			throw new UnsupportedOperationException("empty range variables not supported yet.");

		rangeNode.setIdxRange(idxRange);
		rangeNode.setValRange(valRange);

		block.addNode(rangeNode);
		addEdge(new FalseEdge(rangeNode, exitNode), block);

		if (idxAssign != null) {
			block.addNode(idxAssign);
			storeIds(idxAssign);
		}
		if (valueAssign != null) {
			block.addNode(valueAssign);
			storeIds(valueAssign);
		}

		NodeList<CFG, Statement, Edge> body = new NodeList<>(SEQUENTIAL_SINGLETON);
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> inner = visitBlock(ctx.block());
		body.mergeWith(inner.getMiddle());
		block.mergeWith(body);

		addEdge(new SequentialEdge(inner.getRight(), rangeNode), block);

		if (idxAssign != null && valueAssign != null) {
			addEdge(new TrueEdge(rangeNode, idxAssign), block);
			addEdge(new SequentialEdge(idxAssign, valueAssign), block);
			addEdge(new SequentialEdge(valueAssign, inner.getLeft()), block);
		} else if (idxAssign != null && valueAssign == null) {
			addEdge(new TrueEdge(rangeNode, idxAssign), block);
			addEdge(new SequentialEdge(idxAssign, inner.getLeft()), block);
		} else { // should never happen
			addEdge(new TrueEdge(rangeNode, valueAssign), block);
			addEdge(new SequentialEdge(valueAssign, inner.getLeft()), block);
		}

		// entryPoints.remove(entryPoints.size() - 1);
		// exitPoints.remove(exitPoints.size() - 1);
		restoreVisibleIdsAfterForLoop(backup);

		cfs.add(new GoForRange(matrix, idxRange, valueAssign, rangeNode, exitNode, body.getNodes()));

		return Triple.of(rangeNode, block, exitNode);
	}

	private Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> regularFor(ForStmtContext ctx,
			NodeList<CFG, Statement, Edge> block, SourceCodeLocation location, Map<String, Set<IdInfo>> backup,
			NoOp exitNode) {
		boolean hasInitStmt = ctx.forClause().init != null;
		boolean hasCondition = ctx.forClause().guard != null;
		boolean hasPostStmt = ctx.forClause().inc != null;

		// Checking if initialization is missing
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> init = null;
		Statement entryNode = null;
		if (hasInitStmt) {
			// TODO: variables declared here should be only visible in the
			// for block
			init = visitSimpleStmt(ctx.forClause().simpleStmt(0));
			block.mergeWith(init.getMiddle());
			entryNode = init.getLeft();
			storeIds(entryNode);
		}

		// Checking if condition is missing: if so, true is the boolean
		// guard
		Statement cond = null;
		if (hasCondition)
			cond = visitExpression(ctx.forClause().expression());
		else
			cond = new GoBoolean(cfg, location, true);

		block.addNode(cond);
		storeIds(cond);

		entryPoints.add(cond);

		// Checking if post statement is missing
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> post = null;
		if (hasPostStmt) {
			post = visitSimpleStmt(hasInitStmt ? ctx.forClause().simpleStmt(1) : ctx.forClause().simpleStmt(0));
			block.mergeWith(post.getMiddle());
			storeIds(post.getLeft());
		}

		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> inner;
		if (ctx.block().statementList() == null) {
			NodeList<CFG, Statement, Edge> innerBlock = new NodeList<>(SEQUENTIAL_SINGLETON);
			NoOp emptyBlock = new NoOp(cfg, location);
			innerBlock.addNode(emptyBlock);
			storeIds(exitNode);
			inner = Triple.of(emptyBlock, innerBlock, emptyBlock);
		} else
			inner = visitBlock(ctx.block());
		block.mergeWith(inner.getMiddle());

		Statement exitNodeBlock = inner.getRight();
		Statement entryNodeOfBlock = inner.getLeft();

		restoreVisibleIdsAfterForLoop(backup);

		addEdge(new TrueEdge(cond, entryNodeOfBlock), block);
		addEdge(new FalseEdge(cond, exitNode), block);

		if (hasInitStmt)
			addEdge(new SequentialEdge(init.getRight(), cond), block);
		else
			entryNode = cond;

		NodeList<CFG, Statement, Edge> body;
		if (hasPostStmt) {
			addEdge(new SequentialEdge(exitNodeBlock, post.getRight()), block);
			addEdge(new SequentialEdge(post.getLeft(), cond), block);
			body = new NodeList<>(inner.getMiddle());
			body.mergeWith(post.getMiddle());
		} else {
			addEdge(new SequentialEdge(exitNodeBlock, cond), block);
			body = inner.getMiddle();
		}

		entryPoints.remove(entryPoints.size() - 1);
		exitPoints.remove(exitPoints.size() - 1);

		cfs.add(new Loop(matrix, cond, exitNode, body.getNodes()));

		return Triple.of(entryNode, block, exitNode);
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
	public CodeMemberDescriptor visitMethodSpec(MethodSpecContext ctx) {
		if (ctx.typeName() == null) {
			Type returnType;
			if (ctx.result() == null)
				returnType = Untyped.INSTANCE;
			else {
				Type t = visitResult(ctx.result());
				if (t.isInMemoryType())
					returnType = GoPointerType.lookup(t);
				else
					returnType = t;
			}
			String name = ctx.IDENTIFIER().getText();

			Parameter[] params = visitParameters(ctx.parameters());
			return new CodeMemberDescriptor(locationOf(ctx), currentUnit, false, name, returnType, params);
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
					return new UnresolvedCall(cfg, locationOf(ctx),
							CallType.STATIC,
							currentUnit.getName(), primary.toString(),
							visitArguments(ctx.arguments()));

				else if (primary instanceof GoCollectionAccess) {
					Expression receiver = ((GoCollectionAccess) primary).getReceiver();
					String methodName = ((GoCollectionAccess) primary).getTarget().toString();

					if (program.getUnit(receiver.toString()) != null)
						// static method call (e.g., math.Intv(50))
						// this call is not an instance call
						// the callee's name is concatenated to the function
						// name
						return new UnresolvedCall(cfg, locationOf(ctx),
								CallType.STATIC,
								receiver.toString(), methodName, args);
					else {
						// method call (e.g., x.f(1))
						// this call is an instance call
						// the callee needs to be resolved and it is put as
						// first argument (e.g., f(x, 1))
						args = ArrayUtils.insert(0, args, receiver);
						return new UnresolvedCall(cfg, locationOf(ctx),
								CallType.INSTANCE, "", methodName, args);
					}
				}

				// Anonymous function
				else if (primary instanceof GoFunctionLiteral) {
					CFG cfgLiteral = (CFG) ((GoFunctionLiteral) primary).getValue();
					return new CFGCall(cfg, locationOf(ctx), CallType.STATIC, "",
							funcName,
							Collections.singleton(cfgLiteral),
							args);
				} else {
					// need to resolve also the caller
					args = ArrayUtils.insert(0, args, primary);
					return new UnresolvedCall(cfg, locationOf(ctx),
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
				VariableRef i = new VariableRef(cfg, locationOf(ctx.IDENTIFIER()), ctx.IDENTIFIER().getText());
				return new GoCollectionAccess(cfg, locationOf(ctx), primary, i);
			}

			// Simple slice expression a[l:h]
			else if (ctx.slice() != null) {
				Pair<Expression, Expression> args = visitSlice(ctx.slice());

				if (args.getRight() == null)
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(),
							new GoLength(cfg, locationOf(ctx), primary));
				else
					return new GoSimpleSlice(cfg, locationOf(ctx), primary, args.getLeft(), args.getRight());
			}

			else if (ctx.typeAssertion() != null) {
				return new GoTypeAssertion(cfg, locationOf(ctx), primary, visitType_(ctx.typeAssertion().type_()));
			}
		}

		throw new IllegalStateException("Illegal state: primaryExpr rule has no other productions.");
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
		Type type = new GoTypeVisitor(file, currentUnit, program, constants, globals)
				.visitLiteralType(ctx.literalType());
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
						.lookup(((GoArrayType) type).getContenType(), ((Expression[]) keys).length);
			return new GoKeyedLiteral(cfg, locationOf(ctx), keys, values, type == null ? Untyped.INSTANCE : type);
		} else {

			if (type instanceof GoArrayType && ((GoArrayType) type).getLength() == -1)
				type = GoArrayType
						.lookup(((GoArrayType) type).getContenType(), ((Expression[]) raw).length);
			return new GoNonKeyedLiteral(cfg, locationOf(ctx), (Expression[]) raw,
					type == null ? Untyped.INSTANCE : type);
		}
	}

	private Object visitLiteralValue(LiteralValueContext ctx, Type type) {
		if (ctx.elementList() == null)
			return new LinkedHashMap<Expression, Expression>();
		return visitElementList(ctx.elementList(), type);
	}

	/**
	 * Visits a element list context.
	 * 
	 * @param ctx  the context
	 * @param type the type of the first element
	 * 
	 * @return the visit result
	 */
	public Object visitElementList(ElementListContext ctx, Type type) {
		// All keyed or all without key
		Object firstElement = visitKeyedElement(ctx.keyedElement(0), type);

		if (firstElement instanceof Pair<?, ?>) {
			LinkedHashMap<Expression, Expression> result = new LinkedHashMap<>();
			@SuppressWarnings("unchecked")
			Pair<Expression, Expression> firstKeyed = (Pair<Expression, Expression>) firstElement;
			result.put(firstKeyed.getLeft(), firstKeyed.getRight());
			for (int i = 1; i < ctx.keyedElement().size(); i++) {
				@SuppressWarnings("unchecked")
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

	private Object visitKeyedElement(KeyedElementContext ctx, Type type) {
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitSendStmt(SendStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		GoChannelSend send = new GoChannelSend(cfg, locationOf(ctx), visitExpression(ctx.expression(0)),
				visitExpression(ctx.expression(1)));
		block.addNode(send);
		storeIds(send);
		return Triple.of(send, block, send);
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
	private Expression visitElement(ElementContext ctx, Type type) {

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
							((GoArrayType) type).getContenType(), ((Expression[]) keys).length);
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
		CFG funcLit = new GoFunctionVisitor(ctx, currentUnit, file, program, constants, globals).buildAnonymousCFG(ctx);
		Type funcType = GoFunctionType
				.lookup(funcLit.getDescriptor().getReturnType(),
						funcLit.getDescriptor().getFormals());
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitGoStmt(GoStmtContext ctx) {
		Expression call = visitExpression(ctx.expression());

		if (!(call instanceof Call))
			throw new IllegalStateException("Only method and function calls can be spawn as go routines.");

		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);
		GoRoutine routine = new GoRoutine(cfg, locationOf(ctx), (Call) call);
		block.addNode(routine);
		storeIds(routine);
		return Triple.of(routine, block, routine);
	}

	private void storeIds(Statement node) {
		cfg.registerScoping(node, visibleIds);
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
			e.printStackTrace();
		}
		GoParser parser = new GoParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.type_();

		Type t = visitType_((Type_Context) tree);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return t == null ? Untyped.INSTANCE : t;
	}

	@Override
	public Type visitType_(Type_Context ctx) {
		return new GoTypeVisitor(file, currentUnit, program, constants, globals).visitType_(ctx);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitTypeSwitchStmt(
			TypeSwitchStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		SourceCodeLocation location = locationOf(ctx);
		Expression typeSwitchExp = visitPrimaryExpr(ctx.typeSwitchGuard().primaryExpr());

		NoOp exitNode = new NoOp(cfg, location);
		block.addNode(exitNode);
		storeIds(exitNode);

		Statement entryNode = null;
		Statement previousGuard = null;
		Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> defaultBlock = null;

		int ncases = ctx.typeCaseClause().size();
		List<TypeSwitchCase> scases = new ArrayList<>(ncases);
		DefaultSwitchCase def = null;

		for (int i = 0; i < ncases; i++) {
			TypeCaseClauseContext typeSwitchCase = ctx.typeCaseClause(i);
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> caseBlock = visitStatementList(typeSwitchCase.statementList());
			block.mergeWith(caseBlock.getMiddle());

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
							new SourceCodeLocation(typeLoc.getSourceFile(), typeLoc.getLine(), typeLoc.getCol()), ids,
							new GoTypeAssertion(cfg, typeLoc, typeSwitchExp, types[j]), blockList,
							getContainingBlock());

					for (VariableRef id : ids)
						if (!GoLangUtils.refersToBlankIdentifier(id))
							blockList.getLast().addVarDeclaration(id, DeclarationType.MULTI_SHORT_VARIABLE);

					caseBooleanGuard = new GoEqual(cfg, typeLoc,
							new VariableRef(cfg, typeLoc, "ok"),
							new GoBoolean(cfg, typeLoc, true));

					block.addNode(shortDecl);
					storeIds(shortDecl);
					block.addNode(caseBooleanGuard);
					storeIds(caseBooleanGuard);
					addEdge(new SequentialEdge(shortDecl, caseBooleanGuard), block);

					addEdge(new TrueEdge(caseBooleanGuard, caseBlock.getLeft()), block);
					addEdge(new SequentialEdge(caseBlock.getRight(), exitNode), block);

					if (entryNode == null)
						entryNode = shortDecl;
					else
						addEdge(new FalseEdge(previousGuard, shortDecl), block);

					previousGuard = caseBooleanGuard;
					scases.add(new TypeSwitchCase(shortDecl, caseBooleanGuard, caseBlock.getMiddle().getNodes()));
				}

			} else {
				defaultBlock = caseBlock;
				def = new DefaultSwitchCase(caseBlock.getLeft(), caseBlock.getMiddle().getNodes());
			}
		}

		if (defaultBlock != null) {
			addEdge(new FalseEdge(previousGuard, defaultBlock.getLeft()), block);
			addEdge(new SequentialEdge(defaultBlock.getRight(), exitNode), block);
		} else
			addEdge(new FalseEdge(previousGuard, exitNode), block);

		if (ctx.simpleStmt() != null) {
			Triple<Statement, NodeList<CFG, Statement, Edge>,
					Statement> simpleStmt = visitSimpleStmt(ctx.simpleStmt());
			block.mergeWith(simpleStmt.getMiddle());
			addEdge(new SequentialEdge(simpleStmt.getRight(), entryNode), block);
			entryNode = simpleStmt.getLeft();
		}

		cfs.add(new TypeSwitch(matrix, entryNode, exitNode, scases.toArray(TypeSwitchCase[]::new), def));
		return Triple.of(entryNode, block, exitNode);
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
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitSelectStmt(SelectStmtContext ctx) {
		NodeList<CFG, Statement, Edge> block = new NodeList<>(SEQUENTIAL_SINGLETON);

		NoOp entry = new NoOp(cfg, locationOf(ctx.L_CURLY()));
		NoOp exit = new NoOp(cfg, locationOf(ctx.R_CURLY()));
		block.addNode(exit);
		block.addNode(entry);

		for (CommClauseContext clause : ctx.commClause()) {
			Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> inner = visitCommClause(clause);
			block.mergeWith(inner.getMiddle());
			addEdge(new SequentialEdge(entry, inner.getLeft()), block);
			addEdge(new SequentialEdge(inner.getRight(), exit), block);
		}

		return Triple.of(entry, block, exit);
	}

	@Override
	public Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> visitCommClause(CommClauseContext ctx) {
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