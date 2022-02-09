package it.unive.golisa.checker;

import it.unive.golisa.analysis.apron.Apron;
import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;

public class DivisionByZeroChecker
		implements SemanticCheck<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
				MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> {

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (node instanceof GoDiv) {
			GoDiv div = (GoDiv) node;
			Expression right = div.getSubExpressions()[1];

			for (CFGWithAnalysisResults<?, ?, ?, ?> an : tool.getResultOf(graph)) {
				AnalysisState<?, ?, ?, ?> analysisAtRightNode = an.getAnalysisStateAfter(right);

				try {
					for (SymbolicExpression rightExp : analysisAtRightNode.getComputedExpressions()) {
						Constant zero = new Constant(GoIntType.INSTANCE, 0, right.getLocation());
						BinaryExpression divByZero = new BinaryExpression(GoBoolType.INSTANCE, rightExp, zero,
								ComparisonEq.INSTANCE, right.getLocation());
						Satisfiability sat = analysisAtRightNode.satisfies(divByZero, right);

						if (sat == Satisfiability.SATISFIED)
							tool.warnOn(node, "[DEFINITE-DIV-BY-ZERO] division by zero!");
						else if (sat == Satisfiability.UNKNOWN)
							tool.warnOn(node, "[MAYBE-DIV-BY-ZERO] maybe a division by zero!");
					}
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	@Override
	public void beforeExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<MonolithicHeap, Apron, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, Apron, TypeEnvironment<InferredTypes>> tool) {
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
