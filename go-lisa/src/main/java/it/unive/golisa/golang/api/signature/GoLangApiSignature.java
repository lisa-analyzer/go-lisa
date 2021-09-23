package it.unive.golisa.golang.api.signature;

public abstract class GoLangApiSignature {
	
	private final String pkg;
	
	public GoLangApiSignature(String pkg) {
		this.pkg = pkg;
	}

	public final String getPackage() {
		return pkg;
	}

	@Override
	public String toString() {
		return "pkg " + pkg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoLangApiSignature other = (GoLangApiSignature) obj;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		return true;
	}

}
