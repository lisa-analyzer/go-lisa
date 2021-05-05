package it.unive.golisa.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParser.*;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.lisa.logging.IterationLogger;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;

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

	private final Program program;


	/**
	 * Builds an instance of @GoToCFG for a given Go program
	 * given at the location filePath.
	 *  
	 * @param filePath file path to a Go program.
	 */
	private GoFrontEnd(String filePath) {
		this.filePath = filePath;
		this.program = new Program();
	}

	/**
	 * Returns the parsed file path.
	 * @return the parsed file path
	 */
	public String getFilePath() {
		return filePath;
	}

	private SourceFileContext source;

	public static Program processFile(String filePath) throws IOException {
		return new GoFrontEnd(filePath).toLiSAProgram();
	}

	/**
	 * Returns the collection of @CFG in a Go program at filePath.
	 * 
	 * @return collection of @CFG in file
	 * @throws IOException if {@code stream} to file cannot be written to or closed
	 */
	private Program toLiSAProgram() throws IOException {
		log.info("Go front-end setup...");
		log.info("Reading file... " + filePath);

		InputStream stream = new FileInputStream(getFilePath());
		GoLexer lexer = new GoLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		GoParser parser = new GoParser(new CommonTokenStream(lexer));
		parser.setErrorHandler(new BailErrorStrategy());

		ParseTree tree = parser.sourceFile();

		Program result = visitSourceFile((SourceFileContext) tree);
		stream.close();

		return result;
	}

	CompilationUnit packageUnit;
	@Override
	public Program visitSourceFile(SourceFileContext ctx) {
		source = ctx;
		String packageName = visitPackageClause(ctx.packageClause());

		packageUnit = new CompilationUnit(new SourceCodeLocation(filePath, 0, 0), packageName, false);
		program.addCompilationUnit(packageUnit);

		GoInterfaceType.lookup("EMPTY_INTERFACE", packageUnit);

		for (DeclarationContext decl : IterationLogger.iterate(log, ctx.declaration(), "Parsing global declarations...", "Global declarations")) 
			visitDeclarationContext(decl);

		for (MethodDeclContext decl : IterationLogger.iterate(log, ctx.methodDecl(), "Parsing method declarations...", "Method declarations"))
			visitMethodDecl(decl); 
		
		// method declaration must be linked to compilation unit of a declaration context, for the function declaration is not needed
		// Visit of each FunctionDeclContext populating the corresponding cfg
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Visiting function declarations...", "Function declarations"))	
			visitFunctionDecl(funcDecl);
		

		return program;
	}

	private void visitDeclarationContext(DeclarationContext decl) {
		if (decl.typeDecl() != null) 
			for (CompilationUnit unit : visitTypeDecl(decl.typeDecl()))
				program.addCompilationUnit(unit);
		
	}
	
	@Override
	public Collection<CompilationUnit> visitTypeDecl(TypeDeclContext ctx) {
		HashSet<CompilationUnit> units = new HashSet<>();
		for (TypeSpecContext typeSpec : ctx.typeSpec()) {
			String unitName = typeSpec.IDENTIFIER().getText();
			CompilationUnit unit = new CompilationUnit(new SourceCodeLocation(filePath, getLine(typeSpec), getCol(typeSpec)), unitName, false);
			units.add(unit);
			visitTypeSpec(typeSpec);
		}
		return units;
	}


	private int getLine(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	} 

	
	private int getCol(ParserRuleContext ctx) {
		return ctx.getStop().getCharPositionInLine();
	} 

	
	@Override
	public String visitPackageClause(PackageClauseContext ctx) {
		return ctx.IDENTIFIER().getText();
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

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {	
		return new GoFunctionVisitor(ctx, packageUnit, filePath, program, source).visitFunctionDecl(ctx);
	}

	public CFG visitMethodDeclaration(MethodDeclContext ctx) {
		return new GoCodeMemberVisitor(packageUnit, ctx, filePath,program, source).visitCodeMember(ctx);
	}
}