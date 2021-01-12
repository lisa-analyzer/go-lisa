package it.unive.golisa.cfg.statement.method;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;

public class GoMethod extends CFG {

	private GoReceiver receiver;
	
	public GoMethod(CFGDescriptor other, GoReceiver receiver) {
		super(other);
		this.receiver = receiver;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
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
		GoMethod other = (GoMethod) obj;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		return true;
	}
}
