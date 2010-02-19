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
	public static final String COLUMNNAME_BENEFICIARY_USERID = "beneficiary_user_id";
	public static final String COLUMNNAME_ACCOUNTANT_USERID = "accountant_user_id";
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
	@JoinColumn(name = GroupReservation.COLUMNNAME_BENEFICIARY_USERID, nullable = false)
	private User beneficiary;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_ACCOUNTANT_USERID, nullable = false)
	private User accountant;

	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, optional = true, mappedBy = "groupReservation")
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

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "groupReservations")
	private final Set<Report> reports = new HashSet<Report>(0);

	protected GroupReservation() {
		super();
	}

	public GroupReservation(final User beneficiary, final User accountant) {
		super();

		associateBeneficiary(beneficiary);
		associateAccountant(accountant);
	}

	public GroupReservation(final User beneficiary, final User accountant, final String comment) {
		super();
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

	public User getBeneficiary() {
		return beneficiary;
	}

	void setBeneficiary(final User beneficiary) {
		this.beneficiary = beneficiary;
	}

	public void associateBeneficiary(final User beneficiary) {
		if (null == beneficiary) {
			throw new IllegalArgumentException("'beneficiary' must not be null");
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

	public void associateAccountant(final User accountant) {
		if (null == accountant) {
			throw new IllegalArgumentException("'accountant' must not be null");
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

	public Set<Report> getReports() {
		return reports;
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

	public void associateReport(final Report report) {
		if (null == report) {
			throw new IllegalArgumentException("'report' must not be null");
		}

		addReport(report);
		report.addGroupReservation(this);
	}

	public void associateReports(final Set<Report> reportSet) {
		if (null == reportSet) {
			throw new IllegalArgumentException("'reportSet' must not be null");
		}

		for (final Report report : reportSet) {
			associateReport(report);
		}
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
