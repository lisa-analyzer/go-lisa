package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go channel type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoChannelType implements GoType {

	private Type contentType;

	private boolean isSend;
	private boolean isReceive;

	private static final Set<GoChannelType> channelTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoChannelType} representing a channel type.
	 * 
	 * @param contentType the content of the channel type to lookup
	 * 
	 * @return the unique instance of {@link GoChannelType} representing the
	 *             channel type given as argument
	 */
	public static GoChannelType lookup(Type contentType) {
		GoChannelType type = new GoChannelType(contentType);
		if (!channelTypes.contains(type))
			channelTypes.add(type);
		return channelTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Builds a channel type.
	 * 
	 * @param contentType the content type
	 */
	private GoChannelType(Type contentType) {
		this(contentType, true, true);
	}

	/**
	 * Builds a channel type.
	 * 
	 * @param contentType the content type
	 * @param isSend      if this channel is a sending channel
	 * @param isReceive   if this channel is receiving channle
	 */
	public GoChannelType(Type contentType, boolean isSend, boolean isReceive) {
		this.contentType = contentType;
		this.isSend = isSend;
		this.isReceive = isReceive;
	}

	/**
	 * Yields the content type.
	 * 
	 * @return the content type
	 */
	public Type getContentType() {
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

	/**
	 * Checks if this channel is bi-directional.
	 * 
	 * @return if this channel is bi-directional
	 */
	public boolean isBidiretional() {
		return isSendDirection() && isReceiveDirection();
	}

	/**
	 * Checks if this channel is receiving.
	 * 
	 * @return if this channel is receiving
	 */
	private boolean isReceiveDirection() {
		return isReceive;
	}

	/**
	 * Checks if this channel is sending.
	 * 
	 * @return if this channel is sending
	 */
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

	/**
	 * Yields all the channel types.
	 * 
	 * @return all the channel types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoChannelType in : channelTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		return all();
	}

	/**
	 * Clears all the channel types.
	 */
	public static void clearAll() {
		channelTypes.clear();
	}
}
