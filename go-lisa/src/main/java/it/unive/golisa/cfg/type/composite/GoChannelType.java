package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GoChannelType implements GoType {

	private Type contentType;

	private boolean isSend;
	private boolean isReceive;

	private static final Set<GoChannelType> channelTypes = new HashSet<>();

	public static GoChannelType lookup(GoChannelType type) {
		if (!channelTypes.contains(type))
			channelTypes.add(type);
		return channelTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoChannelType(Type contentType) {
		this.contentType = contentType;
		this.isReceive = true;
		this.isSend = true;
	}

	public GoChannelType(Type contentType, boolean isSend, boolean isReceive) {
		this.contentType = contentType;
		this.isSend = isSend;
		this.isReceive = isReceive;
	}

	public Type getBaseType() {
		return contentType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoChannelType)
			return contentType.canBeAssignedTo(((GoChannelType) other).contentType);
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoChannelType)
			if (contentType.canBeAssignedTo(((GoChannelType) other).contentType))
				return other;
		return Untyped.INSTANCE;
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
			return "chan " + contentType.toString();
		else if (isSendDirection())
			return "chan <-" + contentType.toString();
		return "<- chan" + contentType.toString();
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
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoChannelType in : channelTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoChannelType in : channelTypes)
			instances.add(in);
		return instances;
	}
}
