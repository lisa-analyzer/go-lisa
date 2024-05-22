package it.unive.golisa.frontend;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.FunctionDeclContext;
import it.unive.golisa.antlr.GoParser.FunctionLitContext;
import it.unive.golisa.antlr.GoParser.FunctionTypeContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.SignatureContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

/**
 * An {@link GoParserBaseVisitor} that will parse the code of an Go function.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
class GoFunctionVisitor extends GoCodeMemberVisitor {

	/**
	 * Builds a function visitor (side-effects on packageUnit).
	 * 
	 * @param funcDecl    the function declaration context to visit
	 * @param packageUnit the current unit
	 * @param file        the file path
	 * @param program     the current program
	 * @param constants   the constant mapping
	 * @param globals     the global variables
	 */
	protected GoFunctionVisitor(FunctionDeclContext funcDecl, CompilationUnit packageUnit, String file, Program program,
			Map<String, ExpressionContext> constants, List<Global> globals) {
		super(packageUnit, packageUnit, file, program, constants, globals);
		this.currentUnit = packageUnit;

		// side effects on entrypoints and matrix will affect the cfg
		cfg = new VariableScopingCFG(buildCodeMemberDescriptor(funcDecl, packageUnit), entrypoints,
				new NodeList<>(SEQUENTIAL_SINGLETON));
		initializeVisibleIds();

		packageUnit.addCodeMember(cfg);
	}

	/**
	 * Builds a function visitor (side-effects on packageUnit).
	 * 
	 * @param funcLit     the function literal context to visit
	 * @param packageUnit the current unit
	 * @param file        the file path
	 * @param program     the current program
	 * @param constants   the constant mapping
	 * @param globals     the global variables
	 */
	protected GoFunctionVisitor(FunctionLitContext funcLit, CompilationUnit packageUnit, String file, Program program,
			Map<String, ExpressionContext> constants, List<Global> globals) {
		super(packageUnit, packageUnit, file, program, constants, globals);
		this.currentUnit = packageUnit;

		// side effects on entrypoints and matrix will affect the cfg
		cfg = new VariableScopingCFG(buildCodeMemberDescriptor(funcLit), entrypoints,
				new NodeList<>(GoCodeMemberVisitor.SEQUENTIAL_SINGLETON));
		initializeVisibleIds();

		packageUnit.addCodeMember(cfg);
	}

	/**
	 * Builds the function visitor.
	 * 
	 * @param unit      the current unit
	 * @param pkgUnit   the package unit
	 * @param file      the current file path
	 * @param program   the current program
	 * @param constants the constant mapping
	 * @param globals   the global variables
	 */
	public GoFunctionVisitor(CompilationUnit unit, Unit pkgUnit, String file, Program program,
			Map<String, ExpressionContext> constants, List<Global> globals) {
		super(unit, pkgUnit, file, program, constants, globals);
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {

		Statement entryNode = null;
		Triple<Statement, NodeList<CFG, Statement, Edge>,
				Statement> body = visitMethodBlock(ctx.block());

		processGotos();

		// The function named "main" is the entry point of the program
		if (cfg.getDescriptor().getName().equals("main"))
			program.addEntryPoint(cfg);

		Type returnType = cfg.getDescriptor().getReturnType();
		if (returnType.isInMemoryType())
			returnType = GoPointerType.lookup(returnType);

		NodeList<CFG, Statement, Edge> matrix = cfg.getNodeList();
		entryNode = findEntryNode(entryNode, body, returnType, matrix);

		cfg.getEntrypoints().add(entryNode);

		addReturnStatement(matrix);
		addFinalRet(matrix);

		cfg.simplify();
		return Pair.of(entryNode, body.getRight());
	}

	private Statement findEntryNode(Statement entryNode,
			Triple<Statement, NodeList<CFG, Statement, Edge>, Statement> body, Type returnType,
			NodeList<CFG, Statement, Edge> matrix) {
		if (!(returnType instanceof GoTupleType))
			entryNode = body.getLeft();
		else {
			GoTupleType tuple = (GoTupleType) returnType;
			if (tuple.isNamedValues()) {
				Statement lastStmt = null;

				for (Parameter par : tuple) {
					VariableRef var = new VariableRef(cfg, par.getLocation(), par.getName());
					Type parType = par.getStaticType();
					Expression decl = new GoShortVariableDeclaration(cfg, par.getLocation(), var,
							parType.defaultValue(cfg, (SourceCodeLocation) par.getLocation()));

					cfg.addNode(decl);

					if (lastStmt != null)
						addEdge(new SequentialEdge(lastStmt, decl), matrix);
					else
						entryNode = decl;
					lastStmt = decl;
				}

				addEdge(new SequentialEdge(lastStmt, body.getLeft()), matrix);
				cfg.getEntrypoints().add(entryNode);

			} else
				entryNode = body.getLeft();
		}
		return entryNode;
	}

	/**
	 * Builds a {@link CFG} for an anonymous function.
	 * 
	 * @param ctx the function literal context to visit
	 * 
	 * @return a {@link CFG} for an anonymous function
	 */
	protected CFG buildAnonymousCFG(FunctionLitContext ctx) {
		Statement entryNode = null;
		Triple<Statement, NodeList<CFG, Statement, Edge>,
				Statement> body = visitMethodBlock(ctx.block());

		processGotos();

		Type returnType = cfg.getDescriptor().getReturnType();

		NodeList<CFG, Statement, Edge> matrix = cfg.getNodeList();
		entryNode = findEntryNode(entryNode, body, returnType, matrix);

		cfg.getEntrypoints().add(entryNode);

		addReturnStatement(matrix);
		addFinalRet(matrix);

		cfg.simplify();
		return cfg;
	}

	private CodeMemberDescriptor buildCodeMemberDescriptor(FunctionDeclContext funcDecl, Unit unit) {
		String funcName = funcDecl.IDENTIFIER().getText();
		SignatureContext signature = funcDecl.signature();
		ParametersContext formalPars = signature.parameters();

		int line = getLine(signature);
		int col = getCol(signature);

		Parameter[] cfgArgs = new Parameter[] {};

		for (int i = 0; i < formalPars.parameterDecl().size(); i++)
			cfgArgs = ArrayUtils.addAll(cfgArgs, visitParameterDecl(formalPars.parameterDecl(i)));

		Type returnType = getGoReturnType(funcDecl.signature());
		if (returnType.isInMemoryType())
			returnType = GoPointerType.lookup(returnType);
		CodeMemberDescriptor descriptor = new CodeMemberDescriptor(new SourceCodeLocation(file, line, col), unit, false,
				funcName,
				returnType, cfgArgs);

		return descriptor;
	}

	private CodeMemberDescriptor buildCodeMemberDescriptor(FunctionLitContext funcLit) {
		String funcName = "anonymousFunction" + c++;
		SignatureContext signature = funcLit.signature();
		ParametersContext formalPars = signature.parameters();

		int line = getLine(signature);
		int col = getCol(signature);

		Parameter[] cfgArgs = new Parameter[] {};

		for (int i = 0; i < formalPars.parameterDecl().size(); i++)
			cfgArgs = ArrayUtils.addAll(cfgArgs, visitParameterDecl(formalPars.parameterDecl(i)));

		return new CodeMemberDescriptor(new SourceCodeLocation(file, line, col), currentUnit, false, funcName,
				getGoReturnType(funcLit.signature()), cfgArgs);
	}

	/**
	 * Given a signature context, returns the Go type corresponding to the
	 * return type of the signature. If the result type is missing (i.e. null)
	 * then the {@link Untyped} typed is returned.
	 * 
	 * @param signature the signature context
	 * 
	 * @return the Go type corresponding to the return type of {@code signature}
	 */
	private Type getGoReturnType(SignatureContext signature) {
		// The return type is not specified
		if (signature.result() == null)
			return Untyped.INSTANCE;
		return new GoCodeMemberVisitor(currentUnit, pkgUnit, file, program, constants, globals)
				.visitResult(signature.result());
	}

	/**
	 * Visits the return type of a function.
	 * 
	 * @param ctx the function type context to visit
	 * 
	 * @return the return type
	 */
	public Type visitFunctionType(FunctionTypeContext ctx) {
		SignatureContext sign = ctx.signature();
		Type returnType = getGoReturnType(sign);
		if (returnType.isInMemoryType())
			returnType = GoPointerType.lookup(returnType);
		Parameter[] params = visitParameters(sign.parameters());
		return GoFunctionType.lookup(returnType, params);
	}
}
