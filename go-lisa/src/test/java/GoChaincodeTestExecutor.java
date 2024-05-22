import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;

import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.lisa.program.Program;

public abstract class GoChaincodeTestExecutor extends GoAnalysisTestExecutor {

	@Override
	protected Program readProgram(Path target, CronConfiguration conf) {
		Program program = null;
		try {
			program = GoFrontEnd.processFile(target.toString());
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(conf.annSet);
			annotationLoader.load(program);

			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints("HYPERLEDGER-FABRIC"));
			entryLoader.load(program);

		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception while parsing '" + target + "': " + e.getMessage());
		}
		return program;
	}
}