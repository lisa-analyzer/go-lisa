package it.unive.golisa.golang.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.golang.api.signature.ConstGoLangApiSignature;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.api.signature.TypeGoLangApiSignature;
import it.unive.golisa.golang.api.signature.VarGoLangApiSignature;

public class GoLangAPISignatureLoader {

	Map<String, ? extends Set<GoLangApiSignature>> loadedAPIs;
	
	Map<String, Set<MethodGoLangApiSignature>> methodsAPIs;
	Map<String, Set<FuncGoLangApiSignature>> functionsAPIs;
			
	public GoLangAPISignatureLoader(URL url) throws FileNotFoundException, IOException {
		loadFile(url);
	}
	
	private void loadFile(URL url) throws FileNotFoundException, IOException {
		loadedAPIs = parseGoAPIFile(new FileInputStream(url.getPath()));
	}

	private static Map<String, ? extends Set<GoLangApiSignature>> parseGoAPIFile(InputStream inputStream)
			throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String s;
		while ((s = br.readLine()) != null)
			lines.add(s);
		br.close();

		Map<String, Set<GoLangApiSignature>> tmp = new HashMap<>();
		boolean comment = false;
		for (String line : lines) {
			if (line.contains("/*"))
				comment = true;
			else if (line.contains("*/"))
				comment = false;

			if (!comment) {
				line = cleanLine(line);
				String pkg = parsePackage(line);
				if (pkg != null) {
					GoLangApiSignature signature = parseSignature(line);
					if (signature != null) {
						tmp.putIfAbsent(pkg, new HashSet<>());
						tmp.get(pkg).add(signature);
					}
				}
			}
		}

		return reduceMap(tmp);

	}

	private static String parsePackage(String s) {
		Pattern p = Pattern.compile("pkg (.*), (func|method|type|var|const)");
		Matcher m = p.matcher(s);

		if (m.find())
			return m.group(1);
		return null;
	}

	/**
	 * Information of type struct and const are write some times on multiple
	 * lines in the files. This method allows to merge those information
	 * reducing the dimension of map
	 * 
	 * @param map the map to reduce
	 * 
	 * @return the reduced map
	 */
	private static Map<String, ? extends Set<GoLangApiSignature>> reduceMap(
			Map<String, Set<GoLangApiSignature>> map) {

		// TODO: merge type and const
		return map;
	}

	/**
	 * Clean a line from comments
	 * 
	 * @param line the line to clean
	 * 
	 * @return the cleaned line
	 */
	private static String cleanLine(String line) {
		int index = StringUtils.indexOf(line, "//");
		return index == -1 ? line : line.substring(0, index);
	}

	private final static String regex_func = "pkg (.*), func ([a-zA-Z0-9_]*)\\((.*?)\\) ?(.*)";
	private final static String regex_method = "pkg (.*), method (\\(.*?\\)) ([a-zA-Z0-9_]*)\\((.*?)\\) ?(.*)";
	private final static String regex_const = "pkg (.*), const ([^=]*) [= ]?(.*)";
	private final static String regex_var = "pkg (.*), var (.*) (.*)";
	private final static String regex_type = "pkg (.*), type (.*)";

	private static GoLangApiSignature parseSignature(String line) {

		if (line.matches(regex_method))
			return parseMethodGoLangApiSignature(line);
		else if (line.matches(regex_func))
			return parseFuncGoLangApiSignature(line);
		else if (line.matches(regex_const))
			return parseConstGoLangApiSignature(line);
		else if (line.matches(regex_var))
			return parseVarGoLangApiSignature(line);
		else if (line.matches(regex_type))
			return parseTypeGoLangApiSignature(line);

		throw new UnsupportedOperationException("Unable to parse correct family of GoLang API");
	}

	private static GoLangApiSignature parseTypeGoLangApiSignature(String s) {
		Pattern p = Pattern.compile(regex_type);
		Matcher m = p.matcher(s);
		if (m.matches())
			return new TypeGoLangApiSignature(m.group(1), m.group(2));
		return null;
	}

	private static GoLangApiSignature parseVarGoLangApiSignature(String s) {
		Pattern p = Pattern.compile(regex_var);
		Matcher m = p.matcher(s);
		if (m.matches())
			return new VarGoLangApiSignature(m.group(1), m.group(2), m.group(3));
		return null;
	}

	private static GoLangApiSignature parseConstGoLangApiSignature(String s) {
		Pattern p = Pattern.compile(regex_const);
		Matcher m = p.matcher(s);
		if (m.matches())
			if (s.contains("="))
				return new ConstGoLangApiSignature(m.group(1), m.group(2), m.group(3), null);
			else
				return new ConstGoLangApiSignature(m.group(1), m.group(2), null, m.group(3));
		return null;
	}

	private static GoLangApiSignature parseMethodGoLangApiSignature(String s) {
		Pattern p = Pattern.compile(regex_method);
		Matcher m = p.matcher(s);
		if (m.matches())
			return new MethodGoLangApiSignature(m.group(1), m.group(2).replace("(", "").replace(")", ""), m.group(3),
					m.group(4).contains(",") ? m.group(4).split(",") : new String[] { m.group(4) },
					m.group(5).contains(",") ? m.group(5).replace("(", "").replace(")", "").split(",")
							: new String[] { m.group(5) });
		return null;
	}

	private static GoLangApiSignature parseFuncGoLangApiSignature(String s) {
		Pattern p = Pattern.compile(regex_func);
		Matcher m = p.matcher(s);
		if (m.matches())
			return new FuncGoLangApiSignature(m.group(1), m.group(2),
					m.group(3).contains(",") ? m.group(3).split(",") : new String[] { m.group(3) },
					m.group(4).contains(",") ? m.group(4).replace("(", "").replace(")", "").split(",")
							: new String[] { m.group(4) });
		return null;
	}
	
	public Map<String, ? extends Set<MethodGoLangApiSignature>> getMethodAPIs() {
		if(methodsAPIs == null)
			computeMethodsAPIs();
		return methodsAPIs;
	}
	
	
	private void computeMethodsAPIs() {
		methodsAPIs = new HashMap<>();
		for(Entry<? extends String, ? extends Set<GoLangApiSignature>> e : loadedAPIs.entrySet())
			for(GoLangApiSignature sig :  e.getValue())
				if(sig instanceof MethodGoLangApiSignature){
					methodsAPIs.putIfAbsent(e.getKey(), new HashSet<>());
					methodsAPIs.get(e.getKey()).add((MethodGoLangApiSignature) sig);
				}
		
	}

	public Map<String, ? extends Set<FuncGoLangApiSignature>> getFunctionAPIs() {
		if(functionsAPIs == null)
			computeFunctionsAPIs();
		return functionsAPIs;
	}
	
	private void computeFunctionsAPIs() {
		functionsAPIs = new HashMap<>();
		for(Entry<? extends String, ? extends Set<GoLangApiSignature>> e : loadedAPIs.entrySet())
			for(GoLangApiSignature sig :  e.getValue())
				if(sig instanceof FuncGoLangApiSignature){
					functionsAPIs.putIfAbsent(e.getKey(), new HashSet<>());
					functionsAPIs.get(e.getKey()).add((FuncGoLangApiSignature) sig);
				}
	}

}
