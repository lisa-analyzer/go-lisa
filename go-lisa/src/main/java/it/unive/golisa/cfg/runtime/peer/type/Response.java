package it.unive.golisa.cfg.runtime.peer.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Random generator
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-protos-go/peer#Response
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Response extends GoStructType {

	public static final Response INSTANCE = new Response();

	private Response() {
		this("Response", buildResponseUnit());
	}

	private Response(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildResponseUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "Response", false);
		return randUnit;
	}

	public static void registerMethods() {
		//TODO
	}

	@Override
	public String toString() {
		return "pb.Response";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
