package it.unive.golisa.cfg.statement.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

/**
 * The class is used to track the variable declaration in a block
 * 
 */
public class BlockScope {

	public enum DeclarationType {
		CONSTANT,
		VARIABLE,
		SHORT_VARIABLE,
		MULTI_SHORT_VARIABLE, 
	}
	
	private final Map<VariableRef, DeclarationType> refs;
	
	public BlockScope() {
		refs = new HashMap<>();
	}
	
	public void addVarDeclaration(VariableRef ref, DeclarationType  type) {
		refs.put(ref, type);	
	}

	private Map<VariableRef, DeclarationType> getVarsDeclarations() {
		return refs;
	}
	
	public static Optional<Pair<VariableRef, DeclarationType>> findLastVariableDeclarationInBlockList(List<BlockScope> listBlock, Expression exp) {
		
		if(exp instanceof VariableRef) {
			VariableRef ref = (VariableRef) exp;
			for(int i = listBlock.size()-1; i >= 0; i--) //search backward
				for (Entry<VariableRef, DeclarationType> e : listBlock.get(i).getVarsDeclarations().entrySet())
					if(e.getKey().getName().equals(ref.getName()))
							return Optional.of(Pair.of(e.getKey(),e.getValue()));
		}
		return Optional.empty();
	}	
	
}
