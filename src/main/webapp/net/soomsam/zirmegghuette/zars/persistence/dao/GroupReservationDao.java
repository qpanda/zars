package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;

public interface GroupReservationDao extends EntityDao<GroupReservation> {
	public List<GroupReservation> findGroupReservationByClosedDateInterval(Interval closedDateInterval, Pagination pagination);

	public List<GroupReservation> findGroupReservationByOpenDateInterval(Interval openDateInterval);
}
