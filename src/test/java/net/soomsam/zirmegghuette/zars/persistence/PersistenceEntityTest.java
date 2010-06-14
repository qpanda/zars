package net.soomsam.zirmegghuette.zars.persistence;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.InvoiceDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.dao.PersistenceContextManager;
import net.soomsam.zirmegghuette.zars.persistence.dao.ReportDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.ReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.SettingDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Invoice;
import net.soomsam.zirmegghuette.zars.persistence.entity.Report;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
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
public class PersistenceEntityTest {
	private final static Logger logger = Logger.getLogger(PersistenceEntityTest.class);

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

	@Autowired
	private ReservationDao reservationDao;

	@Autowired
	private InvoiceDao invoiceDao;

	@Autowired
	private ReportDao reportDao;

	@Autowired
	private SettingDao settingDao;

	@Test
	public void testCreateUserRole() {
		final Role userRole = createUserRole();
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted role 'user' as [" + userRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(userRole.getRoleId()));
	}

	@Test
	public void testCreateAdminRole() {
		final Role adminRole = createAdminRole();
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted role 'admin' as [" + adminRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(adminRole.getRoleId()));
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateRoleWithoutName() {
		final Role roleWithoutName = new Role(null);
		roleDao.persist(roleWithoutName);
		persistenceContextManager.flush();
	}

	@Test
	public void testDeleteRoleWithoutUsers() {
		final Role userRole = createUserRole();
		persistenceContextManager.flush();
		logger.debug("persisted role 'user' as [" + userRole + "]");

		roleDao.remove(userRole);
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		Assert.assertNull(roleDao.findByPrimaryKey(userRole.getRoleId()));
	}

	@Test
	public void testDeleteRoleWithUsers() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final Set<Role> testUserRoles = new HashSet<Role>();
		testUserRoles.add(userRole);
		testUserRoles.add(adminRole);
		final User testUser01 = new User("test01", String.valueOf(new Random().nextLong()), "test01@test.com", true, testUserRoles);
		final User testUser02 = new User("test02", String.valueOf(new Random().nextLong()), "test02@test.com", true, testUserRoles);
		userDao.persist(testUser01);
		userDao.persist(testUser02);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser01 + "]");
		logger.debug("persisted user 'test' as [" + testUser02 + "]");

		Assert.assertNotNull(userDao.findByPrimaryKey(testUser01.getUserId()));
		Assert.assertEquals(2, userDao.findByPrimaryKey(testUser01.getUserId()).getRoles().size());
		Assert.assertTrue(userDao.findByPrimaryKey(testUser01.getUserId()).getRoles().contains(userRole));
		Assert.assertTrue(userDao.findByPrimaryKey(testUser01.getUserId()).getRoles().contains(adminRole));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser02.getUserId()));
		Assert.assertEquals(2, userDao.findByPrimaryKey(testUser02.getUserId()).getRoles().size());
		Assert.assertTrue(userDao.findByPrimaryKey(testUser02.getUserId()).getRoles().contains(userRole));
		Assert.assertTrue(userDao.findByPrimaryKey(testUser02.getUserId()).getRoles().contains(adminRole));

		testUser01.unassociateRole(userRole);
		testUser02.unassociateRole(userRole);
		roleDao.remove(userRole);
		persistenceContextManager.flush();

