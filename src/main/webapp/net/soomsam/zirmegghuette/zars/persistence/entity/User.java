package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Collections;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Size;
import org.hibernate.validator.event.JPAValidateListener;

@Entity
@Table(name = User.TABLENAME_USER)
@EntityListeners(value = { JPAValidateListener.class })
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

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@NotEmpty
	@Column(name = User.COLUMNNAME_USERNAME, unique = true, nullable = false, length = 256)
	private String userName;

	@NotNull
	@NotEmpty
	@Column(name = User.COLUMNNAME_PASSWORD, nullable = false, length = 256)
	private String password;

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

	public User(final String userName, final String password, final boolean enabled, final Role role) {
		super();

		if (null == role) {
			throw new IllegalArgumentException("'role' must not be null");
		}

		this.userName = userName;
		this.password = password;
		this.enabled = enabled;

		associateRole(role);
	}

	public User(final String userName, final String password, final boolean enabled, final Set<Role> roles) {
		super();
		this.userName = userName;
		this.password = password;
		this.enabled = enabled;

		associateRoles(roles);
	}

	public User(final String userName, final String password, final String firstName, final String lastName, final boolean enabled, final Set<Role> roles) {
		super();
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;

		associateRoles(roles);
	}

	public User(final String userName, final String password, final String firstName, final String lastName, final boolean enabled, final Role role) {
		super();
		this.userName = userName;
		this.password = password;
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

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof User)) {
			return false;
		}

		final User other = (User) obj;
		return new EqualsBuilder().append(getUserId(), other.getUserId()).append(getTimestamp(), other.getTimestamp()).append(getUserName(), other.getUserName()).append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName()).append(isEnabled(), other.isEnabled()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getUserId()).append(getTimestamp()).append(getUserName()).append(getFirstName()).append(getLastName()).append(isEnabled()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getUserId()).append(getTimestamp()).append(getUserName()).append(getFirstName()).append(getLastName()).append(isEnabled()).toString();
	}
}