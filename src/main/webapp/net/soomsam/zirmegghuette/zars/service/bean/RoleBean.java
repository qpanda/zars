package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RoleBean extends BaseBean {
	private long roleId;

	private Date roleTimestamp;

	private String name;

	public RoleBean() {
		super();
	}

	public RoleBean(long roleId, Date roleTimestamp, String name) {
		super();
		this.roleId = roleId;
		this.roleTimestamp = roleTimestamp;
		this.name = name;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public Date getRoleTimestamp() {
		return roleTimestamp;
	}

	public void setRoleTimestamp(Date roleTimestamp) {
		this.roleTimestamp = roleTimestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