		persistenceContextManager.clear();
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser01.getUserId()));
		Assert.assertEquals(1, userDao.findByPrimaryKey(testUser01.getUserId()).getRoles().size());
		Assert.assertTrue(containsEntity(userDao.findByPrimaryKey(testUser01.getUserId()).getRoles(), adminRole));
		Assert.assertFalse(userDao.findByPrimaryKey(testUser01.getUserId()).getRoles().contains(userRole));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser02.getUserId()));
		Assert.assertEquals(1, userDao.findByPrimaryKey(testUser02.getUserId()).getRoles().size());
		Assert.assertTrue(containsEntity(userDao.findByPrimaryKey(testUser02.getUserId()).getRoles(), adminRole));
		Assert.assertFalse(userDao.findByPrimaryKey(testUser02.getUserId()).getRoles().contains(userRole));
	}

	@Test
	public void testCreateTestUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		persistenceContextManager.clear();
		final User fetchedTestUser = userDao.findByPrimaryKey(testUser.getUserId());
		Assert.assertNotNull(fetchedTestUser);
		Assert.assertNotNull(fetchedTestUser.getRoles());
		Assert.assertEquals(2, fetchedTestUser.getRoles().size());
		Assert.assertTrue(containsEntity(fetchedTestUser.getRoles(), userRole));
		Assert.assertTrue(containsEntity(fetchedTestUser.getRoles(), adminRole));
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateUserWithoutNameAndPassword() {
		final User userWithoutNameAndPassword = new User(null, null, "test@test.com", true, createUserRole());
		userDao.persist(userWithoutNameAndPassword);
		persistenceContextManager.flush();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserWithoutRole() {
		final User userWithoutRole = new User("test", "test", "test@test.com", true, (Role) null);
		userDao.persist(userWithoutRole);
		persistenceContextManager.flush();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserWithNullRoles() {
		final User userWithoutRoles = new User("test", "test", "test@test.com", true, (Set<Role>) null);
		userDao.persist(userWithoutRoles);
		persistenceContextManager.flush();
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateUserWithoutRoles() {
		final User userWithoutRoles = new User("test", "test", "test@test.com", true, new HashSet<Role>());
		userDao.persist(userWithoutRoles);
		persistenceContextManager.flush();
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateUserWithTransientRole() {
		final Role transientRole = new Role("transient");
		final User userWithoutRoles = new User("test", "test", "test@test.com", true, transientRole);
		userDao.persist(userWithoutRoles);
		persistenceContextManager.flush();
	}

	@Test
	public void testCreateUserWithTransientGroupReservation() {
		final Role userRole = createUserRole();
		final User testUser = new User("test", "test", "test@test.com", true, userRole);
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		final User fetchedTestUser = userDao.findByPrimaryKey(testUser.getUserId());
		Assert.assertNotNull(fetchedTestUser);
		Assert.assertTrue(fetchedTestUser.getBeneficiaryGroupReservations().isEmpty());
	}

	@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
	public void testCreateUsersWithNonUniqueNames() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser01 = PersistenceEntityGenerator.createUserTest("test", "test01@test.com", userRole, adminRole);
		userDao.persist(testUser01);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser01 + "]");

		final User testUser02 = PersistenceEntityGenerator.createUserTest("test", "test02@test.com", userRole, adminRole);
		try {
			userDao.persist(testUser02);
			persistenceContextManager.flush();
		} catch (final PersistenceException persistenceException) {
			final Throwable persistenceExceptionCause = persistenceException.getCause();
			Assert.assertTrue(persistenceExceptionCause instanceof org.hibernate.exception.ConstraintViolationException);
			final org.hibernate.exception.ConstraintViolationException constraintViolationException = (org.hibernate.exception.ConstraintViolationException) persistenceExceptionCause;
			Assert.assertTrue(StringUtils.equalsIgnoreCase(User.COLUMNNAME_USERNAME, constraintViolationException.getConstraintName()));
			throw constraintViolationException;
		}
	}

	@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
	public void testCreateUsersWithNonUniqueEmailAddress() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser01 = PersistenceEntityGenerator.createUserTest("test01", "test@test.com", userRole, adminRole);
		userDao.persist(testUser01);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser01 + "]");

		final User testUser02 = PersistenceEntityGenerator.createUserTest("test02", "test@test.com", userRole, adminRole);
		try {
			userDao.persist(testUser02);
			persistenceContextManager.flush();
		} catch (final PersistenceException persistenceException) {
			final Throwable persistenceExceptionCause = persistenceException.getCause();
			Assert.assertTrue(persistenceExceptionCause instanceof org.hibernate.exception.ConstraintViolationException);
			final org.hibernate.exception.ConstraintViolationException constraintViolationException = (org.hibernate.exception.ConstraintViolationException) persistenceExceptionCause;
			Assert.assertTrue(StringUtils.equalsIgnoreCase(User.COLUMNNAME_EMAILADDRESS, constraintViolationException.getConstraintName()));
			throw constraintViolationException;
		}
	}

	@Test
	public void testDisableUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		testUser.setEnabled(false);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		logger.debug("updated user 'test' as [" + testUser + "]");

		persistenceContextManager.clear();
		final User fetchedTestUser = userDao.findByPrimaryKey(testUser.getUserId());
		Assert.assertNotNull(fetchedTestUser);
		Assert.assertFalse(fetchedTestUser.isEnabled());
	}

	@Test(expected = OperationNotSupportedException.class)
	public void testDeleteUser() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		userDao.remove(testUser);
	}

	@Test
	public void testCreateFirstRoom() {
		final Room firstRoom = createFirstRoom();
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted room 'first' as [" + firstRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testCreateSecondRoom() {
		final Room secondRoom = createSecondRoom();
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted room 'second' as [" + secondRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(secondRoom.getRoomId()));
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateRoomWithoutName() {
		final Room roomWithoutName = new Room(null, 1, 1, true);
		roomDao.persist(roomWithoutName);
		persistenceContextManager.flush();
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateRoomWithInvalidCapacity() {
		final Room roomWithInvalidCapacity = new Room("invalid", -5, 1, true);
		roomDao.persist(roomWithInvalidCapacity);
		persistenceContextManager.flush();
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateRoomWithInvalidPrecedence() {
		final Room roomWithInvalidPrecedence = new Room("invalid", 1, 0, true);
		roomDao.persist(roomWithInvalidPrecedence);
		persistenceContextManager.flush();
	}

	@Test
	public void testDisableFirstRoom() {
		final Room firstRoom = createFirstRoom();
		persistenceContextManager.flush();
		logger.debug("persisted room 'first' as [" + firstRoom + "]");

		firstRoom.setInUse(false);
		roomDao.persist(firstRoom);
		persistenceContextManager.flush();
		logger.debug("updated room 'first' as [" + firstRoom + "]");

		persistenceContextManager.clear();
		final Room fetchedFirstRoom = roomDao.findByPrimaryKey(firstRoom.getRoomId());
		Assert.assertNotNull(fetchedFirstRoom);
		Assert.assertFalse(fetchedFirstRoom.isInUse());
	}

	@Test(expected = OperationNotSupportedException.class)
	public void testDeleteFirstRoom() {
		final Room firstRoom = createFirstRoom();
		persistenceContextManager.flush();
		logger.debug("persisted room 'first' as [" + firstRoom + "]");

		roomDao.remove(firstRoom);
	}

	@Test
	public void testCreateGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getRooms(), firstRoom));
		Assert.assertTrue(fetchedGroupReservation.hasReservations());
		Assert.assertEquals(1, fetchedGroupReservation.getReservations().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGroupReservationWithoutArrivalDeparture() {
		final User testUser = createTestUser();
		new GroupReservation(testUser, testUser, null, null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGroupReservationWithInvalidArrivalDeparture() {
		final User testUser = createTestUser();
		new GroupReservation(testUser, testUser, new DateMidnight().plusDays(1), new DateMidnight(), 1);
	}

	@Test
	public void testCreateGroupReservationWithDifferentAccountant() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final Role accountantRole = createAccountantRole();
		final User beneficiaryUser = PersistenceEntityGenerator.createUserTest("test", "test01@test.com", userRole, adminRole);
		final User accountantUser = PersistenceEntityGenerator.createUserTest("accountant", "test02@test.com", userRole, adminRole, accountantRole);
		userDao.persist(beneficiaryUser);
		userDao.persist(accountantUser);

		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(beneficiaryUser, accountantUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertTrue(beneficiaryUser.same(fetchedGroupReservation.getBeneficiary()));
		Assert.assertTrue(accountantUser.same(fetchedGroupReservation.getAccountant()));

		Assert.assertTrue(beneficiaryUser.getAccountantGroupReservations().isEmpty());
		Assert.assertFalse(beneficiaryUser.getBeneficiaryGroupReservations().isEmpty());

		Assert.assertTrue(accountantUser.getBeneficiaryGroupReservations().isEmpty());
		Assert.assertFalse(accountantUser.getAccountantGroupReservations().isEmpty());
	}

	@Test
	public void testCreateGroupReservationWithReservations() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation01 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "c", "d");
		final Reservation testReservation03 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "e", "f");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 3);
		testGroupReservation.associateReservation(testReservation01);
		testGroupReservation.associateReservation(testReservation02);
		testGroupReservation.associateReservation(testReservation03);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getRooms(), firstRoom));
		Assert.assertTrue(fetchedGroupReservation.hasReservations());
		Assert.assertEquals(3, fetchedGroupReservation.getReservations().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation01));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation02));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation03));
	}

	@Test
	public void testCreateGroupReservationWithAutoGuests() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation01 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "c", "d");
		final Reservation testReservation03 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "e", "f");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 2);
		testGroupReservation.associateReservation(testReservation01);
		testGroupReservation.associateReservation(testReservation02);
		testGroupReservation.associateReservation(testReservation03);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getRooms(), firstRoom));
		Assert.assertTrue(fetchedGroupReservation.hasReservations());
		Assert.assertEquals(3, fetchedGroupReservation.getReservations().size());
		Assert.assertEquals(3, fetchedGroupReservation.getGuests());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation01));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation02));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation03));
	}

	@Test
	public void testCreateGroupReservationWithAutoArrivalDeparture() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation01 = new Reservation(new DateMidnight().minusDays(1), new DateMidnight(), "a", "b");
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "c", "d");
		final Reservation testReservation03 = new Reservation(new DateMidnight().plusDays(1), new DateMidnight().plusDays(2), "e", "f");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 2);
		testGroupReservation.associateReservation(testReservation01);
		testGroupReservation.associateReservation(testReservation02);
		testGroupReservation.associateReservation(testReservation03);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getRooms(), firstRoom));
		Assert.assertTrue(fetchedGroupReservation.hasReservations());
		Assert.assertEquals(new DateMidnight().minusDays(1), fetchedGroupReservation.getArrival());
		Assert.assertEquals(new DateMidnight().plusDays(2), fetchedGroupReservation.getDeparture());
		Assert.assertTrue(testReservation01.same(fetchedGroupReservation.getEarliestArrivalReservation()));
		Assert.assertTrue(testReservation03.same(fetchedGroupReservation.getLatestDepartureReservation()));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation01));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation02));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation03));
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateGroupReservationWithoutRoom() {
		final User testUser = createTestUser();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGroupReservationWithoutUser() {
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(null, null, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
	}

	@Test
	public void testCreateGroupReservationWithoutReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getGuests());
		Assert.assertFalse(fetchedGroupReservation.hasReservations());
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateReservation() {
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		reservationDao.persist(testReservation);
		persistenceContextManager.flush();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReservationWithoutArrivalDeparture() {
		new Reservation(null, null, "a", "b");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReservationWithInvalidArrivalDeparture() {
		new Reservation(new DateMidnight().plusDays(1), new DateMidnight(), "a", "b");
	}

	@Test
	public void testCreateReservationWithGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 3);
		testGroupReservation.associateRoom(firstRoom);
		final Reservation testReservation01 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b", testGroupReservation);
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "c", "d", testGroupReservation);
		final Reservation testReservation03 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "e", "f", testGroupReservation);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertNotNull(fetchedGroupReservation.getBeneficiary());
		Assert.assertNotNull(fetchedGroupReservation.getAccountant());
		Assert.assertNotNull(fetchedGroupReservation.getRooms());
		Assert.assertEquals(1, fetchedGroupReservation.getRooms().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getRooms(), firstRoom));
		Assert.assertTrue(fetchedGroupReservation.hasReservations());
		Assert.assertEquals(3, fetchedGroupReservation.getReservations().size());
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation01));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation02));
		Assert.assertTrue(containsEntity(fetchedGroupReservation.getReservations(), testReservation03));
	}

	@Test
	public void testDeleteGroupReservationWithoutReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		groupReservationDao.remove(testGroupReservation);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		Assert.assertNull(groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId()));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testDeleteGroupReservationWithReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		groupReservationDao.remove(testGroupReservation);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		Assert.assertNull(groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId()));
		Assert.assertNull(reservationDao.findByPrimaryKey(testReservation.getReservationId()));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testDeleteGroupReservationWithReservations() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation01 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final Reservation testReservation03 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 3);
		testGroupReservation.associateReservation(testReservation01);
		testGroupReservation.associateReservation(testReservation02);
		testGroupReservation.associateReservation(testReservation03);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		persistenceContextManager.flush();
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");

		groupReservationDao.remove(testGroupReservation);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		Assert.assertNull(groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId()));
		Assert.assertNull(reservationDao.findByPrimaryKey(testReservation01.getReservationId()));
		Assert.assertNull(reservationDao.findByPrimaryKey(testReservation02.getReservationId()));
		Assert.assertNull(reservationDao.findByPrimaryKey(testReservation03.getReservationId()));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testDeleteOnlyReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		final Reservation fetchedReservation = reservationDao.findByPrimaryKey(testReservation.getReservationId());
		fetchedGroupReservation.unassociateReservation(fetchedReservation);
		reservationDao.remove(fetchedReservation);
		groupReservationDao.persist(fetchedGroupReservation);
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		final GroupReservation verifyGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(verifyGroupReservation);
		Assert.assertFalse(verifyGroupReservation.hasReservations());
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testDeleteOneReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation01 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final Reservation testReservation02 = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "c", "d");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 2);
		testGroupReservation.associateReservation(testReservation01);
		testGroupReservation.associateReservation(testReservation02);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		logger.debug("persisted groupReservation as [" + testGroupReservation + "]");
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		final Reservation fetchedReservation01 = reservationDao.findByPrimaryKey(testReservation01.getReservationId());
		fetchedGroupReservation.unassociateReservation(fetchedReservation01);
		reservationDao.remove(fetchedReservation01);
		persistenceContextManager.flush();
		persistenceContextManager.clear();

		final GroupReservation verifyGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		final Reservation verifyReservation = reservationDao.findByPrimaryKey(testReservation02.getReservationId());
		Assert.assertNotNull(verifyGroupReservation);
		Assert.assertTrue(verifyGroupReservation.hasReservations());
		Assert.assertEquals(1, verifyGroupReservation.getReservations().size());
		Assert.assertTrue(verifyGroupReservation.getReservations().contains(verifyReservation));
		Assert.assertNotNull(userDao.findByPrimaryKey(testUser.getUserId()));
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}

	@Test
	public void testCreateInvoice() {
		final GroupReservation testGroupReservation = createTestGroupReservation();
		persistenceContextManager.flush();
		final byte[] testInvoiceDocument = TestUtils.readFile("net/soomsam/zirmegghuette/zars/persistence/test.pdf");

		final Invoice testInvoice = new Invoice(new Date(), "EUR", 123.456, true, testInvoiceDocument, testGroupReservation);
		invoiceDao.persist(testInvoice);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted invoice as [" + testInvoice + "]");

		final Invoice fetchedInvoice = invoiceDao.findByPrimaryKey(testInvoice.getInvoiceId());
		Assert.assertNotNull(fetchedInvoice);
		Assert.assertTrue(Arrays.equals(testInvoiceDocument, fetchedInvoice.getDocument()));
		Assert.assertTrue(fetchedInvoice.getGroupReservation().same(testGroupReservation));
	}

	@Test(expected = OperationNotSupportedException.class)
	public void testDeleteInvoice() {
		final GroupReservation testGroupReservation = createTestGroupReservation();
		persistenceContextManager.flush();
		final byte[] testInvoiceDocument = TestUtils.readFile("net/soomsam/zirmegghuette/zars/persistence/test.pdf");

		final Invoice testInvoice = new Invoice(new Date(), "EUR", 123.456, true, testInvoiceDocument, testGroupReservation);
		invoiceDao.persist(testInvoice);
		persistenceContextManager.flush();
		logger.debug("persisted invoice as [" + testInvoice + "]");

		invoiceDao.remove(testInvoice);
	}

	@Test
	public void testCreateReport() {
		final GroupReservation testGroupReservation = createTestGroupReservation();
		persistenceContextManager.flush();
		final byte[] testReportDocument = TestUtils.readFile("net/soomsam/zirmegghuette/zars/persistence/test.pdf");

		final Report testReport = new Report(new Date(), new DateMidnight().plusDays(1), new DateMidnight(), testReportDocument, testGroupReservation);
		reportDao.persist(testReport);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted report as [" + testReport + "]");

		final Report fetchedReport = reportDao.findByPrimaryKey(testReport.getReportId());
		Assert.assertNotNull(fetchedReport);
		Assert.assertTrue(Arrays.equals(testReportDocument, fetchedReport.getDocument()));
		Assert.assertTrue(containsEntity(fetchedReport.getGroupReservations(), testGroupReservation));
	}

	@Test
	public void testDeleteReport() {
		final GroupReservation testGroupReservation = createTestGroupReservation();
		persistenceContextManager.flush();
		final byte[] testReportDocument = TestUtils.readFile("net/soomsam/zirmegghuette/zars/persistence/test.pdf");

		final Report testReport = new Report(new Date(), new DateMidnight().plusDays(1), new DateMidnight(), testReportDocument, testGroupReservation);
		reportDao.persist(testReport);
		persistenceContextManager.flush();
		logger.debug("persisted report as [" + testReport + "]");

		reportDao.remove(testReport);

		persistenceContextManager.flush();
		persistenceContextManager.clear();
		final GroupReservation fetchedGroupReservation = groupReservationDao.findByPrimaryKey(testGroupReservation.getGroupReservationId());
		Assert.assertNotNull(fetchedGroupReservation);
		Assert.assertTrue(fetchedGroupReservation.getReports().isEmpty());
	}

	@Test
	public void testCreateSetting() {
		final Setting testSetting = new Setting("test", "test", "java.lang.String");
		settingDao.persist(testSetting);
		persistenceContextManager.flush();
		persistenceContextManager.clear();
		logger.debug("persisted setting as [" + testSetting + "]");

		final Setting fetchedSetting = settingDao.findByPrimaryKey(testSetting.getSettingId());
		Assert.assertNotNull(fetchedSetting);
	}

	@Test
	public void testDeleteSetting() {
		final Setting testSetting = new Setting("test", "test", "java.lang.String");
		settingDao.persist(testSetting);
		persistenceContextManager.flush();
		logger.debug("persisted setting as [" + testSetting + "]");

		settingDao.remove(testSetting);

		persistenceContextManager.flush();
		persistenceContextManager.clear();
		final Setting fetchedSetting = settingDao.findByPrimaryKey(testSetting.getSettingId());
		Assert.assertNull(fetchedSetting);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDirectlyModifyAssociation() {
		final Role userRole = createUserRole();
		final Role adminRole = createAdminRole();
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		persistenceContextManager.flush();
		logger.debug("persisted user 'test' as [" + testUser + "]");

		testUser.getRoles().remove(userRole);
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

	private Role createAccountantRole() {
		final Role accountantRole = PersistenceEntityGenerator.createAccountantRole();
		roleDao.persist(accountantRole);
		return accountantRole;
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
		final User testUser = PersistenceEntityGenerator.createUserTest("test", "test@test.com", userRole, adminRole);
		userDao.persist(testUser);
		return testUser;
	}

	private GroupReservation createTestGroupReservation() {
		final User testUser = createTestUser();
		final Room firstRoom = createFirstRoom();
		final Reservation testReservation = new Reservation(new DateMidnight(), new DateMidnight().plusDays(1), "a", "b");
		final GroupReservation testGroupReservation = new GroupReservation(testUser, testUser, new DateMidnight(), new DateMidnight().plusDays(1), 1);
		testGroupReservation.associateReservation(testReservation);
		testGroupReservation.associateRoom(firstRoom);
		groupReservationDao.persist(testGroupReservation);
		return testGroupReservation;
	}

	private <Entity extends BaseEntity> boolean containsEntity(final Set<Entity> entitySet, final Entity otherEntity) {
		for (final Entity entity : entitySet) {
			if (entity.same(otherEntity)) {
				return true;
			}
		}

		return false;
	}
}
