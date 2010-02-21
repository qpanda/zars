package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = Reservation.TABLENAME_RESERVATION)
public class Reservation extends BaseEntity {
	public static final String TABLENAME_RESERVATION = "reservation";
	public static final String COLUMNNAME_RESERVATIONID = "reservation_id";
	public static final String COLUMNNAME_ARRIVAL = "arrival";
	public static final String COLUMNNAME_DEPARTURE = "departure";
	public static final String COLUMNNAME_GUESTFIRSTNAME = "guest_first_name";
	public static final String COLUMNNAME_GUESTLASTNAME = "guest_last_name";

	@Id
	@GeneratedValue
	@Column(name = Reservation.COLUMNNAME_RESERVATIONID, unique = true, nullable = false)
	private long reservationId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = Reservation.COLUMNNAME_ARRIVAL, nullable = false)
	private Date arrival;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = Reservation.COLUMNNAME_DEPARTURE, nullable = false)
	private Date departure;

	@NotNull
	@NotEmpty
	@Column(name = Reservation.COLUMNNAME_GUESTFIRSTNAME, nullable = true, length = 256)
	private String firstName;

	@NotNull
	@NotEmpty
	@Column(name = Reservation.COLUMNNAME_GUESTLASTNAME, nullable = true, length = 256)
	private String lastName;

	@NotNull
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, nullable = false)
	private GroupReservation groupReservation;

	protected Reservation() {
		super();
	}

	public Reservation(final Date arrival, final Date departure, final String firstName, final String lastName) {
		super();
		this.arrival = arrival;
		this.departure = departure;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Reservation(final Date arrival, final Date departure, final String firstName, final String lastName, GroupReservation groupReservation) {
		super();
		this.arrival = arrival;
		this.departure = departure;
		this.firstName = firstName;
		this.lastName = lastName;

		associateGroupReservation(groupReservation);
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(final long reservationId) {
		this.reservationId = reservationId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(final Date arrival) {
		this.arrival = arrival;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(final Date departure) {
		this.departure = departure;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public GroupReservation getGroupReservation() {
		return groupReservation;
	}

	void setGroupReservation(final GroupReservation groupReservation) {
		this.groupReservation = groupReservation;
	}

	protected void associateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		setGroupReservation(groupReservation);
		groupReservation.addReservation(this);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Reservation)) {
			return false;
		}

		final Reservation other = (Reservation) entity;
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Reservation)) {
			return false;
		}

		final Reservation other = (Reservation) entity;
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).append(getTimestamp(), other.getTimestamp()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Reservation)) {
			return false;
		}

		final Reservation other = (Reservation) obj;
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).append(getTimestamp(), other.getTimestamp()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getReservationId()).append(getTimestamp()).append(getArrival()).append(getDeparture()).append(getFirstName()).append(getLastName()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getReservationId()).append(getTimestamp()).append(getArrival()).append(getDeparture()).append(getFirstName()).append(getLastName()).toString();
	}
}
