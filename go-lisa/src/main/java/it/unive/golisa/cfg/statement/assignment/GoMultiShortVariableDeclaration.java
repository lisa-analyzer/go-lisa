package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * A Go multi short variable declaration statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMultiShortVariableDeclaration extends GoMultiAssignment {

	/**
	 * Builds a multi-short variable declaration.
	 * 
	 * @param cfg             the cfg that this statement belongs to
	 * @param location        the location where this statement is defined
	 *                            within the source file
	 * @param ids             identifiers to assign
	 * @param e               expression to assign
	 * @param listBlock       list of block informations
	 * @param containingBlock the block to which this assignment belongs to
	 */
	public GoMultiShortVariableDeclaration(CFG cfg, SourceCodeLocation location, Expression[] ids,
			Expression e, List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, location, ids, e, listBlock, containingBlock);
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}
}
