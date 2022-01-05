package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class tracking information about the variable declarations occurring in a
 * block.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class BlockInfo {

	public enum DeclarationType {
		CONSTANT,
		VARIABLE,
		SHORT_VARIABLE,
		MULTI_SHORT_VARIABLE
	}

	private final Map<VariableRef, DeclarationType> refs;

	private final OpenBlock open;

	public BlockInfo(OpenBlock open) {
		refs = new HashMap<>();
		this.open = open;
	}

	private Map<VariableRef, DeclarationType> getVarsDeclarations() {
		return refs;
	}

	public static List<BlockInfo> getListOfBlocksBeforeDeclaration(List<BlockInfo> listBlock, Expression exp) {
		List<BlockInfo> openBlocks = new ArrayList<>();
		if (exp instanceof VariableRef) {
			VariableRef ref = (VariableRef) exp;
			for (int i = listBlock.size() - 1; i >= 0; i--) // search backward
				for (Entry<VariableRef, DeclarationType> e : listBlock.get(i).getVarsDeclarations().entrySet())
					if (e.getKey().getName().equals(ref.getName())) {
						for (int j = i; j < listBlock.size(); j++)
							openBlocks.add(0, listBlock.get(j));
						return openBlocks;
					}
		}
		return openBlocks;
	}

	/**
	 * Checks whether the declaration of {@code exp} in this block info is
	 * constant.
	 * 
	 * @param exp the expression
	 * 
	 * @return {@code true} if {@code exp} is a {@link VariableRef} and its
	 *             declaration is constant and occurs in the block concerning
	 *             {@code this}, {@code false} otherwise
	 */
	public boolean isConstantDeclaration(Expression exp) {
		if (exp instanceof VariableRef) {
			VariableRef v = (VariableRef) exp;
			for (Entry<VariableRef, DeclarationType> e : getVarsDeclarations().entrySet())
				if (e.getKey().getName().equals(v.getName()) && e.getValue() == DeclarationType.CONSTANT)
					return true;
		}

		return false;
	}

	/**
	 * Yields the {@link OpenBlock} of the block concerning this information.
	 * 
	 * @return the {@link OpenBlock} of the block concerning this information.
	 */
	public OpenBlock getOpen() {
		return open;
	}

	public void addVarDeclaration(VariableRef ref, DeclarationType type) {
		refs.put(ref, type);
	}
}
