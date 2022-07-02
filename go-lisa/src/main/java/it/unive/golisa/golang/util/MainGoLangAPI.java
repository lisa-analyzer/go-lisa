package it.unive.golisa.golang.util;

import java.util.List;

public class MainGoLangAPI {

	public static void main(String[] args) {
		/*
		 * Map<String, Set<MethodGoLangApiSignature>> methods =
		 * GoLangUtils.getGoLangApiMethodSignatures(); Map<String,
		 * Set<FuncGoLangApiSignature>> functions =
		 * GoLangUtils.getGoLangApiFunctionSignatures(); Map<String,
		 * Set<MethodGoLangApiSignature>> methods_noDup = new HashMap<>();
		 * methods.entrySet().forEach(c -> { String key = c.getKey();
		 * if(c.getKey().contains("syscall (")) key= "syscall";
		 * if(c.getKey().contains("runtime/cgo (")) key= "runtime/cgo";
		 * if(c.getKey().contains("log/syslog (")) key= "log/syslog";
		 * if(!methods_noDup.containsKey(key)) methods_noDup.put(key, new
		 * HashSet<>()); for( MethodGoLangApiSignature m : c.getValue())
		 * methods_noDup.get(key).add(new MethodGoLangApiSignature(key,
		 * m.getReceiver(), m.getName(), m.getParams(), m.getRet())); });
		 * Map<String, Set<FuncGoLangApiSignature>> functions_noDup = new
		 * HashMap<>(); functions.entrySet().forEach(c -> { String key =
		 * c.getKey(); int index = StringUtils.indexOf(c.getKey(), " (");
		 * if(index > -1) key= c.getKey().substring(0, index);
		 * if(!functions_noDup.containsKey(key)) functions_noDup.put(key, new
		 * HashSet<>()); for(FuncGoLangApiSignature f : c.getValue())
		 * functions_noDup.get(key).add(new FuncGoLangApiSignature(key,
		 * f.getName(), f.getParams(), f.getRet())); }); int m_error_counter =
		 * 0; int m_counter=0; for( Entry<String, Set<MethodGoLangApiSignature>>
		 * ms : methods_noDup.entrySet()) if(checkPackage(ms.getKey()) &&
		 * !ms.getValue().isEmpty()) { for(MethodGoLangApiSignature m :
		 * ms.getValue()) { if(m.getRet().length > 0) { if(m.getRet().length ==
		 * 1 && (m.getRet()[0].equals(")") || m.getRet()[0].equals("")))
		 * continue; if(Arrays.toString(m.getRet()).contains("error"))
		 * m_error_counter++; System.out.println(m); m_counter++; } } //pkg
		 * time, func Until(Time) Duration } int f_error_counter = 0; int
		 * f_counter=0; for( Entry<String, Set<FuncGoLangApiSignature>> fs :
		 * functions_noDup.entrySet()) if(checkPackage(fs.getKey()) &&
		 * !fs.getValue().isEmpty()) { for( FuncGoLangApiSignature f :
		 * fs.getValue()) { if(f.getRet().length > 0 ) { if(f.getRet().length ==
		 * 1 && (f.getRet()[0].equals(")") ||f.getRet()[0].equals("")))
		 * continue; if(Arrays.toString(f.getRet()).contains("error"))
		 * f_error_counter++; System.out.println(f); f_counter++; } } }
		 * System.out.println("####### methods: "+ m_counter +
		 * ", ret errors = "+ m_error_counter+" ####### functions: "+ f_counter
		 * + ", ret errors = "+ f_error_counter);
		 */
	}

	private static List<String> blackList() {
		return List.of("math/rand", "crypto/rand", "io", "embed", "archive", "compress", "os", "time", "syscall",
				"internal", "database", "net");
	}

	private static boolean checkPackage(String key) {

		return blackList().stream().anyMatch(t -> key.contains(t));
	}

}
