package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.soomsam.zirmegghuette.zars.persistence.utils.DateUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

@Entity
@Table(name = GroupReservation.TABLENAME_GROUPRESERVATION)
@NamedQueries( { @NamedQuery(name = GroupReservation.FINDGROUPRESERVATION_STARTDATE_ENDDATE_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATION_STARTDATE_ENDDATE_QUERYSTRING) })
public class GroupReservation extends BaseEntity {
	public static final String TABLENAME_GROUPRESERVATION = "group_reservation";
	public static final String COLUMNNAME_GROUPRESERVATIONID = "group_reservation_id";
	public static final String COLUMNNAME_ARRIVAL = "arrival";
	public static final String COLUMNNAME_DEPARTURE = "departure";
	public static final String COLUMNNAME_COMMENT = "comment";
	public static final String COLUMNNAME_GUESTS = "guests";
	public static final String COLUMNNAME_BENEFICIARY_USERID = "beneficiary_user_id";
	public static final String COLUMNNAME_ACCOUNTANT_USERID = "accountant_user_id";
	public static final String JOINTABLENAME_GROUPRESERVATION_ROOM = "group_reservation_room";

	public static final String FINDGROUPRESERVATION_STARTDATE_ENDDATE_QUERYNAME = "GroupReservation.findGroupReservationByStartDateEndDateQuery";
	public static final String FINDGROUPRESERVATION_STARTDATE_ENDDATE_QUERYSTRING = "from GroupReservation where (:startDate <= arrival and arrival <= :endDate) or (:startDate <= departure and departure <= :endDate) or (arrival <= :startDate and :endDate <= departure)";

