package it.unive.golisa.cli;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.antlr.GoParser.FunctionDeclContext;
import it.unive.golisa.antlr.GoParser.FunctionTypeContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.SignatureContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.GoReturn;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * An {@link GoParserBaseVisitor} that will parse the code of an Go function
 * 
 */
class GoFunctionVisitor extends GoCodeMemberVisitor {

	//side-effect su packageUnit
	protected GoFunctionVisitor(FunctionDeclContext funcDecl, CompilationUnit packageUnit, String file, Program program) {
		super(file, program);
		this.descriptor = buildCFGDescriptor(funcDecl);

		this.currentUnit = packageUnit;

		// side effects on entrypoints and matrix will affect the cfg
		cfg = new VariableScopingCFG(descriptor, entrypoints, matrix);
		initializeVisibleIds();

		packageUnit.addCFG(cfg);
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {
		Pair<Statement, Statement> result = visitBlock(ctx.block());	
		Statement functionEntryPoint = result.getLeft();
		cfg.getEntrypoints().add(functionEntryPoint);

		if (cfg.getDescriptor().getName().equals("main"))
			program.addEntryPoint(cfg);

		Type returnType = cfg.getDescriptor().getReturnType();
		if (returnType instanceof GoTypesTuple) {
			GoTypesTuple tuple = (GoTypesTuple) returnType;

			if (tuple.isNamedValues()) {
				Statement lastStmt = null;
				Statement entryNode = null;

				for (Parameter par : tuple) {
					VariableRef var = new VariableRef(cfg, par.getLocation(), par.getName());
					GoType parType = (GoType) par.getStaticType();
					GoShortVariableDeclaration decl = new GoShortVariableDeclaration(cfg, par.getLocation(), var, parType.defaultValue(cfg, (SourceCodeLocation) par.getLocation()));

					cfg.addNode(decl);

					if (lastStmt != null)
						addEdge(new SequentialEdge(lastStmt, decl));
					else
						entryNode = decl;
					lastStmt = decl;
				}
				
				addEdge(new SequentialEdge(lastStmt, result.getLeft()));
				cfg.getEntrypoints().add(entryNode);
			} else {
				cfg.getEntrypoints().add(functionEntryPoint);
			}

		} else {
			cfg.getEntrypoints().add(functionEntryPoint);
		}
		
		cfg.simplify();
		return result;
	}

	private CFGDescriptor buildCFGDescriptor(FunctionDeclContext funcDecl) {
		String funcName = funcDecl.IDENTIFIER().getText();
		SignatureContext signature = funcDecl.signature();
		ParametersContext formalPars = signature.parameters();

		int line = getLine(signature);
		int col = getCol(signature);

		Parameter[] cfgArgs = new Parameter[]{};

		for (int i = 0; i < formalPars.parameterDecl().size(); i++)
			cfgArgs = ArrayUtils.addAll(cfgArgs, visitParameterDecl(formalPars.parameterDecl(i)));

		return new CFGDescriptor(new SourceCodeLocation(file, line, col), program, false, funcName, getGoReturnType(funcDecl.signature()), cfgArgs);
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
	public GoType visitFunctionType(FunctionTypeContext ctx) {
		SignatureContext sign = ctx.signature();
		Type returnType = visitResult(sign.result()); 
		Parameter[] params = visitParameters(sign.parameters());

		return GoFunctionType.lookup(new GoFunctionType(params, returnType));
	}
	
	private void addEdge(Edge edge) {
		if (!isReturnStmt(edge.getSource()))
			cfg.addEdge(edge);
	}
	
	private boolean isReturnStmt(Statement stmt) {
		return stmt instanceof GoReturn || stmt instanceof Ret;
	}

}
