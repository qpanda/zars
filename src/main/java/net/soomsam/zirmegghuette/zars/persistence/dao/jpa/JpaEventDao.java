package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.Set;

import net.soomsam.zirmegghuette.zars.persistence.dao.EventDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;

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
}
