package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = Room.TABLENAME_ROOM)
@NamedQueries({ @NamedQuery(name = Room.FINDROOM_INUSE_QUERYNAME, query = Room.FINDROOM_INUSE_QUERYSTRING) })
public class Room extends BaseEntity {
	public static final String TABLENAME_ROOM = "zars_room";
	public static final String COLUMNNAME_ROOMID = "room_id";
	public static final String COLUMNNAME_ROOMTIMESTAMP = "room_timestamp";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_DESCRIPTION = "description";
	public static final String COLUMNNAME_CAPACITY = "capacity";
	public static final String COLUMNNAME_PRECEDENCE = "precedence";
	public static final String COLUMNNAME_INUSE = "in_use";

	public static final int COLUMNLENGTH_NAME = 128;

	public static final String FINDROOM_INUSE_QUERYNAME = "Room.findRoomInUseQuery";
	public static final String FINDROOM_INUSE_QUERYSTRING = "from Room where inUse = :inUse order by precedence asc";

	@Id
	@GeneratedValue
	@Column(name = Room.COLUMNNAME_ROOMID, unique = true, nullable = false)
	private long roomId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Room.COLUMNNAME_ROOMTIMESTAMP, nullable = false)
	private Date roomTimestamp;

	@NotNull
	@NotEmpty
	@Column(name = Room.COLUMNNAME_NAME, unique = true, nullable = false, length = Room.COLUMNLENGTH_NAME)
	private String name;

	@Min(value = 1)
	@Column(name = Room.COLUMNNAME_CAPACITY, nullable = false)
	private long capacity;

	@Min(value = 1)
	@Column(name = Room.COLUMNNAME_PRECEDENCE, unique = true, nullable = false)
	private long precedence;

	@Column(name = Room.COLUMNNAME_INUSE, nullable = false)
	private boolean inUse;

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "rooms")
	private final Set<GroupReservation> groupReservations = new HashSet<GroupReservation>(0);

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

	void setRoomId(final long roomId) {
		this.roomId = roomId;
	}

	public Date getRoomTimestamp() {
		return roomTimestamp;
	}

	void setRoomTimestamp(final Date roomTimestamp) {
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

	public Set<GroupReservation> getGroupReservations() {
		return Collections.unmodifiableSet(groupReservations);
	}

	void addGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		this.groupReservations.add(groupReservation);
	}

	void addGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		this.groupReservations.addAll(groupReservationSet);
	}

	void removeGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		this.groupReservations.remove(groupReservation);
	}

	void removeGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		this.groupReservations.removeAll(groupReservationSet);
	}

	public void associateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		addGroupReservation(groupReservation);
		groupReservation.addRoom(this);
	}

	public void associateGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		for (final GroupReservation groupReservation : groupReservationSet) {
			groupReservation.addRoom(this);
		}

		addGroupReservations(groupReservationSet);
	}

	public void unassociateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		removeGroupReservation(groupReservation);
		groupReservation.removeRoom(this);
	}

	public void unassociateGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		for (final GroupReservation groupReservation : groupReservationSet) {
			groupReservation.removeRoom(this);
		}

		removeGroupReservations(groupReservationSet);
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
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Room)) {
			return false;
		}

		final Room other = (Room) entity;
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).append(getRoomTimestamp(), other.getRoomTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Room)) {
			return false;
		}

		final Room other = (Room) entity;
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).append(getName(), other.getName()).append(getCapacity(), other.getCapacity()).append(getPrecedence(), other.getPrecedence()).append(isInUse(), other.isInUse()).isEquals();
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
		return new EqualsBuilder().append(getRoomId(), other.getRoomId()).append(getRoomTimestamp(), other.getRoomTimestamp()).append(getName(), other.getName()).append(getCapacity(), other.getCapacity()).append(getPrecedence(), other.getPrecedence()).append(isInUse(), other.isInUse()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRoomId()).append(getRoomTimestamp()).append(getName()).append(getCapacity()).append(getPrecedence()).append(isInUse()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getRoomId()).append(getRoomTimestamp()).append(getName()).append(getCapacity()).append(getPrecedence()).append(isInUse()).toString();
	}
}
