package it.unive.golisa.checker;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.expression.binary.GoMul;
import it.unive.golisa.cfg.expression.binary.GoSubtraction;
import it.unive.golisa.cfg.expression.binary.GoSum;
import it.unive.golisa.cfg.expression.literal.GoInteger;
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
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.numeric.MathNumber;

/**
*
*/
public class NumericalOverflowChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> {

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		Expression leftExpression = null;
		Expression rightExpression = null;
		Type vType = null;

		if (node instanceof GoSum) {
			GoSum sum = (GoSum) node;
			leftExpression = sum.getLeft();
			rightExpression = sum.getRight();
		} else if (node instanceof GoSubtraction) {
			GoSubtraction sub = (GoSubtraction) node;
			leftExpression = sub.getLeft();
			rightExpression = sub.getRight();
		} else if (node instanceof GoMul) {
			GoMul mul = (GoMul) node;
			leftExpression = mul.getLeft();
			rightExpression = mul.getRight();
		} else if (node instanceof GoDiv) {
			GoDiv div = (GoDiv) node;
			leftExpression = div.getLeft();
			rightExpression = div.getRight();
		}

		if (leftExpression != null && rightExpression != null) {

			if (mayBeNumeric(leftExpression) && mayBeNumeric(rightExpression)) {
				Set<Interval> leftInterval = getPossibleIntervals(leftExpression, tool, graph, node);
				Set<Interval> rightInterval = getPossibleIntervals(rightExpression, tool, graph, node);

				boolean isPossibleOverflow = isPossibleOverflow(leftInterval, rightInterval, null);
				boolean isPossibleUnderflow = isPossibleUnderflow(leftInterval, rightInterval, null);

				if (isPossibleOverflow)
					tool.warnOn(node, "Detected possible numerical overflow!");
				if (isPossibleUnderflow)
					tool.warnOn(node, "Detected possible numerical overflow!");
			}
		}
		/*
		 * else if (node instanceof GoMinus) { GoShortVariableDeclaration
		 * assignment = (GoShortVariableDeclaration) node; leftExpression =
		 * assignment.getLeft(); }
		 */

