package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	@ManyToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = javax.persistence.FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, nullable = false)
	private GroupReservation groupReservation;

	private Reservation() {
		super();
	}

	public Reservation(final Date arrival, final Date departure, final String firstName, final String lastName) {
		super();
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
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		this.groupReservation = groupReservation;
	}

	public void associateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		setGroupReservation(groupReservation);
		groupReservation.addReservation(this);
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
