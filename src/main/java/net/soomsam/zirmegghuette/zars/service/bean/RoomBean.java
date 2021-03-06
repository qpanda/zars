package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RoomBean extends BaseBean {
	private long roomId;

	private Date roomTimestamp;

	private String name;

	private long capacity;

	private long precedence;

	private boolean inUse;

	public RoomBean() {
		super();
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(final long roomId) {
		this.roomId = roomId;
	}

	public Date getRoomTimestamp() {
		return roomTimestamp;
	}

	public void setRoomTimestamp(final Date roomTimestamp) {
		this.roomTimestamp = roomTimestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(final long capacity) {
		this.capacity = capacity;
	}

	public long getPrecedence() {
		return precedence;
	}

	public void setPrecedence(final long precedence) {
		this.precedence = precedence;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(final boolean inUse) {
		this.inUse = inUse;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[] { "roomTimestamp" });
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[] { "roomTimestamp" });
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
