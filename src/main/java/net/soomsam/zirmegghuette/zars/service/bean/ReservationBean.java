package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ReservationBean extends BaseBean {
	private long reservationId;

	private Date reservationTimestamp;

	private long precedence;

	private Date arrival;

	private Date departure;

	private String firstName;

	private String lastName;

	public ReservationBean() {
		super();
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(final long reservationId) {
		this.reservationId = reservationId;
	}

	public Date getReservationTimestamp() {
		return reservationTimestamp;
	}

	public void setReservationTimestamp(final Date reservationTimestamp) {
		this.reservationTimestamp = reservationTimestamp;
	}

	public long getPrecedence() {
		return precedence;
	}

	public void setPrecedence(final long precedence) {
		this.precedence = precedence;
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
