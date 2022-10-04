package it.unive.golisa.frontend;

import it.unive.golisa.antlr.GoParser.AnonymousFieldContext;
import it.unive.golisa.antlr.GoParser.ArrayLengthContext;
import it.unive.golisa.antlr.GoParser.ArrayTypeContext;
import it.unive.golisa.antlr.GoParser.ChannelTypeContext;
import it.unive.golisa.antlr.GoParser.ElementTypeContext;
import it.unive.golisa.antlr.GoParser.ExpressionContext;
import it.unive.golisa.antlr.GoParser.FieldDeclContext;
import it.unive.golisa.antlr.GoParser.FunctionTypeContext;
import it.unive.golisa.antlr.GoParser.InterfaceTypeContext;
import it.unive.golisa.antlr.GoParser.LiteralTypeContext;
import it.unive.golisa.antlr.GoParser.MapTypeContext;
import it.unive.golisa.antlr.GoParser.MethodSpecContext;
import it.unive.golisa.antlr.GoParser.ParametersContext;
import it.unive.golisa.antlr.GoParser.PointerTypeContext;
import it.unive.golisa.antlr.GoParser.QualifiedIdentContext;
import it.unive.golisa.antlr.GoParser.ReceiverTypeContext;
import it.unive.golisa.antlr.GoParser.ResultContext;
import it.unive.golisa.antlr.GoParser.SliceTypeContext;
import it.unive.golisa.antlr.GoParser.StructTypeContext;
import it.unive.golisa.antlr.GoParser.TypeLitContext;
import it.unive.golisa.antlr.GoParser.TypeNameContext;
import it.unive.golisa.antlr.GoParser.Type_Context;
import it.unive.golisa.antlr.GoParserBaseVisitor;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoAliasType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
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
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntPrtType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * An {@link GoParserBaseVisitor} managing type parsing.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTypeVisitor extends GoParserBaseVisitor<Object> {

	private final String file;

	private final CompilationUnit unit;

	private final Program program;

	private final Map<String, ExpressionContext> constants;
	
	private final List<Global> globals;

	/**
	 * Builds a type visitor.
	 * 
	 * @param file      the file path
	 * @param unit      the current unit
	 * @param program   the current program
	 * @param constants the constant mapping
	 * @param globals 
	 */
	public GoTypeVisitor(String file, CompilationUnit unit, Program program, Map<String, ExpressionContext> constants, List<Global> globals) {
		this.file = file;
		this.unit = unit;
		this.program = program;
		this.constants = constants;
		this.globals = globals;
	}

	/**
	 * Given a type context {@code ctx}, yields the corresponding Go type.
	 * 
	 * @param ctx the type context
	 * 
	 * @return the Go type corresponding to {@code ctx}
	 */
	private Type getGoType(TypeNameContext ctx) {

		if (ctx.IDENTIFIER() != null) {

			String type = ctx.IDENTIFIER().getText();
			switch (type) {
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
			case "uintprt":
				return GoUIntPrtType.INSTANCE;
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
				else if (GoInterfaceType.hasInterfaceType(type))
					return GoInterfaceType.get(type);
				else {
					CompilationUnit unit = new CompilationUnit(new SourceCodeLocation(file, 0, 0), type, false);
					return GoStructType.lookup(type, unit);
				}
			}
		}

		return visitQualifiedIdent(ctx.qualifiedIdent());
	}

	@Override
	public Type visitType_(Type_Context ctx) {
		if (ctx.typeName() != null) {
			return visitTypeName(ctx.typeName());
		}
		if (ctx.typeLit() != null)
			return visitTypeLit(ctx.typeLit());

		if (ctx.type_() != null)
			return visitType_(ctx.type_());

		throw new IllegalStateException("Illegal state: type_ rule has no other productions.");
	}

	@Override
	public Type visitTypeLit(TypeLitContext ctx) {

		if (ctx.arrayType() != null)
			return visitArrayType(ctx.arrayType());

		if (ctx.structType() != null)
			return visitStructType(ctx.structType());

		if (ctx.pointerType() != null)
			return visitPointerType(ctx.pointerType());

		if (ctx.functionType() != null)
			return visitFunctionType(ctx.functionType());

		if (ctx.sliceType() != null)
			return visitSliceType(ctx.sliceType());

		if (ctx.mapType() != null)
			return visitMapType(ctx.mapType());

		if (ctx.channelType() != null)
			return visitChannelType(ctx.channelType());

		Object result = visitChildren(ctx);
		if (!(result instanceof GoType) && !(result instanceof Untyped))
			throw new IllegalStateException("Type expected: " + result + " " + ctx.getText());
		else
			return (Type) result;
	}

	@Override
	public Type visitTypeName(TypeNameContext ctx) {
		if (ctx.IDENTIFIER() != null)
			return getGoType(ctx);
		else
			return visitQualifiedIdent(ctx.qualifiedIdent());
	}

	@Override
	public GoType visitFunctionType(FunctionTypeContext ctx) {
		return new GoFunctionVisitor(unit, file, program, constants, globals).visitFunctionType(ctx);
	}

	@Override
	public Type visitQualifiedIdent(QualifiedIdentContext ctx) {
		for (Type type : program.getRegisteredTypes())
			if (type.toString().equals(ctx.getText()))
				return (GoType) type;

		return Untyped.INSTANCE;
	}

	@Override
	public Type visitArrayType(ArrayTypeContext ctx) {
		Type contentType = visitElementType(ctx.elementType());
		Integer length = visitArrayLength(ctx.arrayLength());
		return GoArrayType.lookup(contentType, length);
	}

	@Override
	public Integer visitArrayLength(ArrayLengthContext ctx) {
		// The array length must be an integer value or a constant expression

		Integer length = getConstantValue(ctx.expression());
		if (length != null)
			return length;

		throw new IllegalStateException("Go error: non-constant array bound " + ctx.getText());
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private Integer getConstantValue(ExpressionContext expression) {

		if (isInteger(expression.getText()))
			return Integer.parseInt(expression.getText());

		if (expression.STAR() != null) {
			Integer left = getConstantValue(expression.expression(0));
			Integer right = getConstantValue(expression.expression(1));
			if (left != null && right != null)
				return left * right;
		}

		if (expression.DIV() != null) {
			Integer left = getConstantValue(expression.expression(0));
			Integer right = getConstantValue(expression.expression(1));
			if (left != null && right != null)
				return left / right;
		}

		if (expression.MINUS() != null) {
			Integer left = getConstantValue(expression.expression(0));
			Integer right = getConstantValue(expression.expression(1));
			if (left != null && right != null)
				return left - right;
		}

		if (expression.PLUS() != null) {
			Integer left = getConstantValue(expression.expression(0));
			Integer right = getConstantValue(expression.expression(1));
			if (left != null && right != null)
				return left + right;
		}

		if (expression.primaryExpr().operand().operandName().IDENTIFIER() != null)
			if (constants.containsKey(expression.getText()))
				return getConstantValue(constants.get(expression.getText()));

		return null;
	}

	@Override
	public GoType visitSliceType(SliceTypeContext ctx) {
		Type contentType = visitElementType(ctx.elementType());
		contentType = contentType == null ? Untyped.INSTANCE : contentType;
		return GoSliceType.lookup(contentType);
	}

	@Override
	public GoType visitMapType(MapTypeContext ctx) {
		Type keyType = visitType_(ctx.type_());
		keyType = keyType == null ? Untyped.INSTANCE : keyType;

		Type elementType = visitElementType(ctx.elementType());
		elementType = elementType == null ? Untyped.INSTANCE : elementType;

		return GoMapType.lookup(keyType, elementType);
	}

	@Override
	public GoType visitChannelType(ChannelTypeContext ctx) {
		Type contentType = visitElementType(ctx.elementType());
		contentType = contentType == null ? Untyped.INSTANCE : contentType;

		if (ctx.RECEIVE() == null)
			return GoChannelType.lookup(contentType);
		else if (GoCodeMemberVisitor.getCol(ctx.CHAN()) < GoCodeMemberVisitor.getCol(ctx.RECEIVE()))
			return GoChannelType.lookup(new GoChannelType(contentType, true, false));

		return GoChannelType.lookup(new GoChannelType(contentType, false, true));
	}

	@Override
	public Type visitElementType(ElementTypeContext ctx) {
		return visitType_(ctx.type_());
	}

	@Override
	public Type visitPointerType(PointerTypeContext ctx) {
		Type baseType = visitType_(ctx.type_());
		return GoPointerType.lookup(baseType == null ? Untyped.INSTANCE : baseType);
	}

	@Override
	public List<Pair<String, Type>> visitFieldDecl(FieldDeclContext ctx) {
		List<Pair<String, Type>> result = new ArrayList<>();

		if (ctx.anonymousField() != null)
			result.add(visitAnonymousField(ctx.anonymousField()));
		else {
			Type fieldType = visitType_(ctx.type_());
			for (TerminalNode f : ctx.identifierList().IDENTIFIER())
				result.add(Pair.of(f.getText(), fieldType));
		}

		return result;
	}

	@Override
	public Pair<String, Type> visitAnonymousField(AnonymousFieldContext ctx) {
		Type type = visitTypeName(ctx.typeName());
		if (ctx.STAR() != null)
			type = GoPointerType.lookup(type);
		return Pair.of(ctx.typeName().getText(), type);
	}

	@Override
	public Type visitLiteralType(LiteralTypeContext ctx) {
		if (ctx.structType() != null)
			return visitStructType(ctx.structType());
		else if (ctx.arrayType() != null)
			return visitArrayType(ctx.arrayType());
		else if (ctx.sliceType() != null)
			return visitSliceType(ctx.sliceType());
		else if (ctx.mapType() != null)
			return visitMapType(ctx.mapType());
		else if (ctx.typeName() != null)
			return visitTypeName(ctx.typeName());
		else {
			// Case with ellipsis (e.g., [...]int)
			// -1 is just a placeholder. It will be replaced with the
			// correct size in GoCodeMemberVisitor.visitCompositeLit.
			Type elementType = visitElementType(ctx.elementType());
			return GoArrayType.lookup(elementType, -1);
		}
	}

	@Override
	public GoStructType visitStructType(StructTypeContext ctx) {
		for (FieldDeclContext field : ctx.fieldDecl())
			for (Pair<String, Type> fd : visitFieldDecl(field))
				unit.addInstanceGlobal(new Global(
						new SourceCodeLocation(file, GoCodeMemberVisitor.getLine(field),
								GoCodeMemberVisitor.getCol(field)),
						fd.getLeft(), fd.getRight() == null ? Untyped.INSTANCE : fd.getRight()));
		return GoStructType.lookup(unit.getName(), unit);
	}

	@Override
	public GoType visitInterfaceType(InterfaceTypeContext ctx) {

		// The interface is empty
		if (ctx.methodSpec().size() == 0)
			return GoInterfaceType.getEmptyInterface();

		for (MethodSpecContext methodSpec : ctx.methodSpec())
			for (CFGDescriptor desc : visitMethodSpec(methodSpec))
				unit.addInstanceCFG(new CFG(desc));
		return GoInterfaceType.lookup(unit.getName(), unit);
	}

	@Override
	public Set<CFGDescriptor> visitMethodSpec(MethodSpecContext ctx) {

		Set<CFGDescriptor> descs = new HashSet<>();

		if (ctx.IDENTIFIER() != null) {
			String funcName = ctx.IDENTIFIER().getText();
			ParametersContext formalPars = ctx.parameters();

			int line = GoCodeMemberVisitor.getLine(ctx);
			int col = GoCodeMemberVisitor.getCol(ctx);

			Parameter[] cfgArgs = new Parameter[] {};

			for (int i = 0; i < formalPars.parameterDecl().size(); i++)
				cfgArgs = (Parameter[]) ArrayUtils.addAll(cfgArgs,
						new GoCodeMemberVisitor(unit, file, program, constants, globals)
								.visitParameterDecl(formalPars.parameterDecl(i)));

			descs.add(new CFGDescriptor(new SourceCodeLocation(file, line, col), program, true, funcName,
					getGoReturnType(ctx.result()), cfgArgs));
		}

		// need to include methods of typeName
		// TODO: this works just if the typeName is defined in the source code
		else if (ctx.typeName() != null) {
			Type type = visitTypeName(ctx.typeName());

			CompilationUnit unitType = program.getUnit(type.toString());

			if (unitType != null)
				for (CFG cfg : unitType.getAllCFGs())
					descs.add(cfg.getDescriptor());
		}

		return descs;
	}

	private Type getGoReturnType(ResultContext result) {
		// The return type is not specified
		if (result == null)
			return Untyped.INSTANCE;
		return new GoCodeMemberVisitor(unit, file, program, constants, globals).visitResult(result);
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO: receiver type
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}
}
