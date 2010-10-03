package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;

public interface GroupReservationService {
	public void createAllRooms();

	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, Set<ReservationVo> reservationVoSet, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, Set<ReservationVo> reservationVoSet, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	public List<GroupReservationBean> findGroupReservation(Interval dateInterval);

	public GroupReservationBean retrieveGroupReservation(long groupReservationId);
}
