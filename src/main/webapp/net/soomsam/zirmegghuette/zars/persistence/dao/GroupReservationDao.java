package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;

public interface GroupReservationDao extends EntityDao<GroupReservation> {
	public long countGroupReservationByClosedDateInterval(final Interval closedDateInterval);

	public List<GroupReservation> findGroupReservationByClosedDateInterval(final Interval closedDateInterval, final Pagination pagination);

	public List<GroupReservation> findGroupReservationByOpenDateInterval(final Interval openDateInterval);
}
