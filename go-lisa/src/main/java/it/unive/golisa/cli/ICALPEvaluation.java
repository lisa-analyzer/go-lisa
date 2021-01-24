package it.unive.golisa.cli;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import it.unive.golisa.analysis.ICALPResult;
import it.unive.golisa.analysis.RSubs;
import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class ICALPEvaluation {


	public static void main(String[] args) throws AnalysisSetupException {
		if (args == null || args[0] == null) {
			System.err.println("Input file is missing. Exiting.");
			return;
		}

		String filePath = args[0];

		if (args.length < 1) {
			System.err.println("Output directory missing. Exiting.");
			return;
		}

		String outputDir = args[1];

		Program program = null;
		boolean cfgCreated = true;
		boolean analyzedByTarsis = true;
		boolean analyzedByRSubs = true;


		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();


		try {
			program = GoFrontEnd.processFile(filePath);
		} catch (ParseCancellationException e) {
			// a parsing  error occurred 
			dumpXml(filePath, outputDir, e + " " + e.getStackTrace()[0].toString(), false, false, false, false);
			return;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath + " does not exists. Exiting");
			return;
		} catch (UnsupportedOperationException e1) {
			// an unsupported operations has been encountered
			e1.printStackTrace();
			dumpXml(filePath, outputDir, e1.getMessage());
			return;
		} catch (Exception e2) {
			// other exception
			e2.printStackTrace();
			dumpXml(filePath, outputDir, e2 + " " + e2.getStackTrace()[0].toString());
			return;
		}


		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);

		lisa.setWorkdir(outputDir + "/cfg");
		lisa.setDumpCFGs(true);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			// an error occurred during the analysis
			e.printStackTrace();
			analyzedByTarsis = false;
		} 

		cfgCreated = true;

		lisa.setWorkdir(outputDir + "/tarsis");

		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Tarsis()));
		lisa.setDumpAnalysis(true);

		try {
			lisa.run();
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			analyzedByTarsis = false;
			dumpXml(filePath, outputDir, e + " " + e.getStackTrace()[0].toString(), true, cfgCreated, false, false);
			return;
		} 

		lisa.setWorkdir(outputDir + "/rsubs");
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RSubs()));


		try {
			lisa.run();
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			analyzedByRSubs = false;
			dumpXml(filePath, outputDir, e + " " + e.getStackTrace()[0].toString(), true, cfgCreated, analyzedByTarsis, analyzedByRSubs);
			return;
		} 

		dumpXml(filePath, outputDir, null, true, cfgCreated, analyzedByTarsis, analyzedByRSubs);
	}

	public static void dumpXml(String filePath, String outputDir, String error, boolean parsed, boolean cfgCreated, boolean analyzedByTarsis, boolean analyzedByRSubs) {

		ICALPResult analysisResult = new ICALPResult();
		if (error != null)
			analysisResult.setError(error);
		analysisResult.setParsed(parsed);
		analysisResult.setAnalyzedByRSub(analyzedByRSubs);
		analysisResult.setAnalyzedByTarsis(analyzedByTarsis);
		analysisResult.setCfgCreated(cfgCreated);
		analysisResult.setFilePath(filePath);

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


	public static void dumpXml(String filePath, String outputDir, String unsupportedOp) {

		ICALPResult analysisResult = new ICALPResult();
		analysisResult.setParsed(true);
		analysisResult.setUnsupportedOp(unsupportedOp);
		analysisResult.setAnalyzedByRSub(false);
		analysisResult.setAnalyzedByTarsis(false);
		analysisResult.setCfgCreated(false);
		analysisResult.setFilePath(filePath);

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
