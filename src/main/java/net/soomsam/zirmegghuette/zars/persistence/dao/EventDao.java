package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.enums.CategoryType;
import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

public interface EventDao extends EntityDao<Event> {
	public Event create(final User user, final CategoryType categoryType, final String message);

	public Event create(final User user, final long entityId, final Class<? extends BaseEntity> entityType, final OperationType entityOperation, final String message);
}
