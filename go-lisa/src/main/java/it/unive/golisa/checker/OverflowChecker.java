package it.unive.golisa.checker;

import it.unive.golisa.analysis.apron.Apron;
import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.numeric.signed.GoInt16Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt8Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt16Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt32Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SyntheticLocation;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLt;
import it.unive.lisa.type.Type;
import java.math.BigInteger;

public class OverflowChecker
		implements SemanticCheck<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
				MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> {

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {

		Expression leftExpression = null;
		Type vType = null;

		if (node instanceof GoAssignment) {
			GoAssignment assignment = (GoAssignment) node;
			leftExpression = assignment.getLeft();
		} else if (node instanceof GoVariableDeclaration) {
			GoVariableDeclaration assignment = (GoVariableDeclaration) node;
			leftExpression = assignment.getLeft();
			vType = assignment.getDeclaredType();
		} else if (node instanceof GoShortVariableDeclaration) {
			GoShortVariableDeclaration assignment = (GoShortVariableDeclaration) node;
			leftExpression = assignment.getLeft();
		}

		// Checking if each variable reference is over/under flowing
		if (leftExpression instanceof VariableRef) {
			Variable id = new Variable(((VariableRef) leftExpression).getStaticType(),
					((VariableRef) leftExpression).getName(), ((VariableRef) leftExpression).getLocation());

			boolean mayBeNumeric = false;
			for (Type type : id.getRuntimeTypes())
				if (type.isNumericType()) {
					mayBeNumeric = true;
					break;
				}

			if (!mayBeNumeric)
				return true;

			vType = vType == null ? id.getDynamicType() : vType;

			for (CFGWithAnalysisResults<?, ?, ?, ?> an : tool.getResultOf(graph)) {
				AnalysisState<?, ?, ?, ?> analysisAtNode = an.getAnalysisStateAfter(node);
				Apron ap = (Apron) analysisAtNode.getState().getValueState();
				BinaryExpression checkOver = new BinaryExpression(GoBoolType.INSTANCE, id, getMaxValue(vType),
						ComparisonGt.INSTANCE, leftExpression.getLocation());
				BinaryExpression checkUnder = new BinaryExpression(GoBoolType.INSTANCE, id, getMinValue(vType),
						ComparisonLt.INSTANCE, leftExpression.getLocation());

				if (!ap.containsIdentifier(id))
					return true;

				Satisfiability overflows = null;
				Satisfiability underflows = null;
				try {
					boolean needToWarn = false;
					String message = "[OVER/UNDERFLOW] ";
					overflows = ap.satisfies(checkOver, node);

					if (overflows == Satisfiability.SATISFIED) {
						message += "The variable " + id + " overflows.";
						needToWarn = true;
					} else if (overflows == Satisfiability.UNKNOWN) {
						needToWarn = true;
						message += "The variable " + id + " may overflow. Need to manually check.";
					}

					underflows = ap.satisfies(checkUnder, node);

					if (underflows == Satisfiability.SATISFIED) {
						message += "The variable " + id + " undeflows.";
						needToWarn = true;
					} else if (underflows == Satisfiability.UNKNOWN) {
						needToWarn = true;
						message += "The variable " + id + " may undeflow. Need to manually check.";
					}

					if (needToWarn)
						tool.warnOn(node, message);

				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	/*
	 * Min-max numerical type values uint8 the set of all unsigned 8-bit
	 * integers (0 to 255) uint16 the set of all unsigned 16-bit integers (0 to
	 * 65535) uint32 the set of all unsigned 32-bit integers (0 to 4294967295)
	 * uint64 the set of all unsigned 64-bit integers (0 to
	 * 18446744073709551615) int8 the set of all signed 8-bit integers (-128 to
	 * 127) int16 the set of all signed 16-bit integers (-32768 to 32767) int32
	 * the set of all signed 32-bit integers (-2147483648 to 2147483647) int64
	 * the set of all signed 64-bit integers (-9223372036854775808 to
	 * 9223372036854775807) float32 the set of all IEEE-754 32-bit
	 * floating-point numbers float64 the set of all IEEE-754 64-bit
	 * floating-point numbers complex64 the set of all complex numbers with
	 * float32 real and imaginary parts complex128 the set of all complex
	 * numbers with float64 real and imaginary parts
	 */
	private Constant getMaxValue(Type type) {
		if (type == GoInt8Type.INSTANCE)
			return new Constant(type, 127, SyntheticLocation.INSTANCE);

		if (type == GoInt16Type.INSTANCE)
			return new Constant(type, 32767, SyntheticLocation.INSTANCE);

		if (type == GoInt32Type.INSTANCE)
			return new Constant(type, 2147483647, SyntheticLocation.INSTANCE);

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new Constant(type, (long) 9223372036854775807L, SyntheticLocation.INSTANCE);

		if (type == GoUInt8Type.INSTANCE)
			return new Constant(type, 255, SyntheticLocation.INSTANCE);

		if (type == GoUInt16Type.INSTANCE)
			return new Constant(type, 65535, SyntheticLocation.INSTANCE);

		if (type == GoUInt32Type.INSTANCE)
			return new Constant(type, 4294967295L, SyntheticLocation.INSTANCE);

		return new Constant(type, new BigInteger("18446744073709551615"), SyntheticLocation.INSTANCE);
	}

	private Constant getMinValue(Type type) {
		if (type == GoInt8Type.INSTANCE)
			return new Constant(type, -128, SyntheticLocation.INSTANCE);

		if (type == GoInt16Type.INSTANCE)
			return new Constant(type, -32768, SyntheticLocation.INSTANCE);

		if (type == GoInt32Type.INSTANCE)
			return new Constant(type, -2147483648, SyntheticLocation.INSTANCE);

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new Constant(type, (long) -9223372036854775808L, SyntheticLocation.INSTANCE);

		return new Constant(type, 0, SyntheticLocation.INSTANCE);
	}

	@Override
	public void beforeExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean visitCompilationUnit(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			CompilationUnit unit) {
		return true;

	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {

		return true;
	}
}
