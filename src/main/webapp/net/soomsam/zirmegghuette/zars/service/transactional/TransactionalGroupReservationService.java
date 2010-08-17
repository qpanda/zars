package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
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
	public GroupReservationBean createGroupReservation(long beneficiaryId, long accountantId, DateMidnight arrival, DateMidnight departure, long guests, String comment) {
		final User beneficiary = userDao.retrieveByPrimaryKey(beneficiaryId);
		final User accountant = userDao.retrieveByPrimaryKey(accountantId);
		final GroupReservation groupReservation = new GroupReservation(beneficiary, accountant, arrival, departure, guests, comment);
		groupReservation.associateRooms(new HashSet<Room>(roomDao.findAll())); // TODO fix this		
		groupReservationDao.persist(groupReservation);
		return serviceBeanMapper.map(GroupReservationBean.class, groupReservation);
	}

	@Override
	public List<GroupReservation> findGroupReservation(Interval dateInterval) {
		return groupReservationDao.findGroupReservation(dateInterval);
	}
}
