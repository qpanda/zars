package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SettingBean extends BaseBean {
	private final long settingId;

	private final Date settingTimestamp;

	private final String name;

	private final Object value;

	private final Class type;

	public SettingBean(long settingId, Date settingTimestamp, String name, Class type) {
		super();
		this.settingId = settingId;
		this.settingTimestamp = settingTimestamp;
		this.name = name;
		this.value = null;
		this.type = type;
	}

	public SettingBean(long settingId, Date settingTimestamp, String name, Object value, Class type) {
		super();
		this.settingId = settingId;
		this.settingTimestamp = settingTimestamp;
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public long getSettingId() {
		return settingId;
	}

	public Date getSettingTimestamp() {
		return settingTimestamp;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public Class getType() {
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
