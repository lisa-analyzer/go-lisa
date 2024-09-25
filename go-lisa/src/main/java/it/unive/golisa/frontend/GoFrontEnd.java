package it.unive.golisa.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.GoFeatures;
import it.unive.golisa.GoTypeSystem;
import it.unive.golisa.antlr.GoLexer;
import it.unive.golisa.antlr.GoParser;
import it.unive.golisa.antlr.GoParser.ConstDeclContext;
import it.unive.golisa.antlr.GoParser.ConstSpecContext;
import it.unive.golisa.antlr.GoParser.DeclarationContext;
import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.ExpressionListContext;
import it.unive.golisa.antlr.GoParser.FunctionDeclContext;
import it.unive.golisa.antlr.GoParser.IdentifierListContext;
import it.unive.golisa.antlr.GoParser.ImportDeclContext;
import it.unive.golisa.antlr.GoParser.ImportPathContext;
import it.unive.golisa.antlr.GoParser.ImportSpecContext;
import it.unive.golisa.antlr.GoParser.MethodDeclContext;
import it.unive.golisa.antlr.GoParser.PackageClauseContext;
import it.unive.golisa.antlr.GoParser.SourceFileContext;
import it.unive.golisa.antlr.GoParser.String_Context;
import it.unive.golisa.antlr.GoParser.TypeDeclContext;
import it.unive.golisa.antlr.GoParser.TypeSpecContext;
import it.unive.golisa.antlr.GoParser.VarDeclContext;
import it.unive.golisa.antlr.GoParser.VarSpecContext;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.runtime.conversion.GoToString;
import it.unive.golisa.cfg.runtime.conversion.ToInt64;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoAliasType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntPrtType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntType;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.golisa.golang.util.GoLangAPISignatureMapper;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.logging.IterationLogger;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.type.Untyped;

