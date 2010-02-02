package net.soomsam.zirmegghuette.zars.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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

	@NotNull
	@NotEmpty
	@Column(name = Role.COLUMNNAME_NAME, unique = true, nullable = false, length = 256)
	private String name;

	private Role() {
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

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
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
		return new EqualsBuilder().append(getRoleId(), other.getRoleId()).append(getName(), other.getName()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getRoleId()).append(getName()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getRoleId()).append(getName()).toString();
	}
}
