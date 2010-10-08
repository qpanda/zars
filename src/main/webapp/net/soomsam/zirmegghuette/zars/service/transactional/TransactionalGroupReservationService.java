package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.ReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.utils.ServiceBeanMapper;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.Pagination;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("groupReservationService")
@Transactional(timeout = 1000)
public class TransactionalGroupReservationService implements GroupReservationService {
	@Autowired
	private ServiceBeanMapper serviceBeanMapper;

	@Autowired
	private GroupReservationDao groupReservationDao;

	@Autowired
	private ReservationDao reservationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoomDao roomDao;

	@Override
	public void createAllRooms() {
		roomDao.persist(new Room("ROOM_PRIMARY", 4, 1, true));
		roomDao.persist(new Room("ROOM_SECONDARY", 4, 2, true));
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class })
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final DateMidnight arrival, final DateMidnight departure, final long guests, final String comment) throws GroupReservationConflictException, InsufficientPermissionException {
		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		assertCreateGroupReservationAllowed(beneficiaryId);
		assertNonConflictingArrivalDepature(arrival, departure);

		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = new GroupReservation(beneficiary, accountant, arrival, departure, guests, comment);
		groupReservation.associateRooms(requiredRooms);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO search for reports covering date range of newly added group reservation and mark them as stale
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class })
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final Set<ReservationVo> reservationVoSet, final String comment) throws GroupReservationConflictException, InsufficientPermissionException {
		if ((null == reservationVoSet) || (reservationVoSet.isEmpty())) {
			throw new IllegalArgumentException("'reservationVoSet' must not be null or empty");
		}

		assertCreateGroupReservationAllowed(beneficiaryId);

		Set<Reservation> reservationSet = new HashSet<Reservation>();
		for (ReservationVo reservationVo : reservationVoSet) {
			assertNonConflictingArrivalDepature(reservationVo.getArrival(), reservationVo.getDeparture());

			Reservation reservation = new Reservation(reservationVo.getPrecedence(), reservationVo.getArrival(), reservationVo.getDeparture(), reservationVo.getFirstName(), reservationVo.getLastName());
			reservationSet.add(reservation);
		}

		final int guests = reservationVoSet.size();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = new GroupReservation(beneficiary, accountant, reservationSet, comment);
		groupReservation.associateRooms(requiredRooms);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO assert no gaps (implementation should be on GroupReservation entity or here?)
		// TODO required capacity fulfilled???
		// TODO search for reports covering date range of newly added group reservation and mark them as stale
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class })
	public GroupReservationBean updateGroupReservation(final long groupReservationId, final long beneficiaryId, final long accountantId, final DateMidnight arrival, final DateMidnight departure, final long guests, final String comment) throws GroupReservationConflictException, InsufficientPermissionException {
		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		assertUpdateGroupReservationAllowed(groupReservationId, beneficiaryId);
		assertNonConflictingArrivalDepature(arrival, departure, groupReservationId);

		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);
		groupReservation.setArrival(arrival);
		groupReservation.setDeparture(departure);
		groupReservation.setGuests(guests);
		groupReservation.setComment(comment);
		groupReservation.associateBeneficiary(beneficiary);
		groupReservation.associateAccountant(accountant);
		groupReservation.updateRooms(requiredRooms);
		groupReservation.markInvoiceStale();
		groupReservation.markReportsStale();

		Set<Reservation> oldReservations = groupReservation.getReservations();
		groupReservation.unassociateReservations(oldReservations);
		reservationDao.removeAll(oldReservations);

		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO only admin should be allowed to update group reservation that had already been payed
		// TODO search for reports covering new date range of group reservation and mark them as stale
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class })
	public GroupReservationBean updateGroupReservation(final long groupReservationId, final long beneficiaryId, final long accountantId, final Set<ReservationVo> reservationVoSet, final String comment) throws GroupReservationConflictException, InsufficientPermissionException {
		if ((null == reservationVoSet) || (reservationVoSet.isEmpty())) {
			throw new IllegalArgumentException("'reservationVoSet' must not be null or empty");
		}

		assertUpdateGroupReservationAllowed(groupReservationId, beneficiaryId);

		Set<Reservation> newReservationSet = new HashSet<Reservation>();
		for (ReservationVo reservationVo : reservationVoSet) {
			assertNonConflictingArrivalDepature(reservationVo.getArrival(), reservationVo.getDeparture(), groupReservationId);

			Reservation newReservation = new Reservation(reservationVo.getPrecedence(), reservationVo.getArrival(), reservationVo.getDeparture(), reservationVo.getFirstName(), reservationVo.getLastName());
			newReservationSet.add(newReservation);
		}

		final int guests = reservationVoSet.size();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);
		groupReservation.setComment(comment);
		groupReservation.associateBeneficiary(beneficiary);
		groupReservation.associateAccountant(accountant);
		groupReservation.updateRooms(requiredRooms);
		groupReservation.markInvoiceStale();
		groupReservation.markReportsStale();

		Set<Reservation> oldReservations = groupReservation.getReservations();
		groupReservation.unassociateReservations(oldReservations);
		reservationDao.removeAll(oldReservations);
		groupReservation.associateReservations(newReservationSet);

		groupReservation.autoSetArrivalDeparture();
		groupReservation.autoSetGuests();
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO only admin should be allowed to update group reservation that had already been payed
		// TODO search for reports covering new date range of group reservation and mark them as stale
		// TODO revise delete/add logic for reservations
	}

	@Override
	@Transactional(rollbackFor = InsufficientPermissionException.class)
	public void deleteGroupReservation(final long groupReservationId) throws InsufficientPermissionException {
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);

		assertDeleteGroupReservationAllowed(groupReservationId, groupReservation.getBeneficiary().getUserId());

		groupReservationDao.remove(groupReservation);

		// TODO only admin should be allowed to delete group reservation that had already been payed
		// TODO search for reports covering new date range of group reservation and mark them as stale
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final Interval dateInterval, final Pagination pagination) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findGroupReservationByClosedDateInterval(dateInterval, pagination));
	}

	@Override
	public GroupReservationBean retrieveGroupReservation(final long groupReservationId) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.retrieveByPrimaryKey(groupReservationId));
	}

	protected Set<Room> determineRequiredRooms(final long requiredCapacity) {
		// assumes BR001
		long availableCapacity = 0;
		Set<Room> requiredRoomList = new HashSet<Room>();

		List<Room> availableRoomList = roomDao.findByPrecedence(true);
		for (Room availableRoom : availableRoomList) {
			requiredRoomList.add(availableRoom);
			availableCapacity += availableRoom.getCapacity();

			if (availableCapacity >= requiredCapacity) {
				return requiredRoomList;
			}
		}

		return requiredRoomList;
		// TODO required capacity fulfilled???
	}

	protected void assertNonConflictingArrivalDepature(final DateMidnight arrival, final DateMidnight departure) throws GroupReservationConflictException {
		// implements BR001
		Interval openArrivalDepartureInterval = new Interval(arrival, departure);
		List<GroupReservation> conflictingGroupReservations = groupReservationDao.findGroupReservationByOpenDateInterval(openArrivalDepartureInterval);
		if (!conflictingGroupReservations.isEmpty()) {
			throw new GroupReservationConflictException("unable to create group reservation with arrival [" + arrival + "] and departure [" + departure + "]. it conflicts with [" + conflictingGroupReservations.size() + "] existing group reservations", serviceBeanMapper.map(GroupReservationBean.class, conflictingGroupReservations));
		}
	}

	protected void assertNonConflictingArrivalDepature(final DateMidnight arrival, final DateMidnight departure, final long excludeGroupReservationId) throws GroupReservationConflictException {
		// implements BR001
		Interval openArrivalDepartureInterval = new Interval(arrival, departure);
		List<GroupReservation> conflictingGroupReservationList = groupReservationDao.findGroupReservationByOpenDateInterval(openArrivalDepartureInterval);
		Iterator<GroupReservation> conflictingGroupReservationIterator = conflictingGroupReservationList.iterator();
		while (conflictingGroupReservationIterator.hasNext()) {
			GroupReservation conflictingGroupReservation = conflictingGroupReservationIterator.next();
			if (excludeGroupReservationId == conflictingGroupReservation.getGroupReservationId()) {
				conflictingGroupReservationIterator.remove();
			}
		}

		if (!conflictingGroupReservationList.isEmpty()) {
			throw new GroupReservationConflictException("unable to create group reservation with arrival [" + arrival + "] and departure [" + departure + "]. it conflicts with [" + conflictingGroupReservationList.size() + "] existing group reservations", serviceBeanMapper.map(GroupReservationBean.class, conflictingGroupReservationList));
		}
	}

	protected void assertCreateGroupReservationAllowed(final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to create group reservations for other users", currentUser.getUserId(), OperationType.ADD);
		}
	}

	protected void assertUpdateGroupReservationAllowed(final long groupReservationId, final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to update group reservations of other users", currentUser.getUserId(), groupReservationBeneficiaryId, OperationType.UPDATE);
		}
	}

	protected void assertDeleteGroupReservationAllowed(final long groupReservationId, final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to delete group reservations of other users", currentUser.getUserId(), groupReservationBeneficiaryId, OperationType.DELETE);
		}
	}
}
