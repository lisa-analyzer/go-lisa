package it.unive.golisa.cfg.runtime.io.fs.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A FileInfo type.
 * 
 * @link https://pkg.go.dev/io/fs#FileInfo
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class FileInfo extends GoStructType {

//	/**
//	 * Unique instance of {@link FileInfo} type.
//	 */
//	public static final FileInfo INSTANCE = new FileInfo();
//
//	private FileInfo() {
//		this("FileInfo", buildFileUnit());
//	}

	private FileInfo(String name, CompilationUnit unit) {
		super(name, unit);
	}

//	private static CompilationUnit buildFileUnit() {
//		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
//		
//	}

	/**
	 * Registers the methods of the {@link FileInfo} type.
	 */
	public static void registerMethods() {
		// TODO
	}

	@Override
	public String toString() {
		return "fs.FileInfo";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	public static FileInfo getFileInfoType(Program program) {
		ClassUnit fileInfoUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "FileInfo", false);
		return new FileInfo("FileInfo", fileInfoUnit);
	}
}
