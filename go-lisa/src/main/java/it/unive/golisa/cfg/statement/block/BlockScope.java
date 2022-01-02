package it.unive.golisa.cfg.statement.block;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

public class BlockScope {

	private final Set<VariableRef> refs;
	
	public BlockScope() {
		refs = new HashSet<>();
	}
	
	public void addVarSpec(VariableRef ref) {
		refs.add(ref);	
	}

	public Set<VariableRef> getVarsSpec() {
		return refs;
	}
	
	public static Optional<VariableRef> findLastVariableRefInBlockList(List<BlockScope> listBlock, Expression exp) {
		
		if(exp instanceof VariableRef) {
			VariableRef ref = (VariableRef) exp;
			for(int i = listBlock.size()-1; i >= 0; i--) //search backward
				for (VariableRef vr : listBlock.get(i).getVarsSpec())
					if(vr.getName().equals(ref.getName()))
							return Optional.of(vr);
		}
		return Optional.empty();
	}
	
	
}
