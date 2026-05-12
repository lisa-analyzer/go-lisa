package it.unive.golisa.cfg.statement.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

/**
 * A class tracking information about the variable declarations occurring in a
 * block.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class BlockInfo {

	/**
	 * Declaration type.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public enum DeclarationType {

		/**
		 * Constant variable declaration.
		 */
		CONSTANT,

		/**
		 * Variable declaration.
		 */
		VARIABLE,

		/**
		 * Short variable declaration.
		 */
		SHORT_VARIABLE,

		/**
		 * Multi variable declaration.
		 */
		MULTI_SHORT_VARIABLE
	}

	private final Map<VariableRef, DeclarationType> refs;

	private final OpenBlock open;

	/**
	 * Builds a block information.
	 * 
	 * @param open the open block to which this class refers to
	 */
	public BlockInfo(OpenBlock open) {
		refs = new HashMap<>();
		this.open = open;
	}

	private Map<VariableRef, DeclarationType> getVarsDeclarations() {
		return refs;
	}

	/**
	 * Yields a list of {@link BlockInfo} to reach the first declaration of
	 * {@code exp}.
	 * 
	 * @param listBlock the block list information
	 * @param exp       the expression
	 * 
	 * @return a list of {@link BlockInfo} to reach the first declaration of
	 *             {@code exp}
	 */
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
	 * @return the {@link OpenBlock} of the block concerning this information
	 */
	public OpenBlock getOpen() {
		return open;
	}

	/**
	 * Adds a variable declaration to {@code this} block information.
	 * 
	 * @param var  the declared variable
	 * @param type the type declaration
	 */
	public void addVarDeclaration(VariableRef var, DeclarationType type) {
		refs.put(var, type);
	}
}
