package it.unive.golisa.cli;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.antlr.GoParser.AnonymousFieldContext;
import it.unive.golisa.antlr.GoParser.ArrayLengthContext;
import it.unive.golisa.antlr.GoParser.ArrayTypeContext;
import it.unive.golisa.antlr.GoParser.ChannelTypeContext;
import it.unive.golisa.antlr.GoParser.ElementTypeContext;
import it.unive.golisa.antlr.GoParser.FieldDeclContext;
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
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoTypeVisitor extends GoParserBaseVisitor<Object> {

	protected final String file;

	protected final CompilationUnit unit;

	protected final Program program;

	public GoTypeVisitor(String file, CompilationUnit unit, Program program) {
		this.file = file;
		this.unit = unit;
		this.program = program;
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
					CompilationUnit unit = new CompilationUnit(new SourceCodeLocation(file, 0, 0), type, false);
					return GoStructType.lookup(type, unit);
				}
			} 
		} 

		Pair<String, String> pair = visitQualifiedIdent(ctx.qualifiedIdent());
		return GoQualifiedType.lookup(new GoQualifiedType(pair.getLeft(), pair.getRight()));
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
	public GoType visitTypeLit(TypeLitContext ctx) {
		Object result = visitChildren(ctx);
		if (!(result instanceof GoType))
			throw new IllegalStateException("Type expected: " + result + " " + ctx.getText());
		else 
			return (GoType) result;
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
	public GoType visitElementType(ElementTypeContext ctx) {
		return visitType_(ctx.type_());
	}

	@Override
	public GoType visitPointerType(PointerTypeContext ctx) {
		return GoPointerType.lookup(new GoPointerType(visitType_(ctx.type_())));
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
	public Pair<String, Type> visitAnonymousField(AnonymousFieldContext ctx) {
		Type type = visitTypeName(ctx.typeName());
		if (ctx.STAR() != null)
			type = new GoPointerType(type);
		return Pair.of(ctx.typeName().getText(), type);
	}

	@Override
	public GoType visitLiteralType(LiteralTypeContext ctx) {
		Object child = visitChildren(ctx);
		if (!(child instanceof GoType))
			throw new IllegalStateException("Type expected");
		else
			return (GoType) child;
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

	@Override
	public GoStructType visitStructType(StructTypeContext ctx) {		
		for (FieldDeclContext field : ctx.fieldDecl()) 
			for (Pair<String, Type> fd : visitFieldDecl(field))
				unit.addInstanceGlobal(new Global(new SourceCodeLocation(file, getLine(field), getCol(field)), fd.getLeft(), fd.getRight()));

		return GoStructType.lookup(unit.getName(), unit);
	}

	@Override
	public GoType visitInterfaceType(InterfaceTypeContext ctx) {
		for (MethodSpecContext methodSpec : ctx.methodSpec()) 
			unit.addInstanceCFG(new CFG(visitMethodSpec(methodSpec)));
		return GoInterfaceType.lookup(unit.getName(), unit);
	}

	@Override
	public CFGDescriptor visitMethodSpec(MethodSpecContext ctx) {

		if (ctx.IDENTIFIER() != null) {
			String funcName = ctx.IDENTIFIER().getText();
			ParametersContext formalPars = ctx.parameters();

			int line = getLine(ctx);
			int col = getCol(ctx);

			Parameter[] cfgArgs = new Parameter[]{};

			for (int i = 0; i < formalPars.parameterDecl().size(); i++)
				cfgArgs = (Parameter[]) ArrayUtils.addAll(cfgArgs, visitParameterDecl(formalPars.parameterDecl(i)));
			
			return new CFGDescriptor(new SourceCodeLocation(file, line, col), program, true, funcName, getGoReturnType(ctx.result()), cfgArgs);
		}

		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}

	private Type getGoReturnType(ResultContext result) {
		// The return type is not specified
		if (result == null)
			return Untyped.INSTANCE;
		return new GoCodeMemberVisitor(file, program).visitResult(result);
	}

	@Override
	public Statement visitReceiverType(ReceiverTypeContext ctx) {
		// TODO: receiver type
		throw new UnsupportedOperationException("Unsupported translation: " + ctx.getText());
	}
}
