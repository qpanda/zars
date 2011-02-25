package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationNonconsecutiveException;
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
import org.joda.time.DateTime;
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

		final DateTime booked = new DateTime();
		final User booker = userDao.retrieveCurrentUser();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = new GroupReservation(booker, beneficiary, accountant, booked, arrival, departure, guests, comment);
		groupReservation.associateRooms(requiredRooms);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO search for reports covering date range of newly added group reservation and mark them as stale
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class, GroupReservationNonconsecutiveException.class })
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final Set<ReservationVo> reservationVoSet, final String comment) throws GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		if ((null == reservationVoSet) || (reservationVoSet.isEmpty())) {
			throw new IllegalArgumentException("'reservationVoSet' must not be null or empty");
		}

		assertCreateGroupReservationAllowed(beneficiaryId);

		final Set<Reservation> reservationSet = new HashSet<Reservation>();
		for (final ReservationVo reservationVo : reservationVoSet) {
			assertNonConflictingArrivalDepature(reservationVo.getArrival(), reservationVo.getDeparture());

			final Reservation reservation = new Reservation(reservationVo.getPrecedence(), reservationVo.getArrival(), reservationVo.getDeparture(), reservationVo.getFirstName(), reservationVo.getLastName());
			reservationSet.add(reservation);
		}

		final int guests = reservationVoSet.size();
		final DateTime booked = new DateTime();
		final User booker = userDao.retrieveCurrentUser();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = new GroupReservation(booked, booker, beneficiary, accountant, reservationSet, comment);
		groupReservation.associateRooms(requiredRooms);
		groupReservation.autoSetArrivalDeparture();
		groupReservation.autoSetGuests();

		assertConsecutiveArrivalDeparture(groupReservation);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

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

		final DateTime booked = new DateTime();
		final User booker = userDao.retrieveCurrentUser();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);
		groupReservation.setBooked(booked);
		groupReservation.setArrival(arrival);
		groupReservation.setDeparture(departure);
		groupReservation.setGuests(guests);
		groupReservation.setComment(comment);
		groupReservation.associateBooker(booker);
		groupReservation.associateBeneficiary(beneficiary);
		groupReservation.associateAccountant(accountant);
		groupReservation.updateRooms(requiredRooms);
		groupReservation.markInvoiceStale();
		groupReservation.markReportsStale();

		final Set<Reservation> oldReservations = groupReservation.getReservations();
		groupReservation.unassociateReservations(oldReservations);
		reservationDao.removeAll(oldReservations);

		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO only admin should be allowed to update group reservation that had already been payed
		// TODO search for reports covering new date range of group reservation and mark them as stale
	}

	@Override
	@Transactional(rollbackFor = { GroupReservationConflictException.class, InsufficientPermissionException.class, GroupReservationNonconsecutiveException.class })
	public GroupReservationBean updateGroupReservation(final long groupReservationId, final long beneficiaryId, final long accountantId, final Set<ReservationVo> reservationVoSet, final String comment) throws GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		if ((null == reservationVoSet) || (reservationVoSet.isEmpty())) {
			throw new IllegalArgumentException("'reservationVoSet' must not be null or empty");
		}

		assertUpdateGroupReservationAllowed(groupReservationId, beneficiaryId);

		final Set<Reservation> newReservationSet = new HashSet<Reservation>();
		for (final ReservationVo reservationVo : reservationVoSet) {
			assertNonConflictingArrivalDepature(reservationVo.getArrival(), reservationVo.getDeparture(), groupReservationId);

			final Reservation newReservation = new Reservation(reservationVo.getPrecedence(), reservationVo.getArrival(), reservationVo.getDeparture(), reservationVo.getFirstName(), reservationVo.getLastName());
			newReservationSet.add(newReservation);
		}

		final int guests = reservationVoSet.size();
		final DateTime booked = new DateTime();
		final User booker = userDao.retrieveCurrentUser();
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);
		groupReservation.setBooked(booked);
		groupReservation.setComment(comment);
		groupReservation.associateBooker(booker);
		groupReservation.associateBeneficiary(beneficiary);
		groupReservation.associateAccountant(accountant);
		groupReservation.updateRooms(requiredRooms);
		groupReservation.markInvoiceStale();
		groupReservation.markReportsStale();

		final Set<Reservation> oldReservations = groupReservation.getReservations();
		groupReservation.unassociateReservations(oldReservations);
		reservationDao.removeAll(oldReservations);
		groupReservation.associateReservations(newReservationSet);
		groupReservation.autoSetArrivalDeparture();
		groupReservation.autoSetGuests();

		assertConsecutiveArrivalDeparture(groupReservation);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO only admin should be allowed to update group reservation that had already been payed
		// TODO search for reports covering new date range of group reservation and mark them as stale
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
	public long countGroupReservation(final Interval dateInterval) {
		return groupReservationDao.countByClosedDateInterval(dateInterval);
	}

	@Override
	public long countGroupReservation(final long beneficiaryId, final Interval dateInterval) {
		return groupReservationDao.countByClosedDateIntervalAndBeneficiaryId(beneficiaryId, dateInterval);
	}

	@Override
	public long countGroupReservation(final long beneficiaryId) {
		return groupReservationDao.countByBeneficiaryId(beneficiaryId);
	}

	@Override
	public long countGroupReservation() {
		return groupReservationDao.countAll();
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final Interval dateInterval, final Pagination pagination) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findByClosedDateInterval(dateInterval, pagination));
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final long beneficiaryId, final Interval dateInterval, final Pagination pagination) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findByClosedDateIntervalAndBeneficiaryId(beneficiaryId, dateInterval, pagination));
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final long beneficiaryId, final Pagination pagination) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findByBeneficiaryId(beneficiaryId, pagination));
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final Pagination pagination) {
		if (null == pagination) {
			return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findAll());
		}

		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findAll(pagination));
	}

	@Override
	public GroupReservationBean retrieveGroupReservation(final long groupReservationId) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.retrieveByPrimaryKey(groupReservationId));
	}

	protected Set<Room> determineRequiredRooms(final long requiredCapacity) {
		// assumes BR001
		long availableCapacity = 0;
		final Set<Room> requiredRoomList = new HashSet<Room>();

		final List<Room> availableRoomList = roomDao.findByInUse(true);
		for (final Room availableRoom : availableRoomList) {
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
		final Interval openArrivalDepartureInterval = new Interval(arrival, departure);
		final List<GroupReservation> conflictingGroupReservations = groupReservationDao.findByOpenDateInterval(openArrivalDepartureInterval);
		if (!conflictingGroupReservations.isEmpty()) {
			throw new GroupReservationConflictException("unable to create group reservation with arrival [" + arrival + "] and departure [" + departure + "]. it conflicts with [" + conflictingGroupReservations.size() + "] existing group reservations", serviceBeanMapper.map(GroupReservationBean.class, conflictingGroupReservations));
		}
	}

	protected void assertNonConflictingArrivalDepature(final DateMidnight arrival, final DateMidnight departure, final long excludeGroupReservationId) throws GroupReservationConflictException {
		// implements BR001
		final Interval openArrivalDepartureInterval = new Interval(arrival, departure);
		final List<GroupReservation> conflictingGroupReservationList = groupReservationDao.findByOpenDateInterval(openArrivalDepartureInterval);
		final Iterator<GroupReservation> conflictingGroupReservationIterator = conflictingGroupReservationList.iterator();
		while (conflictingGroupReservationIterator.hasNext()) {
			final GroupReservation conflictingGroupReservation = conflictingGroupReservationIterator.next();
			if (excludeGroupReservationId == conflictingGroupReservation.getGroupReservationId()) {
				conflictingGroupReservationIterator.remove();
			}
		}

		if (!conflictingGroupReservationList.isEmpty()) {
			throw new GroupReservationConflictException("unable to create group reservation with arrival [" + arrival + "] and departure [" + departure + "]. it conflicts with [" + conflictingGroupReservationList.size() + "] existing group reservations", serviceBeanMapper.map(GroupReservationBean.class, conflictingGroupReservationList));
		}
	}

	protected void assertCreateGroupReservationAllowed(final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		final User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to create group reservations for other users", currentUser.getUserId(), OperationType.ADD);
		}
	}

	protected void assertUpdateGroupReservationAllowed(final long groupReservationId, final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		final User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to update group reservations of other users", currentUser.getUserId(), groupReservationBeneficiaryId, OperationType.UPDATE);
		}
	}

	protected void assertDeleteGroupReservationAllowed(final long groupReservationId, final long groupReservationBeneficiaryId) throws InsufficientPermissionException {
		final User currentUser = userDao.retrieveCurrentUser();
		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && (groupReservationBeneficiaryId != currentUser.getUserId())) {
			throw new InsufficientPermissionException("non admin users are not allowed to delete group reservations of other users", currentUser.getUserId(), groupReservationBeneficiaryId, OperationType.DELETE);
		}
	}

	protected void assertConsecutiveArrivalDeparture(final GroupReservation groupReservation) throws GroupReservationNonconsecutiveException {
		final DateMidnight groupReservationArrival = groupReservation.getArrival();
		final DateMidnight groupReservationDeparture = groupReservation.getDeparture();

		final Set<Interval> reservationArrivalDepartureIntervalSet = new HashSet<Interval>();
		for (final Reservation reservation : groupReservation.getReservations()) {
			final Interval arrivalDepatureInterval = new Interval(reservation.getArrival(), reservation.getDeparture());
			reservationArrivalDepartureIntervalSet.add(arrivalDepatureInterval);
		}

		DateMidnight currentDay = groupReservationArrival;
		while (currentDay.isBefore(groupReservationDeparture)) {
			if (!matchesWithArrivalDepatureIntervals(currentDay, reservationArrivalDepartureIntervalSet)) {
				throw new GroupReservationNonconsecutiveException("the individual arrival and departure dates do not form a consecutive date range");
			}

			currentDay = currentDay.plusDays(1);
		}
	}

	protected boolean matchesWithArrivalDepatureIntervals(final DateMidnight referenceDay, final Set<Interval> arrivalDepartureIntervalSet) {
		for (final Interval arrivalDepartureInterval : arrivalDepartureIntervalSet) {
			if (arrivalDepartureInterval.contains(referenceDay)) {
				return true;
			}
		}

		return false;
	}
}
