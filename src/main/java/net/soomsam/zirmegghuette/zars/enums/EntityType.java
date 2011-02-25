package net.soomsam.zirmegghuette.zars.enums;

public enum EntityType {
	ENTITY_USER("ENTITY_USER"), ENTITY_RESERVATION("ENTITY_RESERVATION");

	private final String entityName;

	private EntityType(final String entityName) {
		this.entityName = entityName;
	}

	public String getEntityName() {
		return entityName;
	}
}
