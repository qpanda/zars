package net.soomsam.zirmegghuette.zars.service.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GroupReservationBean extends BaseBean {
	private long groupReservationId;

	public GroupReservationBean() {
		super();
	}

	public long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(long groupReservationId) {
		this.groupReservationId = groupReservationId;
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
