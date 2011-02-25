package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.CategoryType;
import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EventDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.springframework.stereotype.Repository;

@Repository("eventDao")
public class JpaEventDao extends JpaEntityDao<Event> implements EventDao {
	@Override
	protected Class<Event> determineEntityClass() {
		return Event.class;
	}

	@Override
	public void remove(final Event entity) {
		throw new OperationNotSupportedException("[" + JpaEventDao.class.getSimpleName() + "] does not support operation 'remove'");
	}

	@Override
	public void removeAll(final Set<Event> entitySet) {
		throw new OperationNotSupportedException("[" + JpaEventDao.class.getSimpleName() + "] does not support operation 'removeAll'");
	}

	@Override
	public Event create(final User user, final CategoryType categoryType, final String message) {
		if (CategoryType.PERSISTENCE.equals(categoryType)) {
			throw new IllegalArgumentException("'categoryType' [" + CategoryType.PERSISTENCE.getCategoryName() + "] not supported");
		}

		return new Event(categoryType.getCategoryName(), message, user);
	}

	@Override
	public Event create(final User user, final long entityId, final Class<? extends BaseEntity> entityType, final OperationType entityOperation, final String message) {
		return new Event(CategoryType.PERSISTENCE.getCategoryName(), message, entityId, entityType.getSimpleName(), entityOperation.getOperationName(), user);
	}
}
