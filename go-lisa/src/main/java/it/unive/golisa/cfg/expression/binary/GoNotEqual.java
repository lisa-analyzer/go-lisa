package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.comparison.NotEqual;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.type.Type;
import java.util.Set;

/**
 * A Go equal expression (e.g., x == y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNotEqual extends NotEqual {

	public GoNotEqual(CFG cfg, CodeLocation location, Expression left, Expression right) {
		super(cfg, location, left, right);
	}

}
