package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import net.soomsam.zirmegghuette.zars.enums.CategoryType;
import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EventDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;
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
		if (CategoryType.CATEGORY_PERSISTENCE.equals(categoryType)) {
			throw new IllegalArgumentException("'categoryType' [" + CategoryType.CATEGORY_PERSISTENCE.getCategoryName() + "] not supported");
		}

		return new Event(categoryType.getCategoryName(), message, user);
	}

	@Override
	public Event create(final User user, final long entityId, final Class<? extends BaseEntity> entityType, final OperationType entityOperation, final String message) {
		return new Event(CategoryType.CATEGORY_PERSISTENCE.getCategoryName(), message, entityId, entityType.getSimpleName(), entityOperation.getOperationName(), user);
	}

	@Override
	public List<Event> findLatest(final Pagination pagination) {
		final TypedQuery<Event> findEventTypedQuery = createNamedTypedQuery(Event.FINDEVENT_QUERYNAME);

		if (null != pagination) {
			findEventTypedQuery.setFirstResult(pagination.getFirstResult());
			findEventTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findEventTypedQuery.getResultList();
	}

	@Override
	public List<Event> findByOpenDateInterval(final Interval openDateInterval, final Pagination pagination) {
		if (null == openDateInterval) {
			throw new IllegalArgumentException("'openDateInterval' must not be null");
		}

		final TypedQuery<Event> findEventByOpenDateIntervalTypedQuery = createNamedTypedQuery(Event.FINDEVENTOPENINTERVAL_STARTTIMESTAMP_ENDTIMESTAMP_QUERYNAME);
		findEventByOpenDateIntervalTypedQuery.setParameter("startTimestamp", openDateInterval.getStart().toDateMidnight().toDate(), TemporalType.TIMESTAMP);
		findEventByOpenDateIntervalTypedQuery.setParameter("endTimestamp", openDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.TIMESTAMP);

		if (null != pagination) {
			findEventByOpenDateIntervalTypedQuery.setFirstResult(pagination.getFirstResult());
			findEventByOpenDateIntervalTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findEventByOpenDateIntervalTypedQuery.getResultList();
	}

	@Override
	public List<Event> findByUserId(final long userId, final Pagination pagination) {
		final TypedQuery<Event> findEventByUserIdTypedQuery = createNamedTypedQuery(Event.FINDEVENT_USERID_QUERYNAME);
		findEventByUserIdTypedQuery.setParameter("userId", userId);

		if (null != pagination) {
			findEventByUserIdTypedQuery.setFirstResult(pagination.getFirstResult());
			findEventByUserIdTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findEventByUserIdTypedQuery.getResultList();
	}
}
