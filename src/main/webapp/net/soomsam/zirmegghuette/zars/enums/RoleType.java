package net.soomsam.zirmegghuette.zars.enums;

public enum RoleType {
	ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN"), ROLE_ACCOUNTANT("ROLE_ACCOUNTANT");

	private final String roleName;

	private RoleType(final String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}
