package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = Preference.TABLENAME_PREFERENCE, uniqueConstraints = { @UniqueConstraint(columnNames = { User.COLUMNNAME_USERID, Preference.COLUMNNAME_NAME }) })
@NamedQueries({ @NamedQuery(name = Preference.FINDPREFERENCE_QUERYNAME, query = Preference.FINDPREFERENCE_QUERYSTRING) })
public class Preference extends BaseEntity {
	public static final String TABLENAME_PREFERENCE = "zars_preference";
	public static final String COLUMNNAME_PREFERENCEID = "preference_id";
	public static final String COLUMNNAME_PREFERENCETIMESTAMP = "preference_timestamp";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_VALUE = "value";
	public static final String COLUMNNAME_TYPE = "type";

	public static final int COLUMNLENGTH_NAME = 128;
	public static final int COLUMNLENGTH_VALUE = 256;
	public static final int COLUMNLENGTH_TYPE = 256;

	public static final String FINDPREFERENCE_QUERYNAME = "Preference.findPreference";
	public static final String FINDPREFERENCE_QUERYSTRING = "from Preference preference where preference.user.userId = :userId and preference.name = :name";

	@Id
	@GeneratedValue
	@Column(name = Preference.COLUMNNAME_PREFERENCEID, unique = true, nullable = false)
	private long preferenceId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Preference.COLUMNNAME_PREFERENCETIMESTAMP, nullable = false)
	private Date preferenceTimestamp;

	@NotNull
	@NotEmpty
	@Column(name = Preference.COLUMNNAME_NAME, nullable = false, length = Preference.COLUMNLENGTH_NAME)
	private String name;

	@Column(name = Preference.COLUMNNAME_VALUE, nullable = true, length = Preference.COLUMNLENGTH_VALUE)
	private String value;

	@NotNull
	@NotEmpty
	@Column(name = Preference.COLUMNNAME_TYPE, nullable = false, length = Preference.COLUMNLENGTH_TYPE)
	private String type;

	@NotNull
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = User.COLUMNNAME_USERID, nullable = false)
	private User user;

	protected Preference() {
		super();
	}

	public Preference(final User user, final String name, final String type) {
		super();

		this.name = name;
		this.value = null;
		this.type = type;

		associateUser(user);
	}

	public Preference(final User user, final String name, final String value, final String type) {
		super();

		this.name = name;
		this.value = value;
		this.type = type;

		associateUser(user);
	}

	public long getPreferenceId() {
		return preferenceId;
	}

	void setPreferenceId(final long preferenceId) {
		this.preferenceId = preferenceId;
	}

	public Date getPreferenceTimestamp() {
		return preferenceTimestamp;
	}

	void setPreferenceTimestamp(final Date preferenceTimestamp) {
		this.preferenceTimestamp = preferenceTimestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public boolean hasValue() {
		return null != value;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	void setUser(final User user) {
		this.user = user;
	}

	protected void associateUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		setUser(user);
		user.addPreference(this);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Preference)) {
			return false;
		}

		final Preference other = (Preference) entity;
		return new EqualsBuilder().append(getPreferenceId(), other.getPreferenceId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Preference)) {
			return false;
		}

		final Preference other = (Preference) entity;
		return new EqualsBuilder().append(getPreferenceId(), other.getPreferenceId()).append(getPreferenceTimestamp(), other.getPreferenceTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Preference)) {
			return false;
		}

		final Preference other = (Preference) entity;
		return new EqualsBuilder().append(getPreferenceId(), other.getPreferenceId()).append(getName(), other.getName()).append(getValue(), other.getValue()).append(getType(), other.getType()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Preference)) {
			return false;
		}

		final Preference other = (Preference) obj;
		return new EqualsBuilder().append(getPreferenceId(), other.getPreferenceId()).append(getPreferenceTimestamp(), other.getPreferenceTimestamp()).append(getName(), other.getName()).append(getValue(), other.getValue()).append(getType(), other.getType()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getPreferenceId()).append(getPreferenceTimestamp()).append(getName()).append(getValue()).append(getType()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getPreferenceId()).append(getPreferenceTimestamp()).append(getName()).append(getValue()).append(getType()).toString();
	}
}
