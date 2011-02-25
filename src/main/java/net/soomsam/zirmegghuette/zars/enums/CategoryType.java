package net.soomsam.zirmegghuette.zars.enums;

public enum CategoryType {
	CATEGORY_SESSION("CATEGORY_SESSION"), CATEGORY_PERSISTENCE("CATEGORY_PERSISTENCE"), CATEGORY_NOTIFICATION("CATEGORY_NOTIFICATION");

	private final String categoryName;

	private CategoryType(final String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}
}
