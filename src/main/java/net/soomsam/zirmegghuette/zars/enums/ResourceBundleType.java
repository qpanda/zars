package net.soomsam.zirmegghuette.zars.enums;

public enum ResourceBundleType {
	DISPLAY_MESSAGES("displayMessages", "DisplayMessages"), ENUM_MESSAGES("enumMessages", "EnumMessages"), VALIDATION_MESSAGES("validationMessages", "ValidationMessages");

	private final String resourceBundleName;
	private final String resourceBundleFileName;

	private ResourceBundleType(final String resourceBundleName, final String resourceBundleFileName) {
		this.resourceBundleName = resourceBundleName;
		this.resourceBundleFileName = resourceBundleFileName;
	}

	public String getResourceBundleName() {
		return resourceBundleName;
	}

	public String getResourceBundleFileName() {
		return resourceBundleFileName;
	}
}
