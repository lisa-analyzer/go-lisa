package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoTypeAssertion extends UnaryNativeCall {

	private final Type type;
	
	public GoTypeAssertion(CFG cfg, SourceCodeLocation location, Expression exp, Type type) {
		super(cfg, location, ".(" + type +")", exp);
		this.type = type;
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		
		// A type assertion provides access to an interface value's underlying concrete value,
		// hence we need to check if the static type of the arguments is an interface
		Type argStaticType = getParameters()[0].getStaticType();
		if (argStaticType instanceof GoInterfaceType || argStaticType instanceof Untyped) 			
			for (Type exprType : expr.getTypes()) 
				if (exprType.canBeAssignedTo(type)) 
					return exprState;
				
		return entryState.bottom();
	}
}
