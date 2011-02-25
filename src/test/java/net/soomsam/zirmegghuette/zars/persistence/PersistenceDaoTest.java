package net.soomsam.zirmegghuette.zars.persistence;

import java.util.List;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.enums.CategoryType;
import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EventDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Event;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class PersistenceDaoTest {
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoomDao roomDao;

	@Autowired
	private GroupReservationDao groupReservationDao;

	@Autowired
	private EventDao eventDao;

	@Test
	public void testFindGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		final GroupReservation groupReservation01 = createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(6), new DateMidnight().minusDays(3));
		final GroupReservation groupReservation02 = createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight());
		final GroupReservation groupReservation03 = createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight(), new DateMidnight().plusDays(3));

		final List<GroupReservation> groupReservationList = groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertNotNull(groupReservationList);
		Assert.assertFalse(groupReservationList.contains(groupReservation01));
		Assert.assertTrue(groupReservationList.contains(groupReservation02));
		Assert.assertTrue(groupReservationList.contains(groupReservation03));
	}

	@Test
	public void testFindGroupReservationByClosedDateInterval() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight().plusDays(3));

		Assert.assertTrue(groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(5), new DateMidnight().minusDays(4)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(4), new DateMidnight().minusDays(3)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(3), new DateMidnight().minusDays(2)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(1), new DateMidnight().plusDays(1)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().plusDays(2), new DateMidnight().plusDays(3)), null).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().plusDays(3), new DateMidnight().plusDays(4)), null).isEmpty());
		Assert.assertTrue(groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().plusDays(4), new DateMidnight().plusDays(5)), null).isEmpty());
	}

	@Test
	public void testFindGroupReservationWithPagination() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight());
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight(), new DateMidnight().plusDays(3));

		final List<GroupReservation> allGroupReservationList = groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertEquals(2, allGroupReservationList.size());

		final List<GroupReservation> firstPageGroupReservationList = groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), new Pagination(0, 1));
		Assert.assertEquals(1, firstPageGroupReservationList.size());

		final List<GroupReservation> secondPageGroupReservationList = groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), new Pagination(1, 1));
		Assert.assertEquals(1, secondPageGroupReservationList.size());
	}

	@Test
	public void testCompareCountGroupReservationWithFindGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight());
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight(), new DateMidnight().plusDays(3));

		final Interval closedArrivalDepartureDateInterval = new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1));
		final List<GroupReservation> allGroupReservationList = groupReservationDao.findByClosedDateInterval(closedArrivalDepartureDateInterval, null);
		final long allGroupReservationCount = groupReservationDao.countByClosedDateInterval(closedArrivalDepartureDateInterval);
		Assert.assertEquals(allGroupReservationCount, allGroupReservationList.size());
	}

	@Test
	public void testAllCountGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight());
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight(), new DateMidnight().plusDays(3));

		final Interval closedArrivalDepartureDateInterval = new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1));
		Assert.assertEquals(2, groupReservationDao.countByClosedDateInterval(closedArrivalDepartureDateInterval));

		Assert.assertEquals(2, groupReservationDao.countByClosedDateIntervalAndBeneficiaryId(testUser.getUserId(), closedArrivalDepartureDateInterval));
		Assert.assertEquals(0, groupReservationDao.countByClosedDateIntervalAndBeneficiaryId(3, closedArrivalDepartureDateInterval));

		Assert.assertEquals(2, groupReservationDao.countByBeneficiaryId(testUser.getUserId()));
		Assert.assertEquals(0, groupReservationDao.countByBeneficiaryId(3));

		Assert.assertEquals(2, groupReservationDao.countAll());
	}

	@Test
	public void testAllFindGroupReservation() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight());
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight(), new DateMidnight().plusDays(3));

		final Interval closedArrivalDepartureDateInterval = new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1));
		Assert.assertEquals(2, groupReservationDao.findByClosedDateInterval(closedArrivalDepartureDateInterval, null).size());

		Assert.assertEquals(2, groupReservationDao.findByClosedDateIntervalAndBeneficiaryId(testUser.getUserId(), closedArrivalDepartureDateInterval, null).size());
		Assert.assertEquals(0, groupReservationDao.findByClosedDateIntervalAndBeneficiaryId(3, closedArrivalDepartureDateInterval, null).size());

		Assert.assertEquals(2, groupReservationDao.findByBeneficiaryId(testUser.getUserId(), null).size());
		Assert.assertEquals(0, groupReservationDao.findByBeneficiaryId(3, null).size());

		Assert.assertEquals(2, groupReservationDao.findAll().size());
	}

	@Test
	public void testFindGroupReservationByOpenDateInterval() {
		final User testUser = createTestUser();
		final Room testRoom = createTestRoom();
		createGroupReservation(testUser, testRoom, new DateTime(), new DateMidnight().minusDays(3), new DateMidnight().plusDays(3));

		Assert.assertTrue(groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(5), new DateMidnight().minusDays(4))).isEmpty());
		Assert.assertTrue(groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(4), new DateMidnight().minusDays(3))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(3), new DateMidnight().minusDays(2))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(1), new DateMidnight().plusDays(1))).isEmpty());
		Assert.assertTrue(!groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().plusDays(2), new DateMidnight().plusDays(3))).isEmpty());
		Assert.assertTrue(groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().plusDays(3), new DateMidnight().plusDays(4))).isEmpty());
		Assert.assertTrue(groupReservationDao.findByOpenDateInterval(new Interval(new DateMidnight().plusDays(4), new DateMidnight().plusDays(5))).isEmpty());
	}

	@Test
	public void testFindNoGroupReservation() {
		final List<GroupReservation> groupReservationList = groupReservationDao.findByClosedDateInterval(new Interval(new DateMidnight().minusDays(2), new DateMidnight().plusDays(1)), null);
		Assert.assertNotNull(groupReservationList);
		Assert.assertTrue(groupReservationList.isEmpty());
	}

	@Test
	public void testFindRoomByPrecedence() {
		final List<Room> roomNotInUseList = roomDao.findByInUse(false);
		Assert.assertTrue(roomNotInUseList.isEmpty());

		final List<Room> roomInUseList = roomDao.findByInUse(true);
		Assert.assertEquals(2, roomInUseList.size());
		Assert.assertEquals(1, roomInUseList.get(0).getPrecedence());
		Assert.assertEquals(2, roomInUseList.get(1).getPrecedence());
	}

	@Test
	public void testCreateSessionEvent() {
		final User testUser = createTestUser();
		final Event sessionEvent = eventDao.create(testUser, CategoryType.CATEGORY_SESSION, "S0001");

		Assert.assertNotNull(sessionEvent);
		Assert.assertEquals("S0001", sessionEvent.getMessage());
		Assert.assertEquals(CategoryType.CATEGORY_SESSION.getCategoryName(), sessionEvent.getCategory());
		Assert.assertTrue(testUser.sameValues(sessionEvent.getUser()));
	}

	@Test
	public void testCreatePersistenceEvent() {
		final User testUser = createTestUser();
		final Event persistenceEvent = eventDao.create(testUser, testUser.getUserId(), User.class, OperationType.OPERATION_ADD, "P0001");

		Assert.assertNotNull(persistenceEvent);
		Assert.assertEquals("P0001", persistenceEvent.getMessage());
		Assert.assertEquals(CategoryType.CATEGORY_PERSISTENCE.getCategoryName(), persistenceEvent.getCategory());
		Assert.assertEquals(testUser.getUserId(), persistenceEvent.getEntityId());
		Assert.assertEquals(OperationType.OPERATION_ADD.getOperationName(), persistenceEvent.getEntityOperation());
		Assert.assertTrue(testUser.sameValues(persistenceEvent.getUser()));
	}

	@Test
	public void testFindLatestEvents() {
		final User testUser = createTestUser();
		final Event sessionEvent = eventDao.create(testUser, CategoryType.CATEGORY_SESSION, "S0001");
		eventDao.persist(sessionEvent);

		Assert.assertFalse(eventDao.findLatest(null).isEmpty());
	}

	@Test
	public void testFindEventByOpenDateInterval() {
		final User testUser = createTestUser();
		final Event sessionEvent = eventDao.create(testUser, CategoryType.CATEGORY_SESSION, "S0001");
		eventDao.persist(sessionEvent);

		Assert.assertFalse(eventDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(1), new DateMidnight().plusDays(1)), null).isEmpty());
		Assert.assertTrue(eventDao.findByOpenDateInterval(new Interval(new DateMidnight().minusDays(5), new DateMidnight().minusDays(3)), null).isEmpty());
	}

	@Test
	public void testFindEventByUserId() {
		final User testUser = createTestUser();
		final Event sessionEvent = eventDao.create(testUser, CategoryType.CATEGORY_SESSION, "S0001");
		eventDao.persist(sessionEvent);

		Assert.assertFalse(eventDao.findByUserId(testUser.getUserId(), null).isEmpty());
	}

	private GroupReservation createGroupReservation(final User user, final Room room, final DateTime booked, final DateMidnight arrival, final DateMidnight departure) {
		final Reservation testReservation = new Reservation(1, arrival, departure, "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(user, user, user, booked, arrival, departure, 1);
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
