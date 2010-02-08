package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.event.JPAValidateListener;

@Entity
@Table(name = Room.TABLENAME_ROOM)
@EntityListeners(value = { JPAValidateListener.class })
public class Room extends BaseEntity {
	public static final String TABLENAME_ROOM = "room";
	public static final String COLUMNNAME_ROOMID = "room_id";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_DESCRIPTION = "description";
	public static final String COLUMNNAME_CAPACITY = "capacity";
	public static final String COLUMNNAME_PRECEDENCE = "precedence";
	public static final String COLUMNNAME_INUSE = "in_use";

	@Id
	@GeneratedValue
	@Column(name = Room.COLUMNNAME_ROOMID, unique = true, nullable = false)
	private long roomId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@NotEmpty
	@Column(name = Room.COLUMNNAME_NAME, nullable = false, length = 256)
	private String name;

	@Min(value = 1)
	@Column(name = Room.COLUMNNAME_CAPACITY, nullable = false, length = 256)
	private long capacity;

	@Min(value = 1)
	@Column(name = Room.COLUMNNAME_PRECEDENCE, unique = true, nullable = false)
	private long precedence;

	@Column(name = Room.COLUMNNAME_INUSE, nullable = false)
	private boolean inUse;

	protected Room() {
		super();
	}

	public Room(final String name, final long capacity, final long precedence, final boolean inUse) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.precedence = precedence;
		this.inUse = inUse;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(final long roomId) {
		this.roomId = roomId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
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
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Room)) {
			return false;
		}

		final Room other = (Room) entity;
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Room)) {
			return false;
		}

		final Room other = (Room) obj;
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).append(getName(), other.getName()).append(getCapacity(), other.getCapacity()).append(getPrecedence(), other.getPrecedence()).append(isInUse(), other.isInUse()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRoomId()).append(getName()).append(getCapacity()).append(getPrecedence()).append(isInUse()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getRoomId()).append(getName()).append(getCapacity()).append(getPrecedence()).append(isInUse()).toString();
	}
}
