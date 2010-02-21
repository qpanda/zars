package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = Setting.TABLENAME_SETTING)
public class Setting extends BaseEntity {
	public static final String TABLENAME_SETTING = "setting";
	public static final String COLUMNNAME_SETTINGID = "setting_id";
	public static final String COLUMNNAME_NAME = "name";
	public static final String COLUMNNAME_VALUE = "value";
	public static final String COLUMNNAME_TYPE = "type";

	@Id
	@GeneratedValue
	@Column(name = Setting.COLUMNNAME_SETTINGID, unique = true, nullable = false)
	private long settingId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@NotNull
	@NotEmpty
	@Column(name = Setting.COLUMNNAME_NAME, nullable = false, unique = true, length = 256)
	private String name;

	@Column(name = Setting.COLUMNNAME_VALUE, nullable = true, length = 256)
	private String value;

	@NotNull
	@NotEmpty
	@Column(name = Setting.COLUMNNAME_TYPE, nullable = false, length = 256)
	private String type;

	protected Setting() {
		super();
	}

	protected Setting(String name, String value, String type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public long getSettingId() {
		return settingId;
	}

	public void setSettingId(long settingId) {
		this.settingId = settingId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean same(BaseEntity entity) {
		if (!(entity instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) entity;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).isEquals();
	}

	@Override
	public boolean sameVersion(BaseEntity entity) {
		if (!(entity instanceof Setting)) {
			return false;
		}

		final Setting other = (Setting) entity;
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).append(getTimestamp(), other.getTimestamp()).isEquals();
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
		return new EqualsBuilder().append(getSettingId(), other.getSettingId()).append(getTimestamp(), other.getTimestamp()).append(getName(), other.getName()).append(getValue(), other.getValue()).append(getType(), other.getType()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSettingId()).append(getTimestamp()).append(getName()).append(getValue()).append(getType()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getSettingId()).append(getTimestamp()).append(getName()).append(getValue()).append(getType()).toString();
	}
}
