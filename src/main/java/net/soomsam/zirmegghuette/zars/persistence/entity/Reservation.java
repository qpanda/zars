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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.soomsam.zirmegghuette.zars.persistence.utils.DateUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

@Entity
@Table(name = Reservation.TABLENAME_RESERVATION)
public class Reservation extends BaseEntity {
	public static final String TABLENAME_RESERVATION = "zars_reservation";
	public static final String COLUMNNAME_RESERVATIONID = "reservation_id";
	public static final String COLUMNNAME_RESERVATIONTIMESTAMP = "reservation_timestamp";
	public static final String COLUMNNAME_PRECEDENCE = "precedence";
	public static final String COLUMNNAME_ARRIVAL = "arrival";
	public static final String COLUMNNAME_DEPARTURE = "departure";
	public static final String COLUMNNAME_GUESTFIRSTNAME = "guest_first_name";
	public static final String COLUMNNAME_GUESTLASTNAME = "guest_last_name";

	public static final int COLUMNLENGTH_GUESTFIRSTNAME = 256;
	public static final int COLUMNLENGTH_GUESTLASTNAME = 256;
	
	@Id
	@GeneratedValue
	@Column(name = Reservation.COLUMNNAME_RESERVATIONID, unique = true, nullable = false)
	private long reservationId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Reservation.COLUMNNAME_RESERVATIONTIMESTAMP, nullable = false)
	private Date reservationTimestamp;

	@Min(value = 1)
	@Column(name = Reservation.COLUMNNAME_PRECEDENCE, nullable = false)
	private long precedence;

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
	@Column(name = Reservation.COLUMNNAME_GUESTFIRSTNAME, nullable = false, length = Reservation.COLUMNLENGTH_GUESTFIRSTNAME)
	private String firstName;

	@NotNull
	@NotEmpty
	@Column(name = Reservation.COLUMNNAME_GUESTLASTNAME, nullable = false, length = Reservation.COLUMNLENGTH_GUESTLASTNAME)
	private String lastName;

	@NotNull
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, nullable = false)
	private GroupReservation groupReservation;

	protected Reservation() {
		super();
	}

	public Reservation(final long precedence, final DateMidnight arrival, final DateMidnight departure, final String firstName, final String lastName) {
		super();

		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.precedence = precedence;
		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Reservation(final long precedence, final DateMidnight arrival, final DateMidnight departure, final String firstName, final String lastName, final GroupReservation groupReservation) {
		super();

		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.precedence = precedence;
		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.firstName = firstName;
		this.lastName = lastName;

		associateGroupReservation(groupReservation);
	}

	public long getReservationId() {
		return reservationId;
	}

	void setReservationId(final long reservationId) {
		this.reservationId = reservationId;
	}

	public Date getReservationTimestamp() {
		return reservationTimestamp;
	}

	void setReservationTimestamp(final Date reservationTimestamp) {
		this.reservationTimestamp = reservationTimestamp;
	}

	public long getPrecedence() {
		return precedence;
	}

	public void setPrecedence(long precedence) {
		this.precedence = precedence;
	}

	public DateMidnight getArrival() {
		return DateUtils.convertDate(arrival);
	}

	public void setArrival(final DateMidnight arrival) {
		this.arrival = DateUtils.convertDateMidnight(arrival);
	}

	public DateMidnight getDeparture() {
		return DateUtils.convertDate(departure);
	}

	public void setDeparture(final DateMidnight departure) {
		this.departure = DateUtils.convertDateMidnight(departure);
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
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).append(getReservationTimestamp(), other.getReservationTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Reservation)) {
			return false;
		}

		final Reservation other = (Reservation) entity;
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).isEquals();
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
		return new EqualsBuilder().append(getReservationId(), other.getReservationId()).append(getReservationTimestamp(), other.getReservationTimestamp()).append(getPrecedence(), other.getPrecedence()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getReservationId()).append(getReservationTimestamp()).append(getPrecedence()).append(getArrival()).append(getDeparture()).append(getFirstName()).append(getLastName()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getReservationId()).append(getReservationTimestamp()).append(getPrecedence()).append(getArrival()).append(getDeparture()).append(getFirstName()).append(getLastName()).toString();
	}
}
