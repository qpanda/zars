package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PreferenceBean extends BaseBean {
	private final long preferenceId;

	private final Date preferenceTimestamp;

	private final PreferenceType preferenceType;

	private final Object value;

	private final Class<?> type;

	public PreferenceBean(final long preferenceId, final Date preferenceTimestamp, final PreferenceType preferenceType, final Class<?> type) {
		super();
		this.preferenceId = preferenceId;
		this.preferenceTimestamp = preferenceTimestamp;
		this.preferenceType = preferenceType;
		this.value = null;
		this.type = type;
	}

	public PreferenceBean(final long preferenceId, final Date preferenceTimestamp, final PreferenceType preferenceType, final Object value, final Class<?> type) {
		super();
		this.preferenceId = preferenceId;
		this.preferenceTimestamp = preferenceTimestamp;
		this.preferenceType = preferenceType;
		this.value = value;
		this.type = type;
	}

	public long getPreferenceId() {
		return preferenceId;
	}

	public Date getPreferenceTimestamp() {
		return preferenceTimestamp;
	}

	public PreferenceType getPreferenceType() {
		return preferenceType;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[] { "preferenceTimestamp" });
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[] { "preferenceTimestamp" });
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
