package it.unive.golisa.cli;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import it.unive.golisa.analysis.ICALPResult;
import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class TarsisEval {

	public static void main(String[] args) throws IOException, AnalysisSetupException {
		if (args == null || args[0] == null) {
			System.err.println("Input file is missing. Exiting.");
			return;
		}

		String filePath = args[0];

		if (args.length < 1) {
			dumpXml(filePath, "", "Output directory missing. Exiting.", false, false, false, false, 0);
			return;
		}

		String outputDir = args[1];

		Program program = null;

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		long start = System.currentTimeMillis(); 

		try {
			program = GoFrontEnd.processFile(filePath);
		} catch (ParseCancellationException e) {
			// a parsing  error occurred 
			dumpXml(filePath, outputDir, e + " " + e.getStackTrace()[0].toString(), false, false, false, false, 0);
			return;
		} catch (IOException e) {
			// the file does not exists
			return;
		} catch (UnsupportedOperationException e1) {
			// an unsupported operations has been encountered
			dumpXml(filePath, outputDir, e1 + " " + e1.getStackTrace()[0].toString(), true, true, false, false, 0);
			e1.printStackTrace();
			return;
		} catch (Exception e2) {
			// other exception
			e2.printStackTrace();
			dumpXml(filePath, outputDir, e2 + " " + e2.getStackTrace()[0].toString(), true, true, false, false, 0);
			return;
		}

		LiSA lisa = new LiSA();
		lisa.setProgram(program);
		lisa.setWorkdir(outputDir);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Tarsis()));
		lisa.setDumpAnalysis(false);

		try {
			lisa.run();
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			dumpXml(filePath, outputDir, e + " " + e.getStackTrace()[0].toString(), true, true, false, false, 0);
			return;
		} 
		
		long end = System.currentTimeMillis(); 
		dumpXml(filePath, outputDir, "", true, true, true, true, end - start);
	}


	public static void dumpXml(String filePath, String outputDir, String error, boolean parsed, boolean cfgCreated, boolean analyzedByTarsis, boolean analyzedByRSubs, long time) {

		ICALPResult analysisResult = new ICALPResult();
		if (error != null)
			analysisResult.setError(error);
		analysisResult.setParsed(parsed);
		analysisResult.setAnalyzedByRSub(analyzedByRSubs);
		analysisResult.setAnalyzedByTarsis(analyzedByTarsis);
		analysisResult.setCfgCreated(cfgCreated);
		analysisResult.setFilePath(filePath);
		analysisResult.setTime(time);

		String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.lastIndexOf("."));
		
		try {

			File file = new File(outputDir + "/" + fileName + ".xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(ICALPResult.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(analysisResult, file);
			jaxbMarshaller.marshal(analysisResult, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
