package net.soomsam.zirmegghuette.zars.persistence;

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
import net.soomsam.zirmegghuette.zars.utils.Pagination;

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
		final Room testRoom = createTestRoom();
		final GroupReservation groupReservation01 = createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(6), new DateMidnight().minusDays(3));
		final GroupReservation groupReservation02 = createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight());
		final GroupReservation groupReservation03 = createGroupReservation(testUser, testRoom, new DateMidnight(), new DateMidnight().plusDays(3));

		final List<GroupReservation> groupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertNotNull(groupReservationList);
		Assert.assertFalse(groupReservationList.contains(groupReservation01));
		Assert.assertTrue(groupReservationList.contains(groupReservation02));
		Assert.assertTrue(groupReservationList.contains(groupReservation03));
	}

	@Test
	public void testFindGroupReservationByClosedDateInterval() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight().plusDays(3));

		Assert.assertTrue(groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(5), new DateMidnight().minusDays(4)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(4), new DateMidnight().minusDays(3)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(3), new DateMidnight().minusDays(2)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(1), new DateMidnight().plusDays(1)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().plusDays(2), new DateMidnight().plusDays(3)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().plusDays(3), new DateMidnight().plusDays(4)), null).isEmpty());
		Assert.assertTrue(groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().plusDays(4), new DateMidnight().plusDays(5)), null).isEmpty());
	}

	@Test
	public void testFindGroupReservationWithPagination() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		final GroupReservation groupReservation01 = createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight());
		final GroupReservation groupReservation02 = createGroupReservation(testUser, testRoom, new DateMidnight(), new DateMidnight().plusDays(3));

		final List<GroupReservation> allGroupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertEquals(2, allGroupReservationList.size());

		final List<GroupReservation> firstPageGroupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), new Pagination(0, 1));
		Assert.assertEquals(1, firstPageGroupReservationList.size());

		final List<GroupReservation> secondPageGroupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), new Pagination(1, 1));
		Assert.assertEquals(1, secondPageGroupReservationList.size());
	}

	@Test
	public void testCompareCountGroupReservationWithFindGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		final GroupReservation groupReservation01 = createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight());
		final GroupReservation groupReservation02 = createGroupReservation(testUser, testRoom, new DateMidnight(), new DateMidnight().plusDays(3));

		Interval closedArrivalDepartureDateInterval = new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1));
		final List<GroupReservation> allGroupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(closedArrivalDepartureDateInterval, null);
		long allGroupReservationCount = groupReservationDao.countGroupReservationByClosedDateInterval(closedArrivalDepartureDateInterval);
		Assert.assertEquals(allGroupReservationCount, allGroupReservationList.size());
	}

	@Test
	public void testCountGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		final GroupReservation groupReservation01 = createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight());
		final GroupReservation groupReservation02 = createGroupReservation(testUser, testRoom, new DateMidnight(), new DateMidnight().plusDays(3));

		Interval closedArrivalDepartureDateInterval = new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1));
		Assert.assertEquals(2, groupReservationDao.countGroupReservationByClosedDateInterval(closedArrivalDepartureDateInterval));

		Assert.assertEquals(2, groupReservationDao.countGroupReservationByClosedDateIntervalAndBeneficiaryId(testUser.getUserId(), closedArrivalDepartureDateInterval));
		Assert.assertEquals(0, groupReservationDao.countGroupReservationByClosedDateIntervalAndBeneficiaryId(3, closedArrivalDepartureDateInterval));

		Assert.assertEquals(2, groupReservationDao.countGroupReservationByBeneficiaryId(testUser.getUserId()));
		Assert.assertEquals(0, groupReservationDao.countGroupReservationByBeneficiaryId(3));

		Assert.assertEquals(2, groupReservationDao.countAll());
	}

	@Test
	public void testFindGroupReservationByOpenDateInterval() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateMidnight().minusDays(3), new DateMidnight().plusDays(3));

		Assert.assertTrue(groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().minusDays(5), new DateMidnight().minusDays(4))).isEmpty());
		Assert.assertTrue(groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().minusDays(4), new DateMidnight().minusDays(3))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().minusDays(3), new DateMidnight().minusDays(2))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().minusDays(1), new DateMidnight().plusDays(1))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().plusDays(2), new DateMidnight().plusDays(3))).isEmpty());
		Assert.assertTrue(groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().plusDays(3), new DateMidnight().plusDays(4))).isEmpty());
		Assert.assertTrue(groupReservationDao.findGroupReservationByOpenDateInterval(new Interval(new DateMidnight().plusDays(4), new DateMidnight().plusDays(5))).isEmpty());
	}

	@Test
	public void testFindNoGroupReservation() {
		final List<GroupReservation> groupReservationList = groupReservationDao.findGroupReservationByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertNotNull(groupReservationList);
		Assert.assertTrue(groupReservationList.isEmpty());
	}

	@Test
	public void testFindRoomByPrecedence() {
		List<Room> roomNotInUseList = roomDao.findByPrecedence(false);
		Assert.assertTrue(roomNotInUseList.isEmpty());

		List<Room> roomInUseList = roomDao.findByPrecedence(true);
		Assert.assertEquals(2, roomInUseList.size());
		Assert.assertEquals(1, roomInUseList.get(0).getPrecedence());
		Assert.assertEquals(2, roomInUseList.get(1).getPrecedence());
	}

	private GroupReservation createGroupReservation(final User user, final Room room, final DateMidnight arrival, final DateMidnight departure) {
		final Reservation testReservation = new Reservation(1, arrival, departure, "a", "b");
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

	private Room createTestRoom() {
		final Room testRoom = PersistenceEntityGenerator.createTestRoom();
		roomDao.persist(testRoom);
		return testRoom;
	}
}
