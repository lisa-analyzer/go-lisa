package it.unive.golisa.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import it.unive.golisa.analysis.entrypoints.EntryPointSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;

public class EntryPointLoader implements Loader {

	List<EntryPointSet> entrypointSets;
	
	boolean noEntry;

	public EntryPointLoader() {
		entrypointSets = new ArrayList<EntryPointSet>();
		noEntry = true;
	}

	public void addEntryPoints(EntryPointSet entryPoints) {
		entrypointSets.add(entryPoints);

	}

	@Override
	public void load(Program program) {
		Collection<CFG> cfgs = program.getAllCFGs();

		for (CFG c : cfgs)
			if (c.getDescriptor().getName().equals("Invoke") || c.getDescriptor().getName().equals("Init")) {
				program.addEntryPoint(c);
				noEntry = false;
			}
	}
	
	public boolean isEntryFound() {
		return !noEntry;
	}

}
