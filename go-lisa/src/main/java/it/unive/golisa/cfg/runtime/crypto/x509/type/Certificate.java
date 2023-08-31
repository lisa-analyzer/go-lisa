package it.unive.golisa.cfg.runtime.crypto.x509.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Certificate type.
 * 
 * @link https://pkg.go.dev/crypto/x509#Certificate
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Certificate extends GoStructType {

	/**
	 * Unique instance of File type.
	 */
	private static Certificate INSTANCE;

	private Certificate(CompilationUnit unit) {
		super("Certificate", unit);
	}

	/**
	 * Yields the {@link Certificate} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Certificate} type
	 */
	public static Certificate getCertificateFile(Program program) {
		if (INSTANCE == null) {
			ClassUnit certificateUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Certificate", false);
			INSTANCE = new Certificate(certificateUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "x509.Certificate";
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