	@Id
	@GeneratedValue
	@Column(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, unique = true, nullable = false)
	private long groupReservationId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = GroupReservation.COLUMNNAME_ARRIVAL, nullable = false)
	private Date arrival;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = GroupReservation.COLUMNNAME_DEPARTURE, nullable = false)
	private Date departure;

	@Column(name = GroupReservation.COLUMNNAME_GUESTS, nullable = false)
	private long guests;

	@Column(name = GroupReservation.COLUMNNAME_COMMENT, nullable = true, length = 1024)
	private String comment;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_BENEFICIARY_USERID, nullable = false)
	private User beneficiary;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_ACCOUNTANT_USERID, nullable = false)
	private User accountant;

	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, optional = true, mappedBy = "groupReservation")
	private Invoice invoice;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "groupReservation")
	private final Set<Reservation> reservations = new HashSet<Reservation>(0);

	@NotNull
	@Size(min = 1)
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinTable(name = GroupReservation.JOINTABLENAME_GROUPRESERVATION_ROOM, joinColumns = @JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID), inverseJoinColumns = @JoinColumn(name = Room.COLUMNNAME_ROOMID))
	private final Set<Room> rooms = new HashSet<Room>(0);

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "groupReservations")
	private final Set<Report> reports = new HashSet<Report>(0);

	protected GroupReservation() {
		super();
	}

	public GroupReservation(final User beneficiary, final User accountant, final DateMidnight arrival, final DateMidnight departure, final long guests) {
		super();

		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.guests = guests;

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
	}

	public GroupReservation(final User beneficiary, final User accountant, final DateMidnight arrival, final DateMidnight departure, final long guests, final String comment) {
		super();

		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.guests = guests;
		this.comment = comment;

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
	}

	public GroupReservation(final User beneficiary, final User accountant, final Reservation reservation) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservation(reservation);
	}

	public GroupReservation(final User beneficiary, final User accountant, final Set<Reservation> reservations) {
		super();

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservations(reservations);
	}

	public GroupReservation(final User beneficiary, final User accountant, final Reservation reservation, final String comment) {
		super();

		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.comment = comment;

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservation(reservation);
	}

	public GroupReservation(final User beneficiary, final User accountant, final Set<Reservation> reservations, final String comment) {
		super();
		this.comment = comment;

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservations(reservations);
	}

	@PrePersist
	protected void prePersist() {
		autoSetGuests();
		autoSetArrivalDeparture();
	}

	protected void autoSetGuests() {
		if (hasReservations()) {
			this.guests = getReservations().size();
		}
	}

	protected void autoSetArrivalDeparture() {
		if (hasReservations()) {
			setArrival(getEarliestArrivalReservation().getArrival());
			setDeparture(getLatestDepartureReservation().getDeparture());
		}
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

	public long getGuests() {
		return guests;
	}

	public void setGuests(final long guests) {
		this.guests = guests;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public User getBeneficiary() {
		return beneficiary;
	}

	void setBeneficiary(final User beneficiary) {
		this.beneficiary = beneficiary;
	}

	public boolean hasBeneficiary() {
		return null != getBeneficiary();
	}

	public void associateBeneficiary(final User beneficiary) {
		if (null == beneficiary) {
			throw new IllegalArgumentException("'beneficiary' must not be null");
		}

		if (hasBeneficiary()) {
			getBeneficiary().removeBeneficiaryGroupReservation(this);
		}

		setBeneficiary(beneficiary);
		beneficiary.addBeneficiaryGroupReservation(this);
	}

	public User getAccountant() {
		return accountant;
	}

	void setAccountant(final User accountant) {
		this.accountant = accountant;
	}

	public boolean hasAccountant() {
		return null != getAccountant();
	}

	public void associateAccountant(final User accountant) {
		if (null == accountant) {
			throw new IllegalArgumentException("'accountant' must not be null");
		}

		if (hasAccountant()) {
			getAccountant().removeAccountantGroupReservation(this);
		}

		setAccountant(accountant);
		accountant.addAccountantGroupReservation(this);
	}

	public Invoice getInvoice() {
		return invoice;
	}

	void setInvoice(final Invoice invoice) {
		this.invoice = invoice;
	}

	public Set<Reservation> getReservations() {
		return Collections.unmodifiableSet(reservations);
	}

	public boolean hasReservations() {
		return !getReservations().isEmpty();
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

	public Reservation getEarliestArrivalReservation() {
		if (!hasReservations()) {
			return null;
		}

		final Iterator<Reservation> reservationIterator = getReservations().iterator();
		Reservation firstArrivalReservation = reservationIterator.next();
		while (reservationIterator.hasNext()) {
			final Reservation currentReservation = reservationIterator.next();
			if (currentReservation.getArrival().isBefore(firstArrivalReservation.getArrival())) {
				firstArrivalReservation = currentReservation;
			}
		}
		return firstArrivalReservation;
	}

	public Reservation getLatestDepartureReservation() {
		if (!hasReservations()) {
			return null;
		}

		final Iterator<Reservation> reservationIterator = getReservations().iterator();
		Reservation latestDepartureReservation = reservationIterator.next();
		while (reservationIterator.hasNext()) {
			final Reservation currentReservation = reservationIterator.next();
			if (currentReservation.getDeparture().isAfter(latestDepartureReservation.getDeparture())) {
				latestDepartureReservation = currentReservation;
			}
		}
		return latestDepartureReservation;
	}

	public Set<Room> getRooms() {
		return Collections.unmodifiableSet(rooms);
	}

	void addRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		this.rooms.add(room);
	}

	void removeRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		this.rooms.remove(room);
	}

	public void associateRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		addRoom(room);
		room.addGroupReservations(this);
	}

	public void associateRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		for (final Room room : roomSet) {
			associateRoom(room);
		}
	}

	public void unassociateRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		removeRoom(room);
		room.removeGroupReservations(this);
	}

	public void unassociateRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		for (final Room room : roomSet) {
			unassociateRoom(room);
		}
	}

	public Set<Report> getReports() {
		return Collections.unmodifiableSet(reports);
	}

	void addReport(final Report report) {
		if (null == report) {
			throw new IllegalArgumentException("'report' must not be null");
		}

		this.reports.add(report);
	}

	void removeReport(final Report report) {
		if (null == report) {
			throw new IllegalArgumentException("'report' must not be null");
		}

		this.reports.remove(report);
	}

	public void unassociateReport(final Report report) {
		if (null == report) {
			throw new IllegalArgumentException("'report' must not be null");
		}

		removeReport(report);
		report.removeGroupReservation(this);
	}

	public void unassociateReports(final Set<Report> reportSet) {
		if (null == reportSet) {
			throw new IllegalArgumentException("'reportSet' must not be null");
		}

		for (final Report report : reportSet) {
			unassociateReport(report);
		}
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
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof GroupReservation)) {
			return false;
		}

		final GroupReservation other = (GroupReservation) entity;
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getTimestamp(), other.getTimestamp()).isEquals();
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
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getTimestamp(), other.getTimestamp()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getGuests(), other.getGuests()).append(getComment(), other.getComment()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getGroupReservationId()).append(getTimestamp()).append(getArrival()).append(getDeparture()).append(getGuests()).append(getComment()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getGroupReservationId()).append(getTimestamp()).append(getArrival()).append(getDeparture()).append(getGuests()).append(getComment()).toString();
	}
}
