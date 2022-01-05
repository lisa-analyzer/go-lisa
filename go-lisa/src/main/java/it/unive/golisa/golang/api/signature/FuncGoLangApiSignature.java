package it.unive.golisa.golang.api.signature;

import java.util.Arrays;

public class FuncGoLangApiSignature extends GoLangApiSignature {

	private final String name;
	private final String[] params;
	private final String[] ret;

	public FuncGoLangApiSignature(String pkg, String name, String[] params, String[] ret) {
		super(pkg);
		this.name = name;
		this.params = params;
		this.ret = ret;
	}

	public String getName() {
		return name;
	}

	public String[] getParams() {
		return params;
	}

	public String[] getRet() {
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(params);
		result = prime * result + Arrays.hashCode(ret);
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
		FuncGoLangApiSignature other = (FuncGoLangApiSignature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (!Arrays.equals(ret, other.ret))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String sParams = "";
		for (int i = 0; i < params.length; i++) {
			sParams += params[i];
			if (i != params.length - 1)
				sParams += ", ";
		}

		String sRets = "";
		for (int i = 0; i < ret.length; i++) {
			sRets += ret[i];
			if (i != ret.length - 1)
				sRets += ", ";
		}

		if (ret.length > 1)
			sRets += "(" + sRets + ")";

		return super.toString() + ", func " + name + "(" + sParams + ") " + sRets;
	}

}