/**
 * This class manages the translation from a Go program to the corresponding
 * LiSA {@link CFG}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFrontEnd extends GoParserBaseVisitor<Object> implements GoRuntimeLoader {

	private static final Logger log = LogManager.getLogger(GoFrontEnd.class);

	private final String filePath;

	private final Program program;

	private Map<String, ExpressionContext> constants;
	private List<Global> globals;

	private GoLangAPISignatureMapper mapper = GoLangAPISignatureMapper.getGoApiSignatures();

	private CompilationUnit packageUnit;

	/**
	 * Builds a Go frontend for a given Go program given at the location
	 * {@code filePath}.
	 * 
	 * @param filePath file path to a Go program.
	 */
	private GoFrontEnd(String filePath) {
		this.filePath = filePath;
		this.program = new Program(new GoFeatures(), new GoTypeSystem());
		this.constants = new HashMap<>();
		this.globals = new ArrayList<>();
		GoCodeMemberVisitor.c = 0;
	}

	/**
	 * Returns the parsed file path.
	 * 
	 * @return the parsed file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Processes the Go program located at {@code filePath} and returns the LiSA
	 * program corresponding to the parsed file.
	 * 
	 * @param filePath the file path
	 * 
	 * @return the LiSA program corresponding to the parsed file
	 * 
	 * @throws IOException if something wrong happens while reading the file
	 */
	public static Program processFile(String filePath) throws IOException {
		clearTypes();
		return new GoFrontEnd(filePath).toLiSAProgram();
	}

	/**
	 * Returns a {@link Program} corresponding to the Go program located to
	 * {@code filePath}.
	 * 
	 * @return a {@link Program} corresponding to the Go program located to
	 *             {@code filePath}
	 * 
	 * @throws IOException if something wrong happens while reading the file
	 */
	private Program toLiSAProgram() throws IOException {
		log.info("Go front-end setup...");
		log.info("Reading file... " + filePath);

		long start = System.currentTimeMillis();

		try (InputStream stream = new FileInputStream(getFilePath())) {
			log.info("Lines of code: " + Files.lines(Paths.get(getFilePath())).count());
	
			GoLexer lexer = new GoLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
			GoParser parser = new GoParser(new CommonTokenStream(lexer));
			parser.setErrorHandler(new BailErrorStrategy());
	
			ParseTree tree = parser.sourceFile();
			long parsingTime = System.currentTimeMillis();
	
			Program result = visitSourceFile((SourceFileContext) tree);
	
			log.info("PARSING TIME: " + (parsingTime - start) + " CFG time: " + (System.currentTimeMillis() - parsingTime));
	
			// Register all the types
			registerTypes(program);
	
			return result;
		}
	}

	private static void clearTypes() {
		GoArrayType.clearAll();
		GoStructType.clearAll();
		GoSliceType.clearAll();
		GoPointerType.clearAll();
		GoMapType.clearAll();
		GoTupleType.clearAll();
		GoChannelType.clearAll();
		GoFunctionType.clearAll();
		GoAliasType.clearAll();
		GoInterfaceType.clearAll();
	}

	private void registerTypes(Program program) {
		program.getTypes().registerType(GoBoolType.INSTANCE);
		program.getTypes().registerType(GoFloat32Type.INSTANCE);
		program.getTypes().registerType(GoFloat64Type.INSTANCE);
		program.getTypes().registerType(GoIntType.INSTANCE);
		program.getTypes().registerType(GoUIntType.INSTANCE);
		program.getTypes().registerType(GoUntypedInt.INSTANCE);
		program.getTypes().registerType(GoInt8Type.INSTANCE);
		program.getTypes().registerType(GoUInt8Type.INSTANCE);
		program.getTypes().registerType(GoInt16Type.INSTANCE);
		program.getTypes().registerType(GoUInt16Type.INSTANCE);
		program.getTypes().registerType(GoInt32Type.INSTANCE);
		program.getTypes().registerType(GoUInt32Type.INSTANCE);
		program.getTypes().registerType(GoInt64Type.INSTANCE);
		program.getTypes().registerType(GoUInt64Type.INSTANCE);
		program.getTypes().registerType(GoUIntPrtType.INSTANCE);
		program.getTypes().registerType(GoUntypedFloat.INSTANCE);
		program.getTypes().registerType(GoStringType.INSTANCE);
		program.getTypes().registerType(GoErrorType.INSTANCE);
		program.getTypes().registerType(GoNilType.INSTANCE);

		GoArrayType.all().forEach(program.getTypes()::registerType);
		GoStructType.all().forEach(program.getTypes()::registerType);
		GoSliceType.all().forEach(program.getTypes()::registerType);
		GoPointerType.all().forEach(program.getTypes()::registerType);
		GoMapType.all().forEach(program.getTypes()::registerType);
		GoTupleType.all().forEach(program.getTypes()::registerType);
		GoChannelType.all().forEach(program.getTypes()::registerType);
		GoFunctionType.all().forEach(program.getTypes()::registerType);
		GoAliasType.all().forEach(program.getTypes()::registerType);
		GoInterfaceType.all().forEach(program.getTypes()::registerType);

		// adding type tokens
		Set<Type> ttTypes = new HashSet<>();
		for (Type t : program.getTypes().getTypes())
			ttTypes.add(new TypeTokenType(Collections.singleton(t)));

		for (Type t : ttTypes)
			program.getTypes().registerType(t);
	}

	@Override
	public Program visitSourceFile(SourceFileContext ctx) {
		String packageName = visitPackageClause(ctx.packageClause());

		packageUnit = new ClassUnit(new SourceCodeLocation(filePath, 0, 0), program, packageName, false);
		program.addUnit(packageUnit);

		GoInterfaceType.lookup("EMPTY_INTERFACE", packageUnit, program);

		for (ImportDeclContext imp : ctx.importDecl())
			visitImportDecl(imp);

		loadCore();

		for (DeclarationContext decl : IterationLogger.iterate(log, ctx.declaration(), "Parsing global declarations...",
				"Global declarations"))
			visitDeclarationContext(decl);

		updateGlobals();
		updateUnitReferences();

		for (MethodDeclContext decl : IterationLogger.iterate(log, ctx.methodDecl(), "Parsing method declarations...",
				"Method declarations"))
			visitMethodDecl(decl);

		// method declaration must be linked to compilation unit of a
		// declaration context, for the function declaration is not needed
		// Visit of each FunctionDeclContext populating the corresponding cfg
		for (FunctionDeclContext funcDecl : IterationLogger.iterate(log, ctx.functionDecl(),
				"Visiting function declarations...", "Function declarations"))
			visitFunctionDecl(funcDecl);

		return program;
	}

	private void updateGlobals() {
		globals.forEach(g -> program.addGlobal(g));
	}

	private void updateUnitReferences() {
		for (Unit unit : program.getUnits())
			GoStructType.updateReference(unit.getName(), unit);
	}

	private void visitDeclarationContext(DeclarationContext decl) {
		if (decl.typeDecl() != null)
			for (CompilationUnit unit : visitTypeDecl(decl.typeDecl()))
				program.addUnit(unit);

		if (decl.varDecl() != null)
			visitGlobals(decl.varDecl());

		if (decl.constDecl() != null)
			visitConstDeclContext(decl.constDecl());

	}

	private void visitGlobals(VarDeclContext ctx) {
		for (VarSpecContext spec : ctx.varSpec()) {
			IdentifierListContext ids = spec.identifierList();
			for (int i = 0; i < ids.IDENTIFIER().size(); i++) {
				Type type = spec.type_() == null ? Untyped.INSTANCE
						: new GoTypeVisitor(filePath, packageUnit, packageUnit, program, constants, globals)
								.visitType_(spec.type_());
				globals.add(new Global(
						new SourceCodeLocation(filePath, GoCodeMemberVisitor.getLine(ids.IDENTIFIER(i)),
								GoCodeMemberVisitor.getCol(ids.IDENTIFIER(i))),
						packageUnit, ids.IDENTIFIER(i).getText(),
						false, type));
			}
		}
	}

	private void visitConstDeclContext(ConstDeclContext ctx) {
		for (ConstSpecContext constSpec : ctx.constSpec())
			visitConstSpecContext(constSpec);
	}

	private void visitConstSpecContext(ConstSpecContext ctx) {
		IdentifierListContext ids = ctx.identifierList();
		ExpressionListContext exps = ctx.expressionList();

		for (int i = 0; i < ids.IDENTIFIER().size(); i++)
			if (exps != null && exps.expression(i) != null)
				constants.put(ids.IDENTIFIER(i).getText(), exps.expression(i));
	}

	@Override
	public Collection<CompilationUnit> visitTypeDecl(TypeDeclContext ctx) {
		Set<CompilationUnit> units = new HashSet<>();
		for (TypeSpecContext typeSpec : ctx.typeSpec()) {
			String unitName = typeSpec.IDENTIFIER().getText();
			if (typeSpec.type_().typeLit() == null || typeSpec.type_().typeLit().interfaceType() == null) {
				ClassUnit unit = new ClassUnit(

						new SourceCodeLocation(filePath, GoCodeMemberVisitor.getLine(typeSpec),
								GoCodeMemberVisitor.getCol(typeSpec)),
						program,
						unitName, false);
				units.add(unit);
				new GoTypeVisitor(filePath, unit, packageUnit, program, constants, globals).visitTypeSpec(typeSpec);
			} else {
				InterfaceUnit unit = new InterfaceUnit(

						new SourceCodeLocation(filePath, GoCodeMemberVisitor.getLine(typeSpec),
								GoCodeMemberVisitor.getCol(typeSpec)),
						program,
						unitName, false);
				units.add(unit);
				new GoTypeVisitor(filePath, unit, packageUnit, program, constants, globals).visitTypeSpec(typeSpec);
			}
		}
		return units;
	}

	@Override
	public String visitPackageClause(PackageClauseContext ctx) {
		return ctx.IDENTIFIER().getText();
	}

	@Override
	public Statement visitImportDecl(ImportDeclContext ctx) {
		for (ImportSpecContext impSpec : ctx.importSpec())
			visitImportSpec(impSpec);
		return null;
	}

	@Override
	public Statement visitImportSpec(ImportSpecContext ctx) {
		return visitImportPath(ctx.importPath());
	}

	@Override
	public String visitString_(String_Context ctx) {
		return ctx.getText().substring(1, ctx.getText().length() - 1);
	}

	@Override
	public Statement visitImportPath(ImportPathContext ctx) {
		String module = visitString_(ctx.string_());
		File moduleDirectory = new File(new File(filePath).getParent(), module);

		if (moduleDirectory.exists() && moduleDirectory.isDirectory()) {
			File[] listOfFiles = moduleDirectory.listFiles();

			for (int i = 0; i < listOfFiles.length; i++)
				if (listOfFiles[i].getName().endsWith(".go"))
					try {
						Program moduleProgram = GoFrontEnd.processFile(listOfFiles[i].toString());

						for (Unit cu : moduleProgram.getUnits())
							program.addUnit(cu);

					} catch (IOException e) {
						throw new RuntimeException("Cannot find package + " + listOfFiles[i]);
					}
		} else
			loadRuntime(module, program, mapper);
		return null;
	}

	private void loadCore() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		program.addCodeMember(new GoToString(unknownLocation, program));
		program.addCodeMember(new ToInt64(unknownLocation, program));
	}

	@Override
	public Pair<Statement, Statement> visitFunctionDecl(FunctionDeclContext ctx) {
		return new GoFunctionVisitor(ctx, packageUnit, filePath, program, constants, globals).visitFunctionDecl(ctx);
	}

	@Override
	public CFG visitMethodDecl(MethodDeclContext ctx) {
		return new GoCodeMemberVisitor(packageUnit, packageUnit, ctx, filePath, program, constants, globals)
				.visitCodeMember(ctx);
	}
}