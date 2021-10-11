package it.unive.golisa.checker;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.binary.GoLess;
import it.unive.golisa.cfg.expression.unary.GoLength;
import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
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
import it.unive.lisa.type.Type;

public class ForRangeChecker implements SemanticCheck {

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<?, ?, ?> tool) {}

	@Override
	public void afterExecution(CheckToolWithAnalysisResults<?, ?, ?> tool) {}

	@Override
	public boolean visitCompilationUnit(CheckToolWithAnalysisResults<?, ?, ?> tool, CompilationUnit unit) {
		return true;
	}

	@Override
	public void visitGlobal(CheckToolWithAnalysisResults<?, ?, ?> tool, Unit unit, Global global, boolean instance) {}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<?, ?, ?> tool, CFG graph) {
		return true;
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<?, ?, ?> tool, CFG graph, Edge edge) {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(CheckToolWithAnalysisResults<?, ?, ?> tool, CFG graph, Statement node) {
		if (node instanceof GoRange) {
			Set<Type> rangedTypes = new HashSet<>();
			GoRange range = (GoRange) node;
			for (Expression e1 : range.getParameters()) {
				if(e1 instanceof GoLess) {
					GoLess less = (GoLess) e1;
					for(Expression e2 : less.getParameters()) {
						if(e2 instanceof GoLength) {
							GoLength length = (GoLength) e2;
							for(Expression e3 : length.getParameters()) {
								for (CFGWithAnalysisResults<?, ?, ?> an : tool.getResultOf(graph)) {
									AnalysisState<?, ?, ?> analysisAtNode = an.getAnalysisStateAfter(e3);
									InferenceSystem<InferredTypes> v = (InferenceSystem<InferredTypes>) analysisAtNode.getState().getValueState();
									rangedTypes.addAll(((InferredTypes) v.getInferredValue()).getRuntimeTypes());
								}
							}
						}
					}
				}
			}

			for (Type type : rangedTypes)
				if (type instanceof GoMapType) {
					tool.warnOn(node, "For-range iteration detected!" + type);
					return true;
				}

		}
		return true;
	}
}
