package net.soomsam.zirmegghuette.zars.enums;

public enum CategoryType {
	SESSION("SESSION"), PERSISTENCE("PERSISTENCE"), NOTIFICATION("NOTIFICATION");

	private final String categoryName;

	private CategoryType(final String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}
}
