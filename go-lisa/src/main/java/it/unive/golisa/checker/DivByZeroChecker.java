package it.unive.golisa.checker;

import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
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
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.numeric.IntInterval;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Checker for the detection of division by zero.
 *
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class DivByZeroChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> {

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		if (node instanceof GoDiv)
			checkGoDiv(tool, graph, (GoDiv) node);

		return true;
	}

	private void checkGoDiv(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> tool,
			CFG graph, GoDiv div) {

		for (AnalyzedCFG<
				SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>, TypeEnvironment<T>>> res : tool
						.getResultOf(graph)) {
			AnalysisState<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<IntInterval>,
					TypeEnvironment<T>>> postState = res
							.getAnalysisStateAfter(div.getRight()); // get post
																	// abstract
																	// state of
																	// denominator

			Set<SymbolicExpression> reachableIds = new HashSet<>();
			Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions().iterator();
			if (comExprIterator.hasNext()) {

				SymbolicExpression expr = comExprIterator.next();
				try {
					reachableIds.addAll(tool.getAnalysis().reachableFrom(postState, expr, div).elements);

					for (SymbolicExpression s : reachableIds) {
						Set<Type> types = tool.getAnalysis().getRuntimeTypesOf(postState, s, div);

						if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
							continue;
						// extraction of the abstract value
						var valueState = postState.getExecutionState().valueState;

						SemanticOracle oracle = tool.getAnalysis().domain.makeOracle(postState.getExecutionState());

						Interval analysisValueDomain = (Interval) tool.getAnalysis().domain.valueDomain;

						IntInterval abstractValue = analysisValueDomain.eval(valueState, (ValueExpression) s,
								(ProgramPoint) div, oracle);

						if (abstractValue.equals(new IntInterval(0, 0)))
							tool.warnOn(div, "Detected a division by zero");
						else if (abstractValue.includes(new IntInterval(0, 0)))
							tool.warnOn(div, "Detected a possible division by zero");

					}
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
