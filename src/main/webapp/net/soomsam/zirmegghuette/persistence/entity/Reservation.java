package net.soomsam.zirmegghuette.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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

	@Column(name = Reservation.COLUMNNAME_ARRIVAL, nullable = false)
	private final Date arrival;

	@Column(name = Reservation.COLUMNNAME_DEPARTURE, nullable = false)
	private final Date departure;

	@Column(name = Reservation.COLUMNNAME_GUESTFIRSTNAME, nullable = true, length = 256)
	private final String firstName;

	@Column(name = Reservation.COLUMNNAME_GUESTLASTNAME, nullable = true, length = 256)
	private final String lastName;

	@ManyToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = javax.persistence.FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, nullable = false)
	private GroupReservation groupReservation;

	public Reservation(final GroupReservation groupReservation, final Date arrival, final Date departure,
			final String firstName, final String lastName) {
		super();
		this.groupReservation = groupReservation;
		this.arrival = arrival;
		this.departure = departure;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(final long reservationId) {
		this.reservationId = reservationId;
	}

	public Date getArrival() {
		return arrival;
	}

	public Date getDeparture() {
		return departure;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public GroupReservation getGroupReservation() {
		return groupReservation;
	}

	public void setGroupReservation(final GroupReservation groupReservation) {
		this.groupReservation = groupReservation;
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
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getReservationId()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getReservationId()).append(getArrival()).append(getDeparture()).append(getFirstName()).append(getLastName()).toString();
	}
}
