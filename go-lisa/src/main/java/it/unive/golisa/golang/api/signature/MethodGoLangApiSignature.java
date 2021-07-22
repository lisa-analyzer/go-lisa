package it.unive.golisa.golang.api.signature;

import java.util.Arrays;

public class MethodGoLangApiSignature  extends GoLangApiSignature{

	private final String receiver;
	private final String name;
	private final String[] params;
	private final String[] ret;

	public MethodGoLangApiSignature(String pkg, String receiver, String name, String[] params, String[] ret) {
		super(pkg);
		this.receiver = receiver;
		this.name=name;
		this.params = params;
		this.ret = ret;
	}

	public String getReceiver() {
		return receiver;
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
		result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
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
		MethodGoLangApiSignature other = (MethodGoLangApiSignature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (!Arrays.equals(ret, other.ret))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String sParams = "";
		for(int i= 0; i< params.length; i++) {
			sParams += params[i];
			if(i != params.length-1)
				sParams += ", ";
		}
		
		String sRets = "";
		for(int i= 0; i< ret.length; i++) {
			sRets += ret[i];
			if(i != ret.length-1)
				sRets += ", ";
		}
		
		if( ret.length > 1)
			sRets += "("+sRets+")"; 
		
		return super.toString() + ", method (" + receiver +") " + name + "(" + sParams + ") "+ sRets;
	}
}
