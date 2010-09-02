package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
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
	private UserDao userDao;

	@Autowired
	private RoomDao roomDao;

	@Override
	public void createAllRooms() {
		roomDao.persist(new Room("ROOM_PRIMARY", 4, 1, true));
		roomDao.persist(new Room("ROOM_SECONDARY", 4, 2, true));
	}

	@Override
	@Transactional(rollbackFor = GroupReservationConflictException.class)
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final DateMidnight arrival, final DateMidnight departure, final long guests, final String comment) throws GroupReservationConflictException {
		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		assertNonConflictingArrivalDepature(arrival, departure);

		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = new GroupReservation(beneficiary, accountant, arrival, departure, guests, comment);
		groupReservation.associateRooms(requiredRooms);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
	}

	@Override
	@Transactional(rollbackFor = GroupReservationConflictException.class)
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final Set<ReservationVo> reservationVoSet, final String comment) throws GroupReservationConflictException {
		if ((null == reservationVoSet) || (reservationVoSet.isEmpty())) {
			throw new IllegalArgumentException("'reservationVoSet' must not be null or empty");
		}

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
	}

	@Override
	@Transactional(rollbackFor = GroupReservationConflictException.class)
	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) throws GroupReservationConflictException {
		if ((null == arrival) || (null == departure)) {
			throw new IllegalArgumentException("'arrival' and 'departure' must not be null");
		}

		// TODO all but this should not overlap!!!
		// assertNonConflictingArrivalDepature(arrival, departure);

		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final Set<Room> requiredRooms = determineRequiredRooms(guests);
		final GroupReservation groupReservation = groupReservationDao.retrieveByPrimaryKey(groupReservationId);
		groupReservation.setArrival(arrival);
		groupReservation.setDeparture(departure);
		groupReservation.setGuests(guests);
		groupReservation.setComment(comment);
		// TODO verify if the following associate calls are correct or if we need to unassociate all first!
		groupReservation.associateBeneficiary(beneficiary);
		groupReservation.associateAccountant(accountant);
		groupReservation.associateRooms(requiredRooms);
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);

		// TODO required capacity fulfilled???
		// TODO re-associate rooms
	}

	@Override
	@Transactional(rollbackFor = GroupReservationConflictException.class)
	public GroupReservationBean updateGroupReservation(long groupReservationId, long beneficiaryId, long accountantId, Set<ReservationVo> reservationVoSet, String comment) throws GroupReservationConflictException {
		return null;
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final Interval dateInterval) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findGroupReservationByClosedDateInterval(dateInterval));
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
}
