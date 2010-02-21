package net.soomsam.zirmegghuette.zars.persistence.entity;

public abstract class BaseEntity {
	public abstract boolean same(BaseEntity entity);

	public abstract boolean sameVersion(BaseEntity entity);
}
