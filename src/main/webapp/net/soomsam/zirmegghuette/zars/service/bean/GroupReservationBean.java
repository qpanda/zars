package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateMidnight;

public class GroupReservationBean extends BaseBean {
	private long groupReservationId;

	private Date groupReservationTimestamp;

	private DateMidnight arrival;

	private DateMidnight departure;

	private long guests;

	private String comment;

	private UserBean beneficiary;

	private UserBean accountant;

	private RoomBean rooms;

	public GroupReservationBean() {
		super();
	}

	public Date getGroupReservationTimestamp() {
		return groupReservationTimestamp;
	}

	public void setGroupReservationTimestamp(Date groupReservationTimestamp) {
		this.groupReservationTimestamp = groupReservationTimestamp;
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public DateMidnight getArrival() {
		return arrival;
	}

	public void setArrival(DateMidnight arrival) {
		this.arrival = arrival;
	}

	public DateMidnight getDeparture() {
		return departure;
	}

	public void setDeparture(DateMidnight departure) {
		this.departure = departure;
	}

	public long getGuests() {
		return guests;
	}

	public void setGuests(long guests) {
		this.guests = guests;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UserBean getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(UserBean beneficiary) {
		this.beneficiary = beneficiary;
	}

	public UserBean getAccountant() {
		return accountant;
	}

	public void setAccountant(UserBean accountant) {
		this.accountant = accountant;
	}

	public RoomBean getRooms() {
		return rooms;
	}

	public void setRooms(RoomBean rooms) {
		this.rooms = rooms;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
