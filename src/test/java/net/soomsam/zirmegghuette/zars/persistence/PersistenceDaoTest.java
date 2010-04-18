package net.soomsam.zirmegghuette.zars.persistence;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.PersistenceContextManager;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class PersistenceDaoTest {
	private final static Logger logger = Logger.getLogger(PersistenceDaoTest.class);

	@Autowired
	private PersistenceContextManager persistenceContextManager;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoomDao roomDao;

	@Autowired
	private GroupReservationDao groupReservationDao;

	@Test
	public void testFindGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		GroupReservation groupReservation01 = createGroupReservation(testUser, firstRoom, new DateMidnight().minusDays(6).toDate(), new DateMidnight().minusDays(3).toDate());
		GroupReservation groupReservation02 = createGroupReservation(testUser, firstRoom, new DateMidnight().minusDays(3).toDate(), new DateMidnight().toDate());
		GroupReservation groupReservation03 = createGroupReservation(testUser, firstRoom, new DateMidnight().toDate(), new DateMidnight().plusDays(3).toDate());

		List<GroupReservation> groupReservationList = groupReservationDao.findGroupReservation(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)));
		Assert.assertNotNull(groupReservationList);
		Assert.assertFalse(groupReservationList.contains(groupReservation01));
		Assert.assertTrue(groupReservationList.contains(groupReservation02));
		Assert.assertTrue(groupReservationList.contains(groupReservation03));
	}

	private GroupReservation createGroupReservation(User user, Room room, Date arrival, Date departure) {
		final Reservation testReservation = new Reservation(arrival, departure, "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(user, user, arrival, departure, 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(room);
		groupReservationDao.persist(testGroupReservation);
		return testGroupReservation;
	}

	private User createTestUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		return testUser;
	}

	private Role createUserRole() {
		final Role userRole = PersistenceEntityGenerator.createUserRole();
		roleDao.persist(userRole);
		return userRole;
	}

	private Role createAdminRole() {
		final Role adminRole = PersistenceEntityGenerator.createAdminRole();
		roleDao.persist(adminRole);
		return adminRole;
	}

	private Room createFirstRoom() {
		final Room firstRoom = PersistenceEntityGenerator.createFirstRoom();
		roomDao.persist(firstRoom);
		return firstRoom;
	}
}
