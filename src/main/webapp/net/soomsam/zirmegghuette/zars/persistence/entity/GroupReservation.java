package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Size;
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

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = GroupReservation.COLUMNNAME_COMMENT, nullable = true, length = 256)
	private String comment;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = User.COLUMNNAME_USERID, nullable = false)
	private User user;

	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = Invoice.COLUMNNAME_INVOICEID, unique = true, nullable = true, updatable = false)
	private Invoice invoice;

	@NotNull
	@Size(min = 1)
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "groupReservation")
	private final Set<Reservation> reservations = new HashSet<Reservation>(0);

	@NotNull
	@Size(min = 1)
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinTable(name = GroupReservation.JOINTABLENAME_GROUPRESERVATION_ROOM, joinColumns = @JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID), inverseJoinColumns = @JoinColumn(name = Room.COLUMNNAME_ROOMID))
	private Set<Room> rooms = new HashSet<Room>(0);

	protected GroupReservation() {
		super();
	}

	public GroupReservation(final User user) {
		super();

		associateUser(user);
	}

	public GroupReservation(final User user, final String comment) {
		super();
		this.comment = comment;

		associateUser(user);
	}

	public GroupReservation(final User user, final Reservation reservation) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		associateUser(user);
		associateReservation(reservation);
	}

	public GroupReservation(final User user, final Set<Reservation> reservations) {
		super();

		associateUser(user);
		associateReservations(reservations);
	}

	public GroupReservation(final User user, final Reservation reservation, final String comment) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.comment = comment;

		associateUser(user);
		associateReservation(reservation);
	}

	public GroupReservation(final User user, final Set<Reservation> reservations, final String comment) {
		super();
		this.comment = comment;

		associateUser(user);
		associateReservations(reservations);
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(final long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
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
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		setUser(user);
		user.addGroupReservation(this);
	}

	public Invoice getInvoice() {
		return invoice;
	}

	void setInvoice(final Invoice invoice) {
		this.invoice = invoice;
	}

	public void associateInvoice(final Invoice invoice) {
		if (null == invoice) {
			throw new IllegalArgumentException("'invoice' must not be null");
		}

		setInvoice(invoice);
		invoice.setGroupReservation(this);
	}

	public Set<Reservation> getReservations() {
		return reservations;
	}

	void addReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.reservations.add(reservation);
	}

	void removeReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.reservations.remove(reservation);
	}

	public void associateReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		addReservation(reservation);
		reservation.setGroupReservation(this);
	}

	public void associateReservations(final Set<Reservation> reservationSet) {
		if (null == reservationSet) {
			throw new IllegalArgumentException("'reservationSet' must not be null");
		}

		for (final Reservation reservation : reservationSet) {
			associateReservation(reservation);
		}
	}

	public void unassociateReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		removeReservation(reservation);
		reservation.setGroupReservation(null);
	}

	public void unassociateReservations(final Set<Reservation> reservationSet) {
		if (null == reservationSet) {
			throw new IllegalArgumentException("'reservationSet' must not be null");
		}

		for (final Reservation reservation : reservationSet) {
			unassociateReservation(reservation);
		}
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
