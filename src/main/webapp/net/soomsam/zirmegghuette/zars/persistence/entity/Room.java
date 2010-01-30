package net.soomsam.zirmegghuette.zars.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = Room.TABLENAME_ROOM)
public class Room extends BaseEntity {
	public static final String TABLENAME_ROOM = "room";
	public static final String COLUMNNAME_ROOMID = "room_id";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_DESCRIPTION = "description";
	public static final String COLUMNNAME_CAPACITY = "capacity";
	public static final String COLUMNNAME_PRECEDENCE = "precedence";

	@Id
	@GeneratedValue
	@Column(name = Room.COLUMNNAME_ROOMID, unique = true, nullable = false)
	private long roomId;

	@Column(name = Room.COLUMNNAME_NAME, nullable = false, length = 256)
	private String name;

	@Column(name = Room.COLUMNNAME_DESCRIPTION, nullable = true, length = 1024)
	private String description;

	@Column(name = Room.COLUMNNAME_CAPACITY, nullable = false, length = 256)
	private long capacity;

	@Column(name = Room.COLUMNNAME_PRECEDENCE, unique = true, nullable = false)
	private long precedence;

	public Room(final String name, final long capacity, final long precedence) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.precedence = precedence;
	}

	public Room(final String name, final String description, final long capacity, final long precedence) {
		super();
		this.name = name;
		this.description = description;
		this.capacity = capacity;
		this.precedence = precedence;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(final long roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Room)) {
			return false;
		}

		final Room other = (Room) obj;
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRoomId()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getRoomId()).append(getName()).append(getDescription()).append(getCapacity()).append(getPrecedence()).toString();
	}
}
