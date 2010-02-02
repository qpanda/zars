package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.event.JPAValidateListener;

@Entity
@Table(name = GroupReservation.TABLENAME_GROUPRESERVATION)
@EntityListeners(value = { JPAValidateListener.class })
public class GroupReservation extends BaseEntity {
	public static final String TABLENAME_GROUPRESERVATION = "group_reservation";
	public static final String COLUMNNAME_GROUPRESERVATIONID = "group_reservation_id";
	public static final String COLUMNNAME_COMMENT = "comment";
	public static final String JOINTABLENAME_GROUPRESERVATION_ROOM = "group_reservation_room";

	@Id
	@GeneratedValue
	@Column(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, unique = true, nullable = false)
	private long groupReservationId;

	@Column(name = GroupReservation.COLUMNNAME_COMMENT, nullable = true, length = 256)
	private String comment;

	@NotNull
	@ManyToOne(cascade = { javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH }, fetch = javax.persistence.FetchType.EAGER, optional = false)
	@JoinColumn(name = User.COLUMNNAME_USERID, nullable = false)
	private User user;

	@OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = javax.persistence.FetchType.LAZY, optional = true)
	@JoinColumn(name = Invoice.COLUMNNAME_INVOICEID, unique = true, nullable = true, updatable = false)
	private Invoice invoice;

	@NotNull
	@NotEmpty
	@OneToMany(cascade = { javax.persistence.CascadeType.ALL }, fetch = javax.persistence.FetchType.EAGER, orphanRemoval = true, mappedBy = "groupReservation")
	private Set<Reservation> reservations = new HashSet<Reservation>(0);

	@NotNull
	@NotEmpty
	@ManyToMany(cascade = { javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH }, fetch = javax.persistence.FetchType.EAGER)
	@JoinTable(name = GroupReservation.JOINTABLENAME_GROUPRESERVATION_ROOM, joinColumns = @JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID), inverseJoinColumns = @JoinColumn(name = Room.COLUMNNAME_ROOMID))
	private Set<Room> rooms = new HashSet<Room>(0);

	private GroupReservation() {
		super();
	}

	public GroupReservation(final User user) {
		super();
		this.user = user;
	}

	public GroupReservation(final User user, final String comment) {
		super();
		this.user = user;
		this.comment = comment;
	}

	public GroupReservation(final User user, final Reservation reservation) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.user = user;
		this.reservations.add(reservation);
	}

	public GroupReservation(final User user, final Set<Reservation> reservations) {
		super();
		this.user = user;
		this.reservations = reservations;
	}

	public GroupReservation(final User user, final Reservation reservation, final String comment) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.user = user;
		this.comment = comment;
		this.reservations.add(reservation);
	}

	public GroupReservation(final User user, final Set<Reservation> reservations, final String comment) {
		super();
		this.user = user;
		this.reservations = reservations;
		this.comment = comment;
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(final long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public User getUser() {
		return user;
	}

	void setUser(final User user) {
		this.user = user;
	}

	public void associateUser(final User user) {
		setUser(user);
		user.addGroupReservation(this);
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(final Invoice invoice) {
		this.invoice = invoice;
	}

	public Set<Reservation> getReservations() {
		return reservations;
	}

	void setReservations(final Set<Reservation> reservations) {
		if (null == reservations) {
			throw new IllegalArgumentException("'reservations' must not be null");
		}

		this.reservations = reservations;
	}

	void addReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.reservations.add(reservation);
	}

	public void associateReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		addReservation(reservation);
		reservation.setGroupReservation(this);
	}

	public Set<Room> getRooms() {
		return rooms;
	}

	public void setRooms(final Set<Room> rooms) {
		if (null == rooms) {
			throw new IllegalArgumentException("'rooms' must not be null");
		}

		this.rooms = rooms;
	}

	public void addRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		this.rooms.add(room);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof GroupReservation)) {
			return false;
		}

		final GroupReservation other = (GroupReservation) entity;
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof GroupReservation)) {
			return false;
		}

		final GroupReservation other = (GroupReservation) obj;
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getComment(), other.getComment()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getGroupReservationId()).append(getComment()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getGroupReservationId()).append(getComment()).toString();
	}
}
