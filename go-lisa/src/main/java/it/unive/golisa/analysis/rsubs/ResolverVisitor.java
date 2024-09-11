package it.unive.golisa.analysis.rsubs;

import it.unive.golisa.analysis.StringConstantPropagation;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.symbolic.ExpressionVisitor;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.PushInv;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;
import it.unive.lisa.symbolic.value.ValueExpression;
/**
 * Visitor for value expression. If the expression is constant, its visit
 * returns the constant string.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ResolverVisitor implements ExpressionVisitor<String> {

	private static final String CANNOT_PROCESS_ERROR = "Cannot process a heap expression with a value domain";


	public String visit(AccessChild expression, String receiver, String child, Object... params)
			throws SemanticException {
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}

	@Override
	public String visit(MemoryAllocation expression, Object... params) throws SemanticException {
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}

	@Override
	public String visit(HeapReference expression, String arg, Object... params) throws SemanticException {
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}

	@Override
	public String visit(HeapDereference expression, String arg, Object... params) throws SemanticException {
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}

	@Override
	public String visit(UnaryExpression expression, String arg, Object... params) throws SemanticException {
		return null;
	}

	@Override
	public String visit(BinaryExpression expression, String left, String right, Object... params)
			throws SemanticException {

		if (expression.getOperator() == StringConcat.INSTANCE) {

			if (left != null && right != null)
				return left + right;
			return null;
		} else
			return null;
	}

	@Override
	public String visit(TernaryExpression expression, String left, String middle, String right, Object... params)
			throws SemanticException {

		if (expression.getOperator() == StringReplace.INSTANCE) {
			if (left != null && middle != null && right != null)
				return left.replaceAll(middle, right);
			return null;
		} else
			return null;
	}

	
	@Override
	public String visit(
			ValueExpression expression,
			String[] subExpressions,
			Object... params)
			throws SemanticException{
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}
	
	@Override
	public String visit(
			HeapExpression expression,
			String[] subExpressions,
			Object... params)
			throws SemanticException {
		throw new SemanticException(CANNOT_PROCESS_ERROR);
	}
	
	@Override
	public String visit(Skip expression, Object... params) throws SemanticException {
		return null;
	}

	@Override
	public String visit(PushAny expression, Object... params) throws SemanticException {
		return null;
	}

	@Override
	public String visit(Constant expression, Object... params) throws SemanticException {
		if (expression.getValue() instanceof String)
			return (String) expression.getValue();
		else
			return null;
	}

	@Override
	public String visit(Identifier expression, Object... params) throws SemanticException {
		@SuppressWarnings("unchecked")
		ValueEnvironment<StringConstantPropagation> cs = (ValueEnvironment<StringConstantPropagation>) params[0];

		if (cs.getKeys().contains(expression))
			return cs.getState(expression).getString();

		return null;
	}

	@Override
	public String visit(PushInv expression, Object... params) throws SemanticException {
		return null;
	}
}
