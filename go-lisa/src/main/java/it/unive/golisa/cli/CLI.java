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
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class CLI {

	public static void main(String[] args) throws  AnalysisSetupException, IOException {

		String outputDir = "tmp";
		String filePath = "go-testcases/tarsis/tarsis.go";
		Program program = null;
		boolean cfgCreated = true;
		boolean analyzedByTarsis = true;
		boolean analyzedByRSubs = false;
		
		try {
			program = GoFrontEnd.processFile(filePath);
		} catch (ParseCancellationException e) {
			// a parsing  error occurred 
			dumpXml(filePath, outputDir, false, false, false, false);
			return;
		} 
		
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Tarsis()));
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir(outputDir);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			// an error occurred during the analysis
			e.printStackTrace();
			analyzedByTarsis = false;
			analyzedByRSubs = false;
		} catch (UnsupportedOperationException e) {
			// an unsupported operations has been analyzed
			e.printStackTrace();
			analyzedByTarsis = false;
			analyzedByRSubs = false;
		}	
		
		dumpXml(filePath, outputDir, true, cfgCreated, analyzedByTarsis, analyzedByRSubs);
	}

	public static void dumpXml(String filePath, String outputDir, boolean parsed, boolean cfgCreated, boolean analyzedByTarsis, boolean analyzedByRSubs) {

		ICALPResult analysisResult = new ICALPResult();
		analysisResult.setParsed(parsed);
		analysisResult.setAnalyzedByRSub(analyzedByRSubs);
		analysisResult.setAnalyzedByTarsis(analyzedByTarsis);
		analysisResult.setCfgCreated(cfgCreated);
		analysisResult.setFilePath(filePath);

		try {

			File file = new File(outputDir + "/analysis-result.xml");
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
