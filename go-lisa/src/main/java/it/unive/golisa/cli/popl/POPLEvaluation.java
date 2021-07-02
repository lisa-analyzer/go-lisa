package it.unive.golisa.cli.popl;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import it.unive.golisa.analysis.ModularWorstCaseWithNativeCalls;
import it.unive.golisa.analysis.composition.RelTarsis;
import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.golisa.cli.GoFrontEnd;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.MonolithicHeap;
import it.unive.lisa.analysis.impl.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.interprocedural.callgraph.impl.RTACallGraph;
import it.unive.lisa.program.Program;


public class POPLEvaluation {

	public static void main(String[] args) throws JAXBException, AnalysisSetupException {

		if (args == null || args[0] == null) {
			System.err.println("Input file is missing. Exiting.");
			return;
		}

		String filePath = args[0];

		if (args.length < 2) {
			System.err.println("Output directory is missing. Exiting.");
			return;
		}

		String outputDir = args[1];
		POPLResult result = new POPLResult();
		Program progRelTarsis = null;
		Program progTarsis = null;

		result.setErrorReturned("no error");
		result.setAnalyzedByRSubs(false);
		result.setAnalyzedByTarsis(false);
		result.setCfgCreated(false);
		result.setLocs(0);

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		try {

			if (args.length == 2 || args[2].equals("-tarsis"))
				progTarsis = GoFrontEnd.processFile(filePath);
			if (args.length == 2 || args[2].equals("-reltarsis"))
				progRelTarsis = GoFrontEnd.processFile(filePath);

			result.setCfgCreated(true);
		} catch (ParseCancellationException e) {
			result.setErrorReturned(e + " " + e.getStackTrace()[0].toString());
			// a parsing  error occurred 
			System.err.println("Parsing error.");
			jaxbObjectToXML(result, outputDir);
			return;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath +  "does not exist.");
			result.setErrorReturned(e + " " + e.getStackTrace()[0].toString());
			jaxbObjectToXML(result, outputDir);
			return;
		} catch (UnsupportedOperationException e1) {
			// an unsupported operations has been encountered
			System.err.println(e1 + " " + e1.getStackTrace()[0].toString());
			result.setErrorReturned(e1 + " " + e1.getStackTrace()[0].toString());
			jaxbObjectToXML(result, outputDir);
			e1.printStackTrace();
			return;
		} catch (Exception e2) {
			// other exception
			e2.printStackTrace();
			System.err.println();
			result.setErrorReturned(e2 + " " + e2.getStackTrace()[0].toString());
			jaxbObjectToXML(result, outputDir);
			return;
		}

		LiSAConfiguration conf = new LiSAConfiguration();
		LiSA lisa = new LiSA(conf);

		if (args.length == 2 || args[2].equals("-tarsis")) {

			/**
			 * Tarsis  analysis
			 */
			conf.setWorkdir(outputDir + "/tarsis").setInferTypes(true)
			.setAbstractState(getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Tarsis()))
			.setDumpAnalysis(true)
			.setCallGraph(new RTACallGraph())
			.setInterproceduralAnalysis(new ModularWorstCaseWithNativeCalls<>());
			lisa = new LiSA(conf);

			try {
				lisa.run(progTarsis);
				result.setAnalyzedByTarsis(true);
			} catch (Exception e) {
				// an error occurred during the analysis
				e.printStackTrace();
				result.setErrorReturned(e.getMessage());
				jaxbObjectToXML(result, outputDir);
				return;
			} 
		}

		if (args.length == 2 || args[2].equals("-reltarsis")) {

			/**
			 * RelTarsis  analysis
			 */
			conf = new LiSAConfiguration();
			conf.setWorkdir(outputDir + "/relTarsis").setInferTypes(true)
			.setAbstractState(getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new RelTarsis()))
			.setCallGraph(new RTACallGraph())
			.setInterproceduralAnalysis(new ModularWorstCaseWithNativeCalls<>())
			.setDumpAnalysis(true);

			lisa = new LiSA(conf);

			try {
				lisa.run(progRelTarsis);
				result.setAnalyzedByRSubs(true);
				result.setLocs(countLineBufferedReader(filePath));
			} catch (Exception e) {
				// an error occurred during the analysis
				e.printStackTrace();
				result.setErrorReturned(e.getMessage());
				jaxbObjectToXML(result, outputDir);
				return;
			} 
		}

		jaxbObjectToXML(result, outputDir);
	}

	public static int countLineBufferedReader(String fileName) {

		int lines = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			while (reader.readLine() != null) lines++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;

	}

	private static void jaxbObjectToXML(POPLResult result, String output) throws JAXBException {
		File file = new File(output + "/result.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(POPLResult.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(result, file);
	}
}
