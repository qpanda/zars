package net.soomsam.zirmegghuette.zars.enums;

public enum PreferenceType {
	TIMEZONE("TIMEZONE");

	private final String preferenceName;

	private PreferenceType(final String preferenceName) {
		this.preferenceName = preferenceName;
	}

	public String getPreferenceName() {
		return preferenceName;
	}
}
