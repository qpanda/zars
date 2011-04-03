package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationNonconsecutiveException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GroupReservationService {
	public void createAllRooms();

	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, Set<ReservationVo> reservationVoSet, String comment) throws GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException;

	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException, InsufficientPermissionException;

	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, Set<ReservationVo> reservationVoSet, String comment) throws GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException;

	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public GroupReservationBean deleteGroupReservation(long groupReservationId) throws InsufficientPermissionException;

	@PreAuthorize("isAuthenticated()")
	public long countGroupReservation(Interval dateInterval);

	@PreAuthorize("isAuthenticated()")
	public long countGroupReservation(long beneficiaryId, Interval dateInterval);

	@PreAuthorize("isAuthenticated()")
	public long countGroupReservation(long beneficiaryId);

	@PreAuthorize("isAuthenticated()")
	public long countGroupReservation();

	@PreAuthorize("isAuthenticated()")
	public List<GroupReservationBean> findGroupReservation(Interval dateInterval, Pagination pagination);

	@PreAuthorize("isAuthenticated()")
	public List<GroupReservationBean> findGroupReservation(long beneficiaryId, Interval dateInterval, Pagination pagination);

	@PreAuthorize("isAuthenticated()")
	public List<GroupReservationBean> findGroupReservation(long beneficiaryId, Pagination pagination);

	@PreAuthorize("isAuthenticated()")
	public List<GroupReservationBean> findGroupReservation(Pagination pagination);

	@PreAuthorize("isAuthenticated()")
	public GroupReservationBean retrieveGroupReservation(long groupReservationId);
}
