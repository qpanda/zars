package net.soomsam.zirmegghuette.zars.service;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;

import org.joda.time.Interval;

public interface GroupReservationService {
	public List<GroupReservation> findGroupReservation(Interval dateInterval);
}
