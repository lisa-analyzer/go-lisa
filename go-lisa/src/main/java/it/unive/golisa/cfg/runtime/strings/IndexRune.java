package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.runtime.strings.Index.IndexOfImpl;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;

/**
 * func IndexRune(s string, r rune) int.
 * 
 * @link https://pkg.go.dev/strings#IndexRune
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class IndexRune extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location   the location where this native cfg is defined
	 * @param stringUnit the unit to which this native cfg belongs to
	 */
	public IndexRune(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "IndexRune", GoBoolType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE),
				new Parameter(location, "other", GoStringType.INSTANCE)),
				IndexOfImpl.class);
	}
}