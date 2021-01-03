package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoChannelType implements GoType {
	
	private Type contentType;

	private boolean isSend;
	private boolean isReceive;
	

	private static final Set<GoChannelType> channelTypes = new HashSet<>();

	public static GoChannelType lookup(GoChannelType type)  {
		if (!channelTypes.contains(type))
			channelTypes.add(type);
		return channelTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoChannelType(GoType contentType) {
		this.contentType = contentType;
		this.isReceive = true;
		this.isSend = true;
	}
	
	public GoChannelType(GoType contentType, boolean isSend, boolean isReceive) {
		this.contentType = contentType;
		this.isSend = isSend;
		this.isReceive = isReceive;
	}

	public Type getBaseType() {
		return contentType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBidiretional() {
		return isSendDirection() && isReceiveDirection();
	}
	
	private boolean isReceiveDirection() {
		return isReceive;
	}

	private boolean isSendDirection() {
		return isSend;
	}

	@Override
	public String toString() {
		if (isBidiretional())
			return "chan" + contentType.toString();
		else if (isSendDirection())
			return "chan<-" + contentType.toString();
		return "<-chan" + contentType.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + (isReceive ? 1231 : 1237);
		result = prime * result + (isSend ? 1231 : 1237);
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
		GoChannelType other = (GoChannelType) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (isReceive != other.isReceive)
			return false;
		if (isSend != other.isSend)
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoNil(cfg);
	}
	
	@Override
	public boolean isGoInteger() {
		return false;
	}
}
