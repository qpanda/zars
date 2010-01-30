package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = User.TABLENAME_USER)
public class User extends BaseEntity {
	public static final String TABLENAME_USER = "user";
	public static final String COLUMNNAME_USERID = "user_id";
	public static final String COLUMNNAME_USERNAME = "user_name";
	public static final String COLUMNNAME_FIRSTNAME = "first_name";
	public static final String COLUMNNAME_LASTNAME = "last_name";
	public static final String COLUMNNAME_PASSWORD = "password";
	public static final String COLUMNNAME_ENABLED = "active";
	public static final String JOINTABLENAME_USER_ROLE = "user_role";

	@Id
	@GeneratedValue
	@Column(name = User.COLUMNNAME_USERID, unique = true, nullable = false)
	private long userId;

	@Column(name = User.COLUMNNAME_USERNAME, unique = true, nullable = false, length = 256)
	private String userName;

	@Column(name = User.COLUMNNAME_PASSWORD, nullable = false, length = 256)
	private String password;

	@Column(name = User.COLUMNNAME_FIRSTNAME, nullable = true, length = 256)
	private String firstName;

	@Column(name = User.COLUMNNAME_LASTNAME, nullable = true, length = 256)
	private String lastName;

	@Column(name = User.COLUMNNAME_ENABLED, nullable = false)
	private boolean enabled;

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinTable(name = User.JOINTABLENAME_USER_ROLE, joinColumns = @JoinColumn(name = User.COLUMNNAME_USERID), inverseJoinColumns = @JoinColumn(name = Role.COLUMNNAME_ROLEID))
	private Set<Role> roles = new HashSet<Role>(0);

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "user")
	private Set<GroupReservation> groupReservations = new HashSet<GroupReservation>(0);

	public User(final String userName, final String password, final boolean enabled, final Set<Role> roles) {
		super();
		this.userName = userName;
		this.password = password;
		this.enabled = enabled;
		this.roles = roles;
	}

	public User(final String userName, final String password, final String firstName, final String lastName,
			final boolean enabled, final Set<Role> roles) {
		super();
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.roles = roles;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(final Set<Role> roles) {
		this.roles = roles;
	}

	public Set<GroupReservation> getGroupReservations() {
		return groupReservations;
	}

	public void setGroupReservations(final Set<GroupReservation> groupReservations) {
		this.groupReservations = groupReservations;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof User)) {
			return false;
		}

		final User other = (User) obj;
		return new EqualsBuilder().append(getUserId(), other.getUserId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getUserId()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getUserId()).append(getUserName()).append(getFirstName()).append(getLastName()).append(isEnabled()).toString();
	}
}