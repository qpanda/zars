package net.soomsam.zirmegghuette.zars.enums;

public enum PreferenceType {
	TIMEZONE("TIMEZONE", "Europe/Vienna"), LOCALE("LOCALE", "German"), NOTIFICATION("NOTIFICATION", true);

	private final String preferenceName;
	private final Object preferenceDefaultValue;

	private PreferenceType(final String preferenceName, final Object preferenceDefaultValue) {
		this.preferenceName = preferenceName;
		this.preferenceDefaultValue = preferenceDefaultValue;
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public Object getPreferenceDefaultValue() {
		return preferenceDefaultValue;
	}
}
