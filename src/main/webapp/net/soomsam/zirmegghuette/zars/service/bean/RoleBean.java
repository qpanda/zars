package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RoleBean extends BaseBean {
	private final long roleId;

	private final Date timestamp;

	private final String name;

	public RoleBean(final long roleId, final Date timestamp, final String name) {
		super();
		this.roleId = roleId;
		this.timestamp = timestamp;
		this.name = name;
	}

	public long getRoleId() {
		return roleId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getName() {
		return name;
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
