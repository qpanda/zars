package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GroupReservationBean extends BaseBean {
	private long groupReservationId;

	private Date groupReservationTimestamp;

	private Date booked;

	private Date arrival;

	private Date departure;

	private long guests;

	private String comment;

	private UserBean booker;

	private UserBean beneficiary;

	private UserBean accountant;

	private List<RoomBean> rooms;

	private List<ReservationBean> reservations;

	public GroupReservationBean() {
		super();
	}

	public Date getGroupReservationTimestamp() {
		return groupReservationTimestamp;
	}

	public void setGroupReservationTimestamp(final Date groupReservationTimestamp) {
		this.groupReservationTimestamp = groupReservationTimestamp;
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(final long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public Date getBooked() {
		return booked;
	}

	public void setBooked(final Date booked) {
		this.booked = booked;
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

	public UserBean getBooker() {
		return booker;
	}

	public void setBooker(final UserBean booker) {
		this.booker = booker;
	}

	public UserBean getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(final UserBean beneficiary) {
		this.beneficiary = beneficiary;
	}

	public UserBean getAccountant() {
		return accountant;
	}

	public void setAccountant(final UserBean accountant) {
		this.accountant = accountant;
	}

	public List<RoomBean> getRooms() {
		return rooms;
	}

	public void setRooms(final List<RoomBean> rooms) {
		this.rooms = rooms;
	}

	public List<ReservationBean> getReservations() {
		return reservations;
	}

	public void setReservations(final List<ReservationBean> reservations) {
		this.reservations = reservations;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[] { "groupReservationTimestamp" });
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[] { "groupReservationTimestamp" });
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
