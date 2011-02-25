package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class UserBean extends BaseBean {
	private long userId;

	private Date userTimestamp;

	private String username;

	private String password;

	private String emailAddress;

	private String firstName;

	private String lastName;

	private boolean enabled;

	private List<RoleBean> roles = new ArrayList<RoleBean>(0);

	private UserBean() {
		super();
	}

	private UserBean(final long userId, final Date userTimestamp, final String username, final String password, final String emailAddress, final String firstName, final String lastName, final boolean enabled) {
		super();
		this.userId = userId;
		this.userTimestamp = userTimestamp;
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public Date getUserTimestamp() {
		return userTimestamp;
	}

	public void setUserTimestamp(final Date userTimestamp) {
		this.userTimestamp = userTimestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public List<RoleBean> getRoles() {
		return roles;
	}

	public List<Long> getRoleIds() {
		final List<Long> roleIdList = new ArrayList<Long>();
		for (final RoleBean roleBean : roles) {
			roleIdList.add(roleBean.getRoleId());
		}
		return roleIdList;
	}

	public void setRoles(final List<RoleBean> roles) {
		this.roles = roles;
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
