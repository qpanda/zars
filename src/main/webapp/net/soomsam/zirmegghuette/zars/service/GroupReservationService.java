package net.soomsam.zirmegghuette.zars.service;

import java.util.List;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public interface GroupReservationService {
	public void createAllRooms();
	
	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException;
	
	public List<GroupReservationBean> findGroupReservation(Interval dateInterval);
}
