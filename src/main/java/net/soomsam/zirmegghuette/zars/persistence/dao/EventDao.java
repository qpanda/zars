package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import org.joda.time.Interval;

import net.soomsam.zirmegghuette.zars.enums.CategoryType;
import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

public interface EventDao extends EntityDao<Event> {
	public Event create(final User user, final CategoryType categoryType, final String message);

	public Event create(final User user, final long entityId, final Class<? extends BaseEntity> entityType, final OperationType entityOperation, final String message);

	public List<Event> findLatest(final Pagination pagination);

	public List<Event> findByOpenDateInterval(final Interval openDateInterval, final Pagination pagination);

	public List<Event> findByUserId(final long userId, final Pagination pagination);
}
