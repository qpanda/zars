package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = Setting.TABLENAME_SETTING)
@NamedQueries({ @NamedQuery(name = Setting.FINDSETTING_QUERYNAME, query = Setting.FINDSETTING_QUERYSTRING) })
public class Setting extends BaseEntity {
	public static final String TABLENAME_SETTING = "zars_setting";
	public static final String COLUMNNAME_SETTINGID = "setting_id";
	public static final String COLUMNNAME_SETTINGTIMESTAMP = "setting_timestamp";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_VALUE = "value";
	public static final String COLUMNNAME_TYPE = "type";

	public static final int COLUMNLENGTH_NAME = 128;
	public static final int COLUMNLENGTH_VALUE = 256;
	public static final int COLUMNLENGTH_TYPE = 256;

	public static final String FINDSETTING_QUERYNAME = "Setting.findSettingQuery";
	public static final String FINDSETTING_QUERYSTRING = "from Setting where name = :name";

	@Id
	@GeneratedValue
	@Column(name = Setting.COLUMNNAME_SETTINGID, unique = true, nullable = false)
	private long settingId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Setting.COLUMNNAME_SETTINGTIMESTAMP, nullable = false)
	private Date settingTimestamp;

	@NotNull
	@NotEmpty
	@Column(name = Setting.COLUMNNAME_NAME, nullable = false, unique = true, length = Setting.COLUMNLENGTH_NAME)
	private String name;

	@Column(name = Setting.COLUMNNAME_VALUE, nullable = true, length = Setting.COLUMNLENGTH_VALUE)
	private String value;

	@NotNull
	@NotEmpty
	@Column(name = Setting.COLUMNNAME_TYPE, nullable = false, length = Setting.COLUMNLENGTH_TYPE)
	private String type;

	protected Setting() {
		super();
	}

	public Setting(final String name, final String type) {
		super();
		this.name = name;
		this.value = null;
		this.type = type;
	}

	public Setting(final String name, final String value, final String type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public long getSettingId() {
		return settingId;
	}

	void setSettingId(final long settingId) {
		this.settingId = settingId;
	}

	public Date getSettingTimestamp() {
		return settingTimestamp;
	}

	void setSettingTimestamp(final Date settingTimestamp) {
		this.settingTimestamp = settingTimestamp;
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

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) entity;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) entity;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).append(getSettingTimestamp(), other.getSettingTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) entity;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).append(getName(), other.getName()).append(getValue(), other.getValue()).append(getType(), other.getType()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) obj;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).append(getSettingTimestamp(), other.getSettingTimestamp()).append(getName(), other.getName()).append(getValue(), other.getValue()).append(getType(), other.getType()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSettingId()).append(getSettingTimestamp()).append(getName()).append(getValue()).append(getType()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getSettingId()).append(getSettingTimestamp()).append(getName()).append(getValue()).append(getType()).toString();
	}
}
