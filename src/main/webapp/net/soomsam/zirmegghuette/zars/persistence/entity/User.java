package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Collections;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = User.TABLENAME_USER)
public class User extends BaseEntity {
	public static final String TABLENAME_USER = "zars_user";
	public static final String COLUMNNAME_USERID = "user_id";
	public static final String COLUMNNAME_USERTIMESTAMP = "user_timestamp";
	public static final String COLUMNNAME_USERNAME = "username";
	public static final String COLUMNNAME_EMAILADDRESS = "email_address";
	public static final String COLUMNNAME_FIRSTNAME = "first_name";
	public static final String COLUMNNAME_LASTNAME = "last_name";
	public static final String COLUMNNAME_PASSWORD = "password";
	public static final String COLUMNNAME_ENABLED = "active";
	public static final String JOINTABLENAME_USER_ROLE = "zars_user_zars_role";
	
	public static final String FINDUSER_ROLEID_QUERYNAME = "User.findUserByRoleId";
	public static final String FINDUSER_ROLEID_QUERYSTRING = "from User where roles.roleId = :roleId";

	@Id
	@GeneratedValue
	@Column(name = User.COLUMNNAME_USERID, unique = true, nullable = false)
	private long userId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = User.COLUMNNAME_USERTIMESTAMP)
	private Date userTimestamp;

	@NotNull
	@NotEmpty
	@Column(name = User.COLUMNNAME_USERNAME, unique = true, nullable = false, length = 128)
	private String username;

	@NotNull
	@NotEmpty
	@Column(name = User.COLUMNNAME_PASSWORD, nullable = false, length = 256)
	private String password;

	@NotNull
	@NotEmpty
	@Email
	@Column(name = User.COLUMNNAME_EMAILADDRESS, unique = true, nullable = false, length = 128)
	private String emailAddress;

	@Column(name = User.COLUMNNAME_FIRSTNAME, nullable = true, length = 256)
	private String firstName;

	@Column(name = User.COLUMNNAME_LASTNAME, nullable = true, length = 256)
	private String lastName;

	@Column(name = User.COLUMNNAME_ENABLED, nullable = false)
	private boolean enabled;

	@NotNull
	@Size(min = 1)
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.EAGER)
	@JoinTable(name = User.JOINTABLENAME_USER_ROLE, joinColumns = @JoinColumn(name = User.COLUMNNAME_USERID), inverseJoinColumns = @JoinColumn(name = Role.COLUMNNAME_ROLEID))
	private final Set<Role> roles = new HashSet<Role>(0);

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "beneficiary")
	private final Set<GroupReservation> beneficiaryGroupReservations = new HashSet<GroupReservation>(0);

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, mappedBy = "accountant")
	private final Set<GroupReservation> accountantGroupReservations = new HashSet<GroupReservation>(0);

	protected User() {
		super();
	}

	public User(final String username, final String password, final String emailAddress, final boolean enabled, final Role role) {
		super();

		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.enabled = enabled;

		associateRole(role);
	}

	public User(final String username, final String password, final String emailAddress, final boolean enabled, final Set<Role> roles) {
		super();
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.enabled = enabled;

		associateRoles(roles);
	}

	public User(final String username, final String password, final String emailAddress, final String firstName, final String lastName, final boolean enabled, final Set<Role> roles) {
		super();
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;

		associateRoles(roles);
	}

	public User(final String username, final String password, final String emailAddress, final String firstName, final String lastName, final boolean enabled, final Role role) {
		super();
		this.username = username;
		this.password = password;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;

		associateRole(role);
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

	void setUserTimestamp(final Date userTimestamp) {
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

	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(roles);
	}

	void addRole(final Role role) {
		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		this.roles.add(role);
	}

	void removeRole(final Role role) {
		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		this.roles.remove(role);
	}

	public void associateRole(final Role role) {
		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		addRole(role);
		role.addUser(this);
	}

	public void associateRoles(final Set<Role> roleSet) {
		if (null == roleSet) {
			throw new IllegalArgumentException("'roleSet' must not be null");
		}

		for (final Role role : roleSet) {
			associateRole(role);
		}
	}

	public void unassociateRole(final Role role) {
		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		removeRole(role);
		role.removeUser(this);
	}

	public void unassociateRoles(final Set<Role> roleSet) {
		if (null == roleSet) {
			throw new IllegalArgumentException("'roleSet' must not be null");
		}

		for (final Role role : roleSet) {
			unassociateRole(role);
		}
	}

	public void updateRoles(final Set<Role> newRoleSet) {
		if (null == newRoleSet) {
			throw new IllegalArgumentException("'roleSet' must not be null");
		}

		final Set<Role> oldRoleSet = getRoles();

		final Set<Role> addedRoleSet = new HashSet<Role>(newRoleSet);
		addedRoleSet.removeAll(oldRoleSet);

		final Set<Role> removedRoleSet = new HashSet<Role>(oldRoleSet);
		removedRoleSet.removeAll(newRoleSet);

		unassociateRoles(removedRoleSet);
		associateRoles(addedRoleSet);
	}

	public Set<GroupReservation> getBeneficiaryGroupReservations() {
		return Collections.unmodifiableSet(beneficiaryGroupReservations);
	}

	void addBeneficiaryGroupReservation(final GroupReservation beneficiaryGroupReservation) {
		if (null == beneficiaryGroupReservation) {
			throw new IllegalArgumentException("'beneficiaryGroupReservation' must not be null");
		}

		this.beneficiaryGroupReservations.add(beneficiaryGroupReservation);
	}

	void removeBeneficiaryGroupReservation(final GroupReservation beneficiaryGroupReservation) {
		if (null == beneficiaryGroupReservation) {
			throw new IllegalArgumentException("'beneficiaryGroupReservation' must not be null");
		}

		this.beneficiaryGroupReservations.remove(beneficiaryGroupReservation);
	}

	public Set<GroupReservation> getAccountantGroupReservations() {
		return Collections.unmodifiableSet(accountantGroupReservations);
	}

	void addAccountantGroupReservation(final GroupReservation accountantGroupReservation) {
		if (null == accountantGroupReservation) {
			throw new IllegalArgumentException("'accountantGroupReservation' must not be null");
		}

		this.accountantGroupReservations.add(accountantGroupReservation);
	}

	void removeAccountantGroupReservation(final GroupReservation accountantGroupReservation) {
		if (null == accountantGroupReservation) {
			throw new IllegalArgumentException("'accountantGroupReservation' must not be null");
		}

		this.accountantGroupReservations.remove(accountantGroupReservation);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof User)) {
			return false;
		}

		final User other = (User) entity;
		return new EqualsBuilder().append(getUserId(), other.getUserId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof User)) {
			return false;
		}

		final User other = (User) entity;
		return new EqualsBuilder().append(getUserId(), other.getUserId()).append(getUserTimestamp(), other.getUserTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof User)) {
			return false;
		}

		final User other = (User) entity;
		return new EqualsBuilder().append(getUserId(), other.getUserId()).append(getUsername(), other.getUsername()).append(getPassword(), other.getPassword()).append(getEmailAddress(), other.getEmailAddress()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).append(isEnabled(), other.isEnabled()).isEquals();
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
		return new EqualsBuilder().append(getUserId(), other.getUserId()).append(getUserTimestamp(), other.getUserTimestamp()).append(getUsername(), other.getUsername()).append(getPassword(), other.getPassword()).append(getEmailAddress(), other.getEmailAddress()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).append(isEnabled(), other.isEnabled()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getUserId()).append(getUserTimestamp()).append(getUsername()).append(getPassword()).append(getEmailAddress()).append(getFirstName()).append(getLastName()).append(isEnabled()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getUserId()).append(getUserTimestamp()).append(getUsername()).append(getPassword()).append(getEmailAddress()).append(getFirstName()).append(getLastName()).append(isEnabled()).toString();
	}
}