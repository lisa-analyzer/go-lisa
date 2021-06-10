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
import it.unive.golisa.antlr.GoParser.DeclarationContext;
import it.unive.golisa.antlr.GoParser.FunctionDeclContext;
import it.unive.golisa.antlr.GoParser.ImportDeclContext;
import it.unive.golisa.antlr.GoParser.ImportPathContext;
import it.unive.golisa.antlr.GoParser.ImportSpecContext;
import it.unive.golisa.antlr.GoParser.MethodDeclContext;
import it.unive.golisa.antlr.GoParser.PackageClauseContext;
import it.unive.golisa.antlr.GoParser.SourceFileContext;
import it.unive.golisa.antlr.GoParser.String_Context;
import it.unive.golisa.antlr.GoParser.TypeDeclContext;
import it.unive.golisa.antlr.GoParser.TypeSpecContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.expression.runtime.fmt.GoPrintln;
import it.unive.golisa.cfg.expression.runtime.strings.GoContains;
import it.unive.golisa.cfg.expression.runtime.strings.GoHasPrefix;
import it.unive.golisa.cfg.expression.runtime.strings.GoHasSuffix;
import it.unive.golisa.cfg.expression.runtime.strings.GoIndexOf;
import it.unive.golisa.cfg.expression.runtime.strings.GoReplace;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.lisa.logging.IterationLogger;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnresolvedCall.ResolutionStrategy;

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
	 * The resolution strategy for Go calling expressions.
	 */
	public static final ResolutionStrategy CALL_STRATEGY = ResolutionStrategy.DYNAMIC_TYPES;

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
		String packageName = visitPackageClause(ctx.packageClause());

		packageUnit = new CompilationUnit(new SourceCodeLocation(filePath, 0, 0), packageName, false);
		program.addCompilationUnit(packageUnit);

		GoInterfaceType.lookup("EMPTY_INTERFACE", packageUnit);

		for (ImportDeclContext imp : ctx.importDecl())
			visitImportDecl(imp);

		for (DeclarationContext decl : IterationLogger.iterate(log, ctx.declaration(), "Parsing global declarations...", "Global declarations")) 
			visitDeclarationContext(decl);

		for (MethodDeclContext decl : IterationLogger.iterate(log, ctx.methodDecl(), "Parsing method declarations...", "Method declarations"))
			visitMethodDecl(decl); 

		updateUnitReferences();

		// method declaration must be linked to compilation unit of a declaration context, for the function declaration is not needed
		// Visit of each FunctionDeclContext populating the corresponding cfg
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(), "Visiting function declarations...", "Function declarations"))	
			visitFunctionDecl(funcDecl);

		return program;
	}

	private void updateUnitReferences() {
		for (CompilationUnit unit : program.getUnits())
			GoStructType.updateReference(unit.getName(), unit);
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
			new GoTypeVisitor(filePath, unit).visitTypeSpec(typeSpec);
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
		for (ImportSpecContext impSpec: ctx.importSpec())
			visitImportSpec(impSpec);
		return null;
	}

	@Override
	public Statement visitImportSpec(ImportSpecContext ctx) {
		return visitImportPath(ctx.importPath());
	}

	@Override
	public String visitString_(String_Context ctx) {
		return ctx.getText().substring(1, ctx.getText().length() -1);
	}

	@Override
	public Statement visitImportPath(ImportPathContext ctx) {
		String lib = visitString_(ctx.string_());

		switch (lib) {
		case "strings": loadStrings();
		case "fmt": loadFmt();
		}
		return null;
	}

	private void loadStrings() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation("go-runtime", 0, 0);
		CompilationUnit str = new CompilationUnit(unknownLocation, "strings", false);
		str.addConstruct(new GoHasPrefix(unknownLocation, str));
		str.addConstruct(new GoHasSuffix(unknownLocation, str));
		str.addConstruct(new GoContains(unknownLocation, str));
		str.addConstruct(new GoReplace(unknownLocation, str));
		str.addConstruct(new GoIndexOf(unknownLocation, str));	

		program.addCompilationUnit(str);

		// We add the string methods also in package unit as non-instant cfgs
		packageUnit.addConstruct(new GoHasPrefix(unknownLocation, str));
		packageUnit.addConstruct(new GoHasSuffix(unknownLocation, str));
		packageUnit.addConstruct(new GoContains(unknownLocation, str));
		packageUnit.addConstruct(new GoReplace(unknownLocation, str));
		packageUnit.addConstruct(new GoIndexOf(unknownLocation, str));
	}

	private void loadFmt() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation("go-runtime", 0, 0);
		CompilationUnit fmt = new CompilationUnit(unknownLocation, "fmt", false);
		fmt.addConstruct(new GoPrintln(unknownLocation, fmt));

		program.addCompilationUnit(fmt);

		// We add the string methods also in package unit as non-instant cfgs
		packageUnit.addConstruct(new GoPrintln(unknownLocation, fmt));
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {	
		return new GoFunctionVisitor(ctx, packageUnit, filePath, program).visitFunctionDecl(ctx);
	}

	@Override
	public CFG visitMethodDecl(MethodDeclContext ctx) {
		return new GoCodeMemberVisitor(packageUnit, ctx, filePath, program).visitCodeMember(ctx);
	}

}