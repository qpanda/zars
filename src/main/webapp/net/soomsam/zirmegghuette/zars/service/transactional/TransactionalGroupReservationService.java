package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.dto.ReservationDto;
import net.soomsam.zirmegghuette.zars.service.utils.ServiceBeanMapper;

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

		// implements BR001
		Interval openArrivalDepartureInterval = new Interval(arrival, departure);
		List<GroupReservation> conflictingGroupReservations = groupReservationDao.findGroupReservationByOpenDateInterval(openArrivalDepartureInterval);
		if (!conflictingGroupReservations.isEmpty()) {
			throw new GroupReservationConflictException("unable to create group reservation for user [" + beneficiaryId + "], arrival [" + arrival + "], departure [" + departure + "], with [" + guests + "] guests. it conflicts with [" + conflictingGroupReservations.size() + "] existing group reservations", serviceBeanMapper.map(GroupReservationBean.class, conflictingGroupReservations));
		}

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
	public GroupReservationBean createGroupReservation(final long beneficiaryId, final long accountantId, final Set<ReservationDto> reservationDtoSet, final String comment) throws GroupReservationConflictException {
		return null;
	}

	@Override
	public List<GroupReservationBean> findGroupReservation(final Interval dateInterval) {
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservationDao.findGroupReservationByClosedDateInterval(dateInterval));
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
}
