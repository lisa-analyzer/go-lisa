package it.unive.golisa.checker;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.expression.binary.GoMul;
import it.unive.golisa.cfg.expression.binary.GoSubtraction;
import it.unive.golisa.cfg.expression.binary.GoSum;
import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.type.numeric.signed.GoInt16Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt8Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt16Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt32Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.golisa.checker.NumericalOverflowOfVariablesChecker.NumericalIssueEnum.NumericalIssue;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.numeric.IntInterval;
import it.unive.lisa.util.numeric.MathNumber;

/**
 * Checker for the detection of integer overflow/underflow in program variables.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class NumericalOverflowOfVariablesChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> {

	Set<IssueInfo> detectedIssues = new HashSet<>();

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool) {
		// TODO: find a more smart way to trigger warnings
		for (IssueInfo issue : detectedIssues) {
			tool.warnOn(issue.getStatement(), getWarningMessage(issue.getIssues()));
		}
	}

	private String getWarningMessage(Set<NumericalIssue> numericalIssues) {

		String res = "";
		int i = 0;
		for (NumericalIssue ni : numericalIssues) {
			if (i > 0)
				res += " and ";
			switch (ni) {
			case OVERFLOW:
				res += "an integer overflow occurs";
				break;
			case MAY_OVERFLOW:
				res += "an integer overflow may occur";
				break;
			case UNDERFLOW:
				res += "an integer underflow occurs";
				break;
			case MAY_UNDERFLOW:
				res += "an integer underflow may occur";
				break;
			default:
				new IllegalArgumentException("Message for the numerical issue " + ni + " not implemented yet!");

			}
			i++;
		}

		return "Detected numerical issues: " + res;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		Expression leftExpression = null;
		Type vType = null;
		Set<Type> numericalTypes = new HashSet<Type>();
		if (node instanceof GoAssignment) {
			GoAssignment assignment = (GoAssignment) node;
			leftExpression = assignment.getLeft();
			if (!containsMathematicalOperation(assignment.getRight()))
				return true;
		} else if (node instanceof GoVariableDeclaration) {
			GoVariableDeclaration assignment = (GoVariableDeclaration) node;
			leftExpression = assignment.getLeft();
			vType = assignment.getDeclaredType();
			if (!containsMathematicalOperation(assignment.getRight()))
				return true;
		} else if (node instanceof GoShortVariableDeclaration) {
			GoShortVariableDeclaration assignment = (GoShortVariableDeclaration) node;
			leftExpression = assignment.getLeft();
			if (!containsMathematicalOperation(assignment.getRight()))
				return true;
		}

		// Checking if each field reference is over/under-flowing
		if (leftExpression instanceof GoCollectionAccess) {
			GoCollectionAccess collectionAccess = (GoCollectionAccess) leftExpression;
			Expression rightExpression = collectionAccess.getRight();
			if (rightExpression instanceof VariableRef)
				checkVariableRef(tool, (VariableRef) rightExpression, vType, numericalTypes, graph, node);
		}

		// Checking if each variable reference is over/under-flowing
		if (leftExpression instanceof VariableRef) {
			checkVariableRef(tool, (VariableRef) leftExpression, vType, numericalTypes, graph, node);
		}

		return true;
	}

	private boolean containsMathematicalOperation(Expression right) {
		return right instanceof GoSum || right instanceof GoSubtraction || right instanceof GoDiv
				|| right instanceof GoMul;
	}

	private void checkVariableRef(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			VariableRef varRef, Type vType, Set<Type> numericalTypes, CFG graph, Statement node) {
		Variable id = new Variable(((VariableRef) varRef).getStaticType(), ((VariableRef) varRef).getName(),
				((VariableRef) varRef).getLocation());

		if (GoLangUtils.isBlankIdentifier(id))
			return;

		vType = vType == null ? id.getStaticType() : vType;

		boolean mayBeNumeric = false;
		if (vType.isNumericType()) {
			numericalTypes.add(vType);
			mayBeNumeric = true;
		} else if (vType.isUntyped()) {
			for (var result : tool.getResultOf(graph)) {
				var postState = result.getAnalysisStateAfter(node);
				Set<SymbolicExpression> reachableIds = new HashSet<>();
				Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions().iterator();
				if (comExprIterator.hasNext()) {

					SymbolicExpression expr = comExprIterator.next();
					try {
						reachableIds.addAll(tool.getAnalysis().reachableFrom(postState, expr, node).elements);

						for (SymbolicExpression s : reachableIds) {
							Type dynamicType = tool.getAnalysis().getDynamicTypeOf(postState, s, node);
							if (dynamicType.isNumericType()) {
								numericalTypes.add(vType);
							} else if (dynamicType.isUntyped()) {
								Set<Type> runtimeTypes = tool.getAnalysis().getRuntimeTypesOf(postState, s, node);

								if (runtimeTypes.stream().anyMatch(t -> t.isNumericType() || t == Untyped.INSTANCE))
									for (Type t : runtimeTypes)
										if (t.isNumericType())
											numericalTypes.add(t);
							}

						}
					} catch (SemanticException e) {
						System.err.println("Cannot check " + node);
						e.printStackTrace(System.err);
					}
				}
			}

			mayBeNumeric = !numericalTypes.isEmpty();

		}

		if (!mayBeNumeric)
			return;

		for (var result : tool.getResultOf(graph)) {
			var postState = result.getAnalysisStateAfter(node);

			Set<SymbolicExpression> reachableIds = new HashSet<>();
			Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions().iterator();
			if (comExprIterator.hasNext()) {

				SymbolicExpression boolExpr = comExprIterator.next();
				try {
					reachableIds.addAll(tool.getAnalysis().reachableFrom(postState, boolExpr, node).elements);

					for (SymbolicExpression s : reachableIds) {

						if (s instanceof Variable v) {
							if (id.equals(v)) {
								// extraction of the abstract value
								var valueState = postState.getExecutionState().valueState;

								SemanticOracle oracle = tool.getAnalysis().domain
										.makeOracle(postState.getExecutionState());

								Interval analysisValueDomain = (Interval) tool.getAnalysis().domain.valueDomain;

								IntInterval abstractValue = analysisValueDomain.eval(valueState, (ValueExpression) s,
										(ProgramPoint) node, oracle);
								if (!abstractValue.isBottom()) {
									NumericalIssue overflow = checkOverflow(tool, node, abstractValue, numericalTypes);
									NumericalIssue underflow = checkUnderflow(tool, node, abstractValue,
											numericalTypes);

									if (overflow != null || underflow != null) {
										IssueInfo info = new IssueInfo(node, new HashSet<>());
										if (overflow != null)
											info.getIssues().add(overflow);
										if (underflow != null)
											info.getIssues().add(underflow);
										detectedIssues.add(info);
									}
								}
							}
						}
					}
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}

			/*
			 * Interval intervalAbstractValue = state.getValueState().getState(id);
			 * if(!intervalAbstractValue.isBottom()) { IntInterval interval =
			 * intervalAbstractValue.interval;
			 * 
			 * 
			 * }
			 */
		}

	}

	private NumericalIssue checkOverflow(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			Statement node, IntInterval interval, Set<Type> numericalTypes) {
		Type[] arrayTypes = numericalTypes.toArray(new Type[] {});
		if (!numericalTypes.isEmpty())
			if (numericalTypes.size() == 1) {
				if (isOverflow(interval, arrayTypes[0]))
					return NumericalIssue.OVERFLOW;
			} else {
				if (isOverflow(interval, getWorstCaseTypeForOverflow(numericalTypes)))
					return NumericalIssue.MAY_OVERFLOW;
			}
		return null;
	}

	private Type getWorstCaseTypeForOverflow(Set<Type> numericTypes) {
		Type[] arrayTypes = numericTypes.toArray(new Type[] {});
		Type res = null;
		if (arrayTypes.length >= 1) {
			res = arrayTypes[0];

			for (int i = 1; i < arrayTypes.length; i++) {
				if (getMaxValue(arrayTypes[i]).compareTo(getMaxValue(res)) < 0)
					res = arrayTypes[i];
			}
		}

		return res;
	}

	private boolean isOverflow(IntInterval interval, Type vType) {
		return interval.getHigh().compareTo(getMaxValue(vType)) > 0;

	}

	private NumericalIssue checkUnderflow(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			Statement node, IntInterval interval, Set<Type> numericalTypes) {
		Type[] arrayTypes = numericalTypes.toArray(new Type[] {});
		if (!numericalTypes.isEmpty()) {
			if (numericalTypes.size() == 1) {
				if (isUnderflow(interval, arrayTypes[0]))
					return NumericalIssue.UNDERFLOW;
			} else {
				if (isUnderflow(interval, getWorstCaseTypeForUnderflow(numericalTypes)))
					return NumericalIssue.MAY_UNDERFLOW;
			}
		}
		return null;
	}

	private Type getWorstCaseTypeForUnderflow(Set<Type> numericTypes) {
		Type[] arrayTypes = numericTypes.toArray(new Type[] {});
		Type res = null;
		if (arrayTypes.length >= 1) {
			res = arrayTypes[0];

			for (int i = 1; i < arrayTypes.length; i++) {
				if (getMinValue(arrayTypes[i]).compareTo(getMinValue(res)) > 0)
					res = arrayTypes[i];
			}
		}

		return res;
	}

	private boolean isUnderflow(IntInterval interval, Type vType) {
		return interval.getLow().compareTo(getMinValue(vType)) < 0;
	}

	/*
	 * Min-max numerical type values
	 * 
	 * uint8 the set of all unsigned 8-bit integers (0 to 255) uint16 the set of all
	 * unsigned 16-bit integers (0 to 65535) uint32 the set of all unsigned 32-bit
	 * integers (0 to 4294967295) uint64 the set of all unsigned 64-bit integers (0
	 * to 18446744073709551615)
	 * 
	 * int8 the set of all signed 8-bit integers (-128 to 127) int16 the set of all
	 * signed 16-bit integers (-32768 to 32767) int32 the set of all signed 32-bit
	 * integers (-2147483648 to 2147483647) int64 the set of all signed 64-bit
	 * integers (-9223372036854775808 to 9223372036854775807)
	 * 
	 * float32 the set of all IEEE-754 32-bit floating-point numbers float64 the set
	 * of all IEEE-754 64-bit floating-point numbers
	 * 
	 * complex64 the set of all complex numbers with float32 real and imaginary
	 * parts complex128 the set of all complex numbers with float64 real and
	 * imaginary parts
	 */
	private MathNumber getMaxValue(Type type) {
		if (type == GoInt8Type.INSTANCE)
			return new MathNumber(127);

		if (type == GoInt16Type.INSTANCE)
			return new MathNumber(32767);

		if (type == GoInt32Type.INSTANCE)
			return new MathNumber(2147483647);

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new MathNumber(9223372036854775807L);

		if (type == GoUInt8Type.INSTANCE)
			return new MathNumber(255);

		if (type == GoUInt16Type.INSTANCE)
			return new MathNumber(65535);

		if (type == GoUInt32Type.INSTANCE)
			return new MathNumber(4294967295L);

		return new MathNumber(new BigDecimal("18446744073709551615"));
	}

	private MathNumber getMinValue(Type type) {

		if (type == GoUInt8Type.INSTANCE || type == GoUInt16Type.INSTANCE || type == GoUInt32Type.INSTANCE)
			return new MathNumber(0);

		if (type == GoInt8Type.INSTANCE)
			return new MathNumber(-128);

		if (type == GoInt16Type.INSTANCE)
			return new MathNumber(-32768);

		if (type == GoInt32Type.INSTANCE)
			return new MathNumber(-2147483648);

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new MathNumber(-9223372036854775808L);

		return new MathNumber(-9223372036854775808L);
	}

	public static class NumericalIssueEnum {
		public enum NumericalIssue {
			OVERFLOW(false), MAY_OVERFLOW(true), UNDERFLOW(false), MAY_UNDERFLOW(true);

			private final boolean may;

			NumericalIssue(boolean may) {
				this.may = may;
			}

			boolean isMay() {
				return may;
			}
		}
	}

	class IssueInfo {

		private final Statement statement;
		private final Set<NumericalIssue> issues;

		public IssueInfo(Statement statement, Set<NumericalIssue> issues) {
			this.statement = statement;
			this.issues = issues;
		}

		public Statement getStatement() {
			return statement;
		}

		public Set<NumericalIssue> getIssues() {
			return issues;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(issues, statement);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IssueInfo other = (IssueInfo) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(issues, other.issues) && Objects.equals(statement, other.statement);
		}

		private NumericalOverflowOfVariablesChecker getEnclosingInstance() {
			return NumericalOverflowOfVariablesChecker.this;
		}

	}

}
