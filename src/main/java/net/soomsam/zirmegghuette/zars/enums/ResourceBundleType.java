package net.soomsam.zirmegghuette.zars.enums;

public enum ResourceBundleType {
	DISPLAY_MESSAGES("displayMessages"), ENUM_MESSAGES("enumMessages"), VALIDATION_MESSAGES("validationMessages");

	private final String resourceBundleName;

	private ResourceBundleType(final String resourceBundleName) {
		this.resourceBundleName = resourceBundleName;
	}

	public String getResourceBundleName() {
		return resourceBundleName;
	}
}
