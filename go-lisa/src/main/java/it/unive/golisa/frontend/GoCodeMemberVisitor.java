package it.unive.golisa.frontend;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import it.unive.golisa.antlr.GoParser.BlockContext;
import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.MethodDeclContext;
import it.unive.golisa.antlr.GoParser.ParameterDeclContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.QualifiedIdentContext;
import it.unive.golisa.antlr.GoParser.ReceiverContext;
import it.unive.golisa.antlr.GoParser.ResultContext;
import it.unive.golisa.antlr.GoParser.Type_Context;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.composite.GoVariadicType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;

/**
 * A {@link GoParserBaseVisitor} that will parse the code of an Go method
 */
public class GoCodeMemberVisitor {

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

		Triple<Statement, AdjacencyMatrix<Statement, Edge, CFG>,
				Statement> body = new BaseCodeVisitor(file, program, constants, currentUnit, cfg, cfg.getDescriptor(),
						cfg.getAdjacencyMatrix()).visitMethodBlock(ctx.block());

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

	public Parameter visitReceiver(ReceiverContext ctx) {
		Parameter[] params = visitParameters(ctx.parameters());
		if (params.length != 1)
			throw new IllegalStateException("Go receiver must be a single parameter");

		return params[0];
	}

	public Parameter[] visitParameters(ParametersContext ctx) {
		Parameter[] result = new Parameter[] {};
		for (int i = 0; i < ctx.parameterDecl().size(); i++)
			result = ArrayUtils.addAll(result, visitParameterDecl(ctx.parameterDecl(i)));

		return result;
	}

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

	public Pair<String, String> visitQualifiedIdent(QualifiedIdentContext ctx) {
		return Pair.of(ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
	}

	public Type visitResult(ResultContext ctx) {
		if (ctx == null)
			return Untyped.INSTANCE;
		if (ctx.type_() != null)
			return visitType_(ctx.type_());
		else {
			return new GoTypesTuple(visitParameters(ctx.parameters()));
		}
	}

	public Type visitType_(Type_Context ctx) {
		return new GoTypeVisitor(file, currentUnit, program, constants).visitType_(ctx);
	}
}
