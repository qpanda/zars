package net.soomsam.zirmegghuette.zars.persistence;

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.apache.log4j.Logger;
import org.hibernate.validator.InvalidStateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 1319361
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class PersistenceEntityTest {
	private final static Logger logger = Logger.getLogger(PersistenceEntityTest.class);

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoomDao roomDao;

	@Autowired
	private GroupReservationDao groupReservationDao;

	@Test
	public void testCreateUserRole() {
		final Role userRole = createUserRole();
		roleDao.flush();
		roleDao.clear();
		logger.debug("persisted role 'user' as [" + userRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(userRole.getRoleId()));
	}

	@Test
	public void testCreateAdminRole() {
		final Role adminRole = createAdminRole();
		roleDao.flush();
		roleDao.clear();
		logger.debug("persisted role 'admin' as [" + adminRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(adminRole.getRoleId()));
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateRoleWithoutName() {
		final Role roleWithoutName = new Role(null);
		roleDao.persist(roleWithoutName);
		roleDao.flush();
	}

	@Test
	public void testCreateTestUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest(userRole, adminRole);
		userDao.persist(testUser);
		userDao.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		userDao.clear();
		final User fetchedTestUser = userDao.findByPrimaryKey(testUser.getUserId());
		Assert.assertNotNull(fetchedTestUser);
		Assert.assertNotNull(fetchedTestUser.getRoles());
		Assert.assertEquals(2, fetchedTestUser.getRoles().size());
		Assert.assertTrue(fetchedTestUser.getRoles().contains(userRole));
		Assert.assertTrue(fetchedTestUser.getRoles().contains(adminRole));
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateUserWithoutNameAndPassword() {
		final User userWithoutNameAndPassword = new User(null, null, true, createUserRole());
		userDao.persist(userWithoutNameAndPassword);
		userDao.flush();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserWithoutRole() {
		final User userWithoutRole = new User("test", "test", true, (Role) null);
		userDao.persist(userWithoutRole);
		userDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateUserWithoutRoles() {
		final User userWithoutRoles = new User("test", "test", true, (Set<Role>) null);
		userDao.persist(userWithoutRoles);
		userDao.flush();
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateUserWithTransientRole() {
		final Role transientRole = new Role("transient");
		final User userWithoutRoles = new User("test", "test", true, transientRole);
		userDao.persist(userWithoutRoles);
		userDao.flush();
	}

	@Test
	public void testDeleteUserWithRole() {
		final Role userRole = createUserRole();
		final User testUser = PersistenceEntityGenerator.createUserTest(userRole);
		userDao.persist(testUser);
		userDao.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		userDao.remove(testUser);
		userDao.flush();
		userDao.clear();

		Assert.assertNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roleDao.findByPrimaryKey(userRole.getRoleId()));
	}

	@Test
	public void testCreateFirstRoom() {
		final Room firstRoom = createFirstRoom();
		roomDao.flush();
		roomDao.clear();
		logger.debug("persisted room 'first' as [" + firstRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testCreateSecondRoom() {
		final Room secondRoom = createSecondRoom();
		roomDao.flush();
		roomDao.clear();
		logger.debug("persisted room 'second' as [" + secondRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(secondRoom.getRoomId()));
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateRoomWithoutName() {
		final Room roomWithoutName = new Room(null, 1, 1);
		roomDao.persist(roomWithoutName);
		roomDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateRoomWithInvalidCapacity() {
		final Room roomWithInvalidCapacity = new Room("invalid", -5, 1);
		roomDao.persist(roomWithInvalidCapacity);
		roomDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateRoomWithInvalidPrecedence() {
		final Room roomWithInvalidPrecedence = new Room("invalid", 1, 0);
		roomDao.persist(roomWithInvalidPrecedence);
		roomDao.flush();
	}

	@Test
	public void testCreateGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new Date(), new Date(), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.addRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		groupReservationDao.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		groupReservationDao.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getUser());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(fetchedGroupReservation.getRooms().contains(firstRoom));
		Assert.assertNotNull(fetchedGroupReservation.getReservations());
		Assert.assertEquals(1, fetchedGroupReservation.getReservations().size());
		Assert.assertTrue(fetchedGroupReservation.getReservations().contains(testReservation));
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateGroupReservationWithoutReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final GroupReservation testGroupReservation = new GroupReservation(testUser);
		testGroupReservation.addRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		groupReservationDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateGroupReservationWithoutRoom() {
		final User testUser = createTestUser();
		final Reservation testReservation = new Reservation(new Date(), new Date(), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser);
		testGroupReservation.associateReservation(testReservation);
		groupReservationDao.persist(testGroupReservation);
		groupReservationDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateGroupReservationWithoutUser() {
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new Date(), new Date(), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(null);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.addRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		groupReservationDao.flush();
	}

	@Test(expected = InvalidStateException.class)
	public void testCreateInvoice() {
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new Date(), new Date(), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(null);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.addRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		groupReservationDao.flush();
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

	private Room createSecondRoom() {
		final Room secondRoom = PersistenceEntityGenerator.createSecondRoom();
		roomDao.persist(secondRoom);
		return secondRoom;
	}

	private User createTestUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest(userRole, adminRole);
		userDao.persist(testUser);
		return testUser;
	}

	private GroupReservation createTestGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new Date(), new Date(), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.addRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		return testGroupReservation;
	}
}
