package it.unive.golisa.golang.api.signature;

public class ConstGoLangApiSignature extends GoLangApiSignature {

	private final String name;
	private final String value;
	private final String type;

	public ConstGoLangApiSignature(String pkg, String name, String value, String type) {
		super(pkg);
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ConstGoLangApiSignature other = (ConstGoLangApiSignature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", const " + type == null ? ""
				: (type + " ") + name + value == null ? "" : (" = " + value);
	}

}
