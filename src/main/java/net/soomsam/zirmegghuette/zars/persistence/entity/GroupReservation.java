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
import javax.persistence.OrderBy;
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
@NamedQueries({ @NamedQuery(name = GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME, query = GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYSTRING), @NamedQuery(name = GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME, query = GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYSTRING), @NamedQuery(name = GroupReservation.COUNTGROUPRESERVATION_BENEFICIARYID_QUERYNAME, query = GroupReservation.COUNTGROUPRESERVATION_BENEFICIARYID_QUERYSTRING), @NamedQuery(name = GroupReservation.FINDGROUPRESERVATION_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATION_QUERYSTRING), @NamedQuery(name = GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYSTRING), @NamedQuery(name = GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYSTRING), @NamedQuery(name = GroupReservation.FINDGROUPRESERVATION_BENEFICIARYID_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATION_BENEFICIARYID_QUERYSTRING), @NamedQuery(name = GroupReservation.FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYNAME, query = GroupReservation.FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYSTRING) })
public class GroupReservation extends BaseEntity {
	public static final String TABLENAME_GROUPRESERVATION = "zars_group_reservation";
	public static final String COLUMNNAME_GROUPRESERVATIONID = "group_reservation_id";
	public static final String COLUMNNAME_GROUPRESERVATIONTIMESTAMP = "group_reservation_timestamp";
	public static final String COLUMNNAME_BOOKED = "booked";
	public static final String COLUMNNAME_ARRIVAL = "arrival";
	public static final String COLUMNNAME_DEPARTURE = "departure";
	public static final String COLUMNNAME_COMMENT = "comment";
	public static final String COLUMNNAME_GUESTS = "guests";
	public static final String COLUMNNAME_BOOKER_USERID = "booker_user_id";
	public static final String COLUMNNAME_BENEFICIARY_USERID = "beneficiary_user_id";
	public static final String COLUMNNAME_ACCOUNTANT_USERID = "accountant_user_id";
	public static final String JOINTABLENAME_GROUPRESERVATION_ROOM = "zars_group_reservation_zars_room";

	public static final int COLUMNLENGTH_COMMENT = 512;

	public static final String COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME = "GroupReservation.countGroupReservationByClosedStartEndIntervalQuery";
	public static final String COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYSTRING = "select count(groupReservation) from GroupReservation groupReservation where (:startDate <= groupReservation.arrival and groupReservation.arrival <= :endDate) or (:startDate <= groupReservation.departure and groupReservation.departure <= :endDate) or (groupReservation.arrival <= :startDate and :endDate <= groupReservation.departure)";

	public static final String COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME = "GroupReservation.countGroupReservationByClosedStartEndIntervalAndBeneficiaryIdQuery";
	public static final String COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYSTRING = "select count(groupReservation) from GroupReservation groupReservation where groupReservation.beneficiary.userId = :beneficiaryId and ((:startDate <= groupReservation.arrival and groupReservation.arrival <= :endDate) or (:startDate <= groupReservation.departure and groupReservation.departure <= :endDate) or (groupReservation.arrival <= :startDate and :endDate <= groupReservation.departure))";

	public static final String COUNTGROUPRESERVATION_BENEFICIARYID_QUERYNAME = "GroupReservation.countGroupReservationByBeneficiaryIdQuery";
	public static final String COUNTGROUPRESERVATION_BENEFICIARYID_QUERYSTRING = "select count(groupReservation) from GroupReservation groupReservation where groupReservation.beneficiary.userId = :beneficiaryId";

	public static final String FINDGROUPRESERVATION_QUERYNAME = "GroupReservation.findGroupReservationQuery";
	public static final String FINDGROUPRESERVATION_QUERYSTRING = "from GroupReservation as groupReservation order by groupReservation.arrival asc";

	public static final String FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME = "GroupReservation.findGroupReservationByClosedStartEndIntervalQuery";
	public static final String FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYSTRING = "from GroupReservation groupReservation where (:startDate <= groupReservation.arrival and groupReservation.arrival <= :endDate) or (:startDate <= groupReservation.departure and groupReservation.departure <= :endDate) or (groupReservation.arrival <= :startDate and :endDate <= groupReservation.departure) order by groupReservation.arrival asc";

	public static final String FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME = "GroupReservation.findGroupReservationByClosedStartEndIntervalAndBeneficiaryIdQuery";
	public static final String FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYSTRING = "from GroupReservation groupReservation where groupReservation.beneficiary.userId = :beneficiaryId and ((:startDate <= groupReservation.arrival and groupReservation.arrival <= :endDate) or (:startDate <= groupReservation.departure and groupReservation.departure <= :endDate) or (groupReservation.arrival <= :startDate and :endDate <= groupReservation.departure)) order by groupReservation.arrival asc";

