package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;

import org.joda.time.Interval;

public interface GroupReservationDao extends EntityDao<GroupReservation> {
	public List<GroupReservation> findGroupReservationInclusive(Interval dateInterval);
	public List<GroupReservation> findGroupReservationExclusive(Interval dateInterval);
}
