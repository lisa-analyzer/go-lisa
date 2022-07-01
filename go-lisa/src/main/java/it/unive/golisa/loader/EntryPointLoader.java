package it.unive.golisa.loader;

import it.unive.golisa.analysis.entrypoints.EntryPointSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The loader of entrypoints in programs
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 *
 */
public class EntryPointLoader implements Loader {

	/**
	 * The sets of entry points to load
	 */
	List<EntryPointSet> entrypointSets;

	/**
	 * The field indicates after a load if there is no entry loaded in the program
	 */
	boolean noEntry;

	public EntryPointLoader() {
		entrypointSets = new ArrayList<EntryPointSet>();
		noEntry = true;
	}

	/**
	 * The method add the entry points to load.
	 * @param entryPoints the entry points to add in the loader.
	 */
	public void addEntryPoints(EntryPointSet entryPoints) {
		entrypointSets.add(entryPoints);

	}

	@Override
	public void load(Program program) {
		Collection<CFG> cfgs = program.getAllCFGs();

		for (CFG c : cfgs)
			if (entrypointSets.stream().anyMatch(set -> set.getEntryPoints().contains(c.getDescriptor().getName()))) {
				program.addEntryPoint(c);
				noEntry = false;
			}
	}

	/**
	 * After a load, yields true if found an entry point in the program
	 * @return true, if found an entry point in the program after a load
	 */
	public boolean isEntryFound() {
		return !noEntry;
	}

}
