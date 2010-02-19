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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.event.JPAValidateListener;

@Entity
@Table(name = Role.TABLENAME_ROLE)
@EntityListeners(value = { JPAValidateListener.class })
public class Role extends BaseEntity {
	public static final String TABLENAME_ROLE = "role";
	public static final String COLUMNNAME_ROLEID = "role_id";
	public static final String COLUMNNAME_NAME = "name";

	@Id
	@GeneratedValue
	@Column(name = Role.COLUMNNAME_ROLEID, unique = true, nullable = false)
	private long roleId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@NotEmpty
	@Column(name = Role.COLUMNNAME_NAME, unique = true, nullable = false, length = 256)
	private String name;

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "roles")
	private final Set<User> users = new HashSet<User>(0);

	protected Role() {
		super();
	}

	public Role(final String name) {
		super();
		this.name = name;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(final long roleId) {
		this.roleId = roleId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	void addUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		this.users.add(user);
	}

	void removeUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		this.users.remove(user);
	}

	public void associateUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		addUser(user);
		user.addRole(this);
	}

	public void associateUsers(final Set<User> userSet) {
		if (null == userSet) {
			throw new IllegalArgumentException("'userSet' must not be null");
		}

		for (final User user : userSet) {
			associateUser(user);
		}
	}

	public void unassociateUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		removeUser(user);
		user.removeRole(this);
	}

	public void unassociateUsers(final Set<User> userSet) {
		if (null == userSet) {
			throw new IllegalArgumentException("'userSet' must not be null");
		}

		for (final User user : userSet) {
			unassociateUser(user);
		}
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Role)) {
			return false;
		}

		final Role other = (Role) entity;
		return new EqualsBuilder().append(getRoleId(), other.getRoleId()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Role)) {
			return false;
		}

		final Role other = (Role) obj;
		return new EqualsBuilder().append(getRoleId(), other.getRoleId()).append(getTimestamp(), other.getTimestamp()).append(getName(), other.getName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRoleId()).append(getTimestamp()).append(getName()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getRoleId()).append(getTimestamp()).append(getName()).toString();
	}
}