		/*
		 * // Checking if each variable reference is over/under flowing if
		 * (leftExpression instanceof VariableRef) { Variable id = new
		 * Variable(((VariableRef) leftExpression).getStaticType(),
		 * ((VariableRef) leftExpression).getName(), ((VariableRef)
		 * leftExpression).getLocation()); boolean mayBeNumeric = false; for
		 * (Type type :
		 * id.getRuntimeTypes(leftExpression.getProgram().getTypes())) if
		 * (type.isNumericType()) { mayBeNumeric = true; break; } if
		 * (!mayBeNumeric) return true; vType = vType == null ?
		 * id.getDynamicType() : vType; for
		 * (AnalyzedCFG<SimpleAbstractState<PointBasedHeap,
		 * ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>,
		 * PointBasedHeap, ValueEnvironment<Interval>,
		 * TypeEnvironment<InferredTypes>> an : tool.getResultOf(graph)) {
		 * AnalysisState<SimpleAbstractState<PointBasedHeap,
		 * ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>,
		 * PointBasedHeap, ValueEnvironment<Interval>,
		 * TypeEnvironment<InferredTypes>> analysisAtNode =
		 * an.getAnalysisStateAfter(node); ValueEnvironment<Interval> interval =
		 * (ValueEnvironment<Interval>)
		 * analysisAtNode.getState().getValueState(); BinaryExpression checkOver
		 * = new BinaryExpression(GoBoolType.INSTANCE, id, getMaxValue(vType),
		 * ComparisonGt.INSTANCE, leftExpression.getLocation());
		 * BinaryExpression checkUnder = new
		 * BinaryExpression(GoBoolType.INSTANCE, id, getMinValue(vType),
		 * ComparisonLt.INSTANCE, leftExpression.getLocation()); if
		 * (interval.getState(id) == null) return true; Satisfiability overflows
		 * = null; Satisfiability underflows = null; try { boolean needToWarn =
		 * false; String message= "[OVER/UNDERFLOW] "; overflows =
		 * interval.satisfies(checkOver, node); if (overflows ==
		 * Satisfiability.SATISFIED) { message += "The variable " + id +
		 * " overflows."; needToWarn = true; } else if (overflows ==
		 * Satisfiability.UNKNOWN) { needToWarn = true; message +=
		 * "The variable " + id + " may overflow. Need to manually check."; }
		 * underflows = interval.satisfies(checkUnder, node); if (underflows ==
		 * Satisfiability.SATISFIED) { message += "The variable " + id +
		 * " undeflows."; needToWarn = true; } else if (underflows ==
		 * Satisfiability.UNKNOWN) { needToWarn = true; message +=
		 * "The variable " + id + " may undeflow. Need to manually check."; } if
		 * (needToWarn) tool.warnOn(node, message); } catch (SemanticException
		 * e) { e.printStackTrace(); } } }
		 */
		return true;
	}

	private boolean isPossibleOverflow(Set<Interval> leftInterval, Set<Interval> rightInterval, Type type) {

		return leftInterval.parallelStream().anyMatch(li -> rightInterval.parallelStream().anyMatch(ri -> {
			return isOverflow(li, ri, type);
		}));
	}

	private boolean isOverflow(Interval li, Interval ri, Type type) {
		if (li.interval == null || ri.interval == null)
			return false;

		MathNumber lHigh = li.interval.getHigh();
		MathNumber rHigh = ri.interval.getHigh();
		if (lHigh == MathNumber.PLUS_INFINITY && rHigh.gt(MathNumber.ZERO))
			return true;
		else if (rHigh == MathNumber.PLUS_INFINITY && lHigh.gt(MathNumber.ZERO))
			return true;

		MathNumber res = lHigh.add(rHigh);
		return res.gt(new MathNumber(getMaxValue(type)));
	}

	private boolean isPossibleUnderflow(Set<Interval> leftInterval, Set<Interval> rightInterval, Type type) {

		return leftInterval.parallelStream().anyMatch(li -> rightInterval.parallelStream().anyMatch(ri -> {
			return isUnderflow(li, ri, type);
		}));
	}

	private boolean isUnderflow(Interval li, Interval ri, Type type) {
		if (li.interval == null || ri.interval == null)
			return false;

		MathNumber lLow = li.interval.getLow();
		MathNumber rLow = ri.interval.getLow();
		if (lLow == MathNumber.MINUS_INFINITY && rLow.gt(MathNumber.ZERO))
			return true;
		else if (rLow == MathNumber.PLUS_INFINITY && lLow.lt(MathNumber.ZERO))
			return true;

		MathNumber res = lLow.subtract(rLow);
		return res.lt(new MathNumber(getMinValue(type)));
	}

	private Set<Interval> getPossibleIntervals(Expression expr, CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		Set<Interval> res = new TreeSet<Interval>();
		if (expr instanceof VariableRef) {
			Variable id = new Variable(((VariableRef) expr).getStaticType(), ((VariableRef) expr).getName(),
					((VariableRef) expr).getLocation());

			for (AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> an : tool.getResultOf(graph)) {
				AnalysisState<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
						TypeEnvironment<InferredTypes>>> analysisAtNode = an.getAnalysisStateAfter(node);
				ValueEnvironment<Interval> intervalValueState = (ValueEnvironment<Interval>) analysisAtNode.getState()
						.getValueState();

				res.add(intervalValueState.getState(id));
			}
		} else if (expr instanceof GoInteger) {
			GoInteger n = (GoInteger) expr;
			Integer value = (Integer) n.getValue();
			res.add(new Interval(value.intValue(), value.intValue()));
		}
		return res;
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
	private BigDecimal getMaxValue(Type type) {
		if (type == GoInt8Type.INSTANCE)
			return new BigDecimal("127");

		if (type == GoInt16Type.INSTANCE)
			return new BigDecimal("32767");

		if (type == GoInt32Type.INSTANCE)
			return new BigDecimal("2147483647");

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new BigDecimal("9223372036854775807");

		if (type == GoUInt8Type.INSTANCE)
			return new BigDecimal("255");

		if (type == GoUInt16Type.INSTANCE)
			return new BigDecimal("65535");

		if (type == GoUInt32Type.INSTANCE)
			return new BigDecimal("4294967295");

		return new BigDecimal("18446744073709551615");
	}

	private BigDecimal getMinValue(Type type) {
		if (type == GoInt8Type.INSTANCE)
			return new BigDecimal("-128");

		if (type == GoInt16Type.INSTANCE)
			return new BigDecimal("-32768");

		if (type == GoInt32Type.INSTANCE)
			return new BigDecimal("-2147483648");

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new BigDecimal("-9223372036854775808");

		return new BigDecimal("-9223372036854775808");
	}

	private boolean mayBeNumeric(Expression expr) {

		if (expr instanceof VariableRef) {
			Variable id = new Variable(((VariableRef) expr).getStaticType(), ((VariableRef) expr).getName(),
					((VariableRef) expr).getLocation());

			boolean mayBeNumeric = false;

//			for (Type type : id.getRuntimeTypes(expr.getProgram().getTypes()))
//				if (type.isNumericType()) {
//					mayBeNumeric = true;
//					break;
//				}

			if (mayBeNumeric)
				return true;
		}
		if (expr instanceof GoInteger) {
			return true;
		}

		return false;
	}
}
