package net.soomsam.zirmegghuette.zars.enums;

public enum SettingType {
	TEST("TEST", null), DATABASE_SCHEMA_VERSION("DATABASE_SCHEMA_VERSION", null), DATABASE_SCHEMA_STATE("DATABASE_SCHEMA_STATE", null);

	private final String settingName;
	private final Object settingDefaultValue;

	private SettingType(final String settingName, final Object settingDefaultValue) {
		this.settingName = settingName;
		this.settingDefaultValue = settingDefaultValue;
	}

	public String getSettingName() {
		return settingName;
	}

	public Object getSettingDefaultValue() {
		return settingDefaultValue;
	}
}
