package it.unive.golisa.golang.api.signature;

public class TypeGoLangApiSignature  extends GoLangApiSignature{

	final String fullTypeSignature; //TODO: missing parsing
	
	public TypeGoLangApiSignature(String pkg, String fullTypeSignature) {
		super(pkg);
		
		this.fullTypeSignature = fullTypeSignature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fullTypeSignature == null) ? 0 : fullTypeSignature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeGoLangApiSignature other = (TypeGoLangApiSignature) obj;
		if (fullTypeSignature == null) {
			if (other.fullTypeSignature != null)
				return false;
		} else if (!fullTypeSignature.equals(other.fullTypeSignature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + " " + fullTypeSignature;
	}

	public String getFullTypeSignature() {
		return fullTypeSignature;
	}
	
	
}