	public static final String FINDGROUPRESERVATION_BENEFICIARYID_QUERYNAME = "GroupReservation.findGroupReservationByBeneficiaryIdQuery";
	public static final String FINDGROUPRESERVATION_BENEFICIARYID_QUERYSTRING = "from GroupReservation groupReservation where groupReservation.beneficiary.userId = :beneficiaryId order by groupReservation.arrival asc";

	public static final String FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYNAME = "GroupReservation.findGroupReservationByOpenStartEndIntervalQuery";
	public static final String FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYSTRING = "from GroupReservation where (:startDate <= arrival and arrival < :endDate) or (:startDate < departure and departure <= :endDate) or (arrival <= :startDate and :endDate < departure) order by arrival asc";

	@Id
	@GeneratedValue
	@Column(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, unique = true, nullable = false)
	private long groupReservationId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONTIMESTAMP, nullable = false)
	private Date groupReservationTimestamp;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = GroupReservation.COLUMNNAME_BOOKED, nullable = false)
	private Date booked;

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

	@Column(name = GroupReservation.COLUMNNAME_COMMENT, nullable = true, length = GroupReservation.COLUMNLENGTH_COMMENT)
	private String comment;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_BOOKER_USERID, nullable = false)
	private User booker;

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

	@OrderBy("precedence")
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

	public GroupReservation(final User booker, final User beneficiary, final User accountant, final DateTime booked, final DateMidnight arrival, final DateMidnight departure, final long guests) {
		super();

		if ((null == booked) || (null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'booked', 'arrival', and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.booked = DateUtils.convertDateTime(booked);
		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.guests = guests;

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
	}

	public GroupReservation(final User booker, final User beneficiary, final User accountant, final DateTime booked, final DateMidnight arrival, final DateMidnight departure, final long guests, final String comment) {
		super();

		if ((null == booked) || (null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'booked', 'arrival', and 'departure' must not be null");
		}

		if (new DateTime(arrival).isAfter(new DateTime(departure))) {
			throw new IllegalArgumentException("'arrival' has to be before 'departure'");
		}

		this.booked = DateUtils.convertDateTime(booked);
		this.arrival = DateUtils.convertDateMidnight(arrival);
		this.departure = DateUtils.convertDateMidnight(departure);
		this.guests = guests;
		this.comment = comment;

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
	}

	public GroupReservation(final DateTime booked, final User booker, final User beneficiary, final User accountant, final Reservation reservation) {
		super();

		if ((null == booked) || (null == reservation)) {
			throw new IllegalArgumentException("'booked' and 'reservation' must not be null");
		}

		this.booked = DateUtils.convertDateTime(booked);

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservation(reservation);
	}

	public GroupReservation(final DateTime booked, final User booker, final User beneficiary, final User accountant, final Set<Reservation> reservations) {
		super();

		if (null == booked) {
			throw new IllegalArgumentException("'booked' must not be null");
		}

		this.booked = DateUtils.convertDateTime(booked);

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservations(reservations);
	}

	public GroupReservation(final DateTime booked, final User booker, final User beneficiary, final User accountant, final Reservation reservation, final String comment) {
		super();

		if ((null == booked) || (null == reservation)) {
			throw new IllegalArgumentException("'booked' and 'reservation' must not be null");
		}

		this.comment = comment;
		this.booked = DateUtils.convertDateTime(booked);

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservation(reservation);
	}

	public GroupReservation(final DateTime booked, final User booker, final User beneficiary, final User accountant, final Set<Reservation> reservations, final String comment) {
		super();

		if (null == booked) {
			throw new IllegalArgumentException("'booked' must not be null");
		}

		this.comment = comment;
		this.booked = DateUtils.convertDateTime(booked);

		associateBooker(booker);
		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
		associateReservations(reservations);
	}

	@PrePersist
	protected void prePersist() {
		autoSetGuests();
		autoSetArrivalDeparture();
	}

	public void autoSetGuests() {
		if (hasReservations()) {
			this.guests = getReservations().size();
		}
	}

	public void autoSetArrivalDeparture() {
		if (hasReservations()) {
			setArrival(getEarliestArrivalReservation().getArrival());
			setDeparture(getLatestDepartureReservation().getDeparture());
		}
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	void setGroupReservationId(final long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public Date getGroupReservationTimestamp() {
		return groupReservationTimestamp;
	}

	void setGroupReservationTimestamp(final Date groupReservationTimestamp) {
		this.groupReservationTimestamp = groupReservationTimestamp;
	}

	public DateTime getBooked() {
		return DateUtils.convertDateTime(booked);
	}

	public void setBooked(final DateTime booked) {
		this.booked = DateUtils.convertDateTime(booked);
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

	public User getBooker() {
		return booker;
	}

	void setBooker(final User booker) {
		this.booker = booker;
	}

	public boolean hasBooker() {
		return null != getBooker();
	}

	public void associateBooker(final User booker) {
		if (null == booker) {
			throw new IllegalArgumentException("'booker' must not be null");
		}

		if (hasBooker()) {
			getBooker().removeBookerGroupReservation(this);
		}

		setBooker(booker);
		booker.addBookerGroupReservation(this);
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

	void addReservations(final Set<Reservation> reservationSet) {
		if (null == reservationSet) {
			throw new IllegalArgumentException("'reservationSet' must not be null");
		}

		this.reservations.addAll(reservationSet);
	}

	void removeReservation(final Reservation reservation) {
		if (null == reservation) {
			throw new IllegalArgumentException("'reservation' must not be null");
		}

		this.reservations.remove(reservation);
	}

	void removeReservations(final Set<Reservation> reservationSet) {
		if (null == reservationSet) {
			throw new IllegalArgumentException("'reservationSet' must not be null");
		}

		this.reservations.removeAll(reservationSet);
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
			reservation.setGroupReservation(this);
		}

		addReservations(reservationSet);
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
			reservation.setGroupReservation(null);
		}

		removeReservations(reservationSet);
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

	void addRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		this.rooms.addAll(roomSet);
	}

	void removeRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		this.rooms.remove(room);
	}

	void removeRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		this.rooms.removeAll(roomSet);
	}

	public void associateRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		addRoom(room);
		room.addGroupReservation(this);
	}

	public void associateRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		for (final Room room : roomSet) {
			room.addGroupReservation(this);
		}

		addRooms(roomSet);
	}

	public void unassociateRoom(final Room room) {
		if (null == room) {
			throw new IllegalArgumentException("'room' must not be null");
		}

		removeRoom(room);
		room.removeGroupReservation(this);
	}

	public void unassociateRooms(final Set<Room> roomSet) {
		if (null == roomSet) {
			throw new IllegalArgumentException("'roomSet' must not be null");
		}

		for (final Room room : roomSet) {
			room.removeGroupReservation(this);
		}

		removeRooms(roomSet);
	}

	public void updateRooms(final Set<Room> newRoomSet) {
		if (null == newRoomSet) {
			throw new IllegalArgumentException("'newRoomSet' must not be null");
		}

		final Set<Room> oldRoomSet = getRooms();

		final Set<Room> addedRoomSet = new HashSet<Room>(newRoomSet);
		addedRoomSet.removeAll(oldRoomSet);

		final Set<Room> removedRoomSet = new HashSet<Room>(oldRoomSet);
		removedRoomSet.removeAll(newRoomSet);

		unassociateRooms(removedRoomSet);
		associateRooms(addedRoomSet);
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

	void removeReports(final Set<Report> reportSet) {
		if (null == reportSet) {
			throw new IllegalArgumentException("'reportSet' must not be null");
		}

		this.reports.removeAll(reportSet);
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
			report.removeGroupReservation(this);
		}

		removeReports(reportSet);
	}

	public void markInvoiceStale() {
		final Invoice invoice = getInvoice();
		if (null != invoice) {
			invoice.setStale(true);
		}
	}

	public void markReportsStale() {
		final Set<Report> reportSet = getReports();
		for (final Report report : reportSet) {
			report.setStale(true);
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
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getGroupReservationTimestamp(), other.getGroupReservationTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof GroupReservation)) {
			return false;
		}

		final GroupReservation other = (GroupReservation) entity;
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getBooked(), other.getBooked()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getGuests(), other.getGuests()).append(getComment(), other.getComment()).isEquals();
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
		return new EqualsBuilder().append(getGroupReservationId(), other.getGroupReservationId()).append(getGroupReservationTimestamp(), other.getGroupReservationTimestamp()).append(getBooked(), other.getBooked()).append(getArrival(), other.getArrival()).append(getDeparture(), other.getDeparture()).append(getGuests(), other.getGuests()).append(getComment(), other.getComment()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getGroupReservationId()).append(getGroupReservationTimestamp()).append(getBooked()).append(getArrival()).append(getDeparture()).append(getGuests()).append(getComment()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getGroupReservationId()).append(getGroupReservationTimestamp()).append(getBooked()).append(getArrival()).append(getDeparture()).append(getGuests()).append(getComment()).toString();
	}
}
