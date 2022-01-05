package it.unive.golisa.cfg.statement.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	private final OpenBlock open;

	public BlockScope(OpenBlock open) {
		refs = new HashMap<>();
		this.open = open;
	}

	public void addVarDeclaration(VariableRef ref, DeclarationType  type) {
		refs.put(ref, type);	
	}

	private Map<VariableRef, DeclarationType> getVarsDeclarations() {
		return refs;
	}

	public static List<OpenBlock> findLastVariableDeclarationInBlockList(List<BlockScope> listBlock, Expression exp) {
		List<OpenBlock> openBlocks = new ArrayList<>();
		if(exp instanceof VariableRef) {
			VariableRef ref = (VariableRef) exp;
			for(int i = listBlock.size()-1; i >= 0; i--) //search backward
				for (Entry<VariableRef, DeclarationType> e : listBlock.get(i).getVarsDeclarations().entrySet())
					if(e.getKey().getName().equals(ref.getName())) {
						for (int j = i; j < listBlock.size(); j++)
							openBlocks.add(0 ,listBlock.get(j).getOpen());
						return openBlocks;
					}
		}
		return openBlocks;
	}

	public OpenBlock getOpen() {
		return open;
	}	
}
