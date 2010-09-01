package net.soomsam.zirmegghuette.zars.service.vo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;
import org.joda.time.DateMidnight;

@Immutable
public class ReservationVo extends BaseVo {
	private final DateMidnight arrival;
	private final DateMidnight departure;
	private final String firstName;
	private final String lastName;

	public ReservationVo(final DateMidnight arrival, final DateMidnight departure, final String firstName, final String lastName) {
		this.arrival = arrival;
		this.departure = departure;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public DateMidnight getArrival() {
		return arrival;
	}

	public DateMidnight getDeparture() {
		return departure;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
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