package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import net.soomsam.zirmegghuette.zars.enums.SettingType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SettingBean extends BaseBean {
	private final long settingId;

	private final Date settingTimestamp;

	private final SettingType settingType;

	private final Object value;

	private final Class<?> type;

	public SettingBean(final long settingId, final Date settingTimestamp, final SettingType settingType, final Class<?> type) {
		super();
		this.settingId = settingId;
		this.settingTimestamp = settingTimestamp;
		this.settingType = settingType;
		this.value = null;
		this.type = type;
	}

	public SettingBean(final long settingId, final Date settingTimestamp, final SettingType settingType, final Object value, final Class<?> type) {
		super();
		this.settingId = settingId;
		this.settingTimestamp = settingTimestamp;
		this.settingType = settingType;
		this.value = value;
		this.type = type;
	}

	public long getSettingId() {
		return settingId;
	}

	public Date getSettingTimestamp() {
		return settingTimestamp;
	}

	public SettingType getSettingType() {
		return settingType;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getType() {
		return type;
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
