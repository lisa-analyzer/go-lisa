package it.unive.golisa.loader;

import it.unive.golisa.analysis.entrypoints.EntryPointSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntryPointLoader {

	List<EntryPointSet> entrypointSets;

	public EntryPointLoader() {
		entrypointSets = new ArrayList<EntryPointSet>();
	}

	public void addEntryPoints(EntryPointSet entryPoints) {
		entrypointSets.add(entryPoints);

	}

	public void load(Program program) {
		Collection<CFG> cfgs = program.getAllCFGs();

		for (CFG c : cfgs)
			for (EntryPointSet set : entrypointSets)
				for (String name : set.getEntryPoints())
					checkAndAddEntryPoint(program, c, name);
	}

	private void checkAndAddEntryPoint(Program program, CFG cfg, String name) {
		if (cfg.getDescriptor().getName().equals("Invoke") || cfg.getDescriptor().getName().equals("Init"))
			program.addEntryPoint(cfg);
	}

}
