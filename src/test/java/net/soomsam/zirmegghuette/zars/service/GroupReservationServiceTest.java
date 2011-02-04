package net.soomsam.zirmegghuette.zars.service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationNonconsecutiveException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.PersistenceContextManager;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class GroupReservationServiceTest {
	private final static Logger logger = Logger.getLogger(GroupReservationServiceTest.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupReservationService groupReservationService;

	@Autowired
	private RoomDao roomDao;

	@Autowired
	private PersistenceContextManager persistenceContextManager;

	@Before
	public void login() {
		doLogin("admin", "admin");
	}

	@After
	public void logout() {
		doLogout();
	}

	@Test
	public void createGroupReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight arrival = new DateMidnight();
		DateMidnight departure = arrival.plusDays(3);
		long guests = 3;
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), arrival, departure, guests, comment);
		Assert.assertEquals(arrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(guests, createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(createdGroupReservation.getReservations().isEmpty());
	}

	@Test(expected = GroupReservationConflictException.class)
	public void createGroupReservationConflict() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdExistingGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
		GroupReservationBean createdConflictingGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(1), new DateMidnight().plusDays(5), 3, null);
	}

	@Test
	public void createGroupReservationWithoutConflict() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdGroupReservation01 = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
		GroupReservationBean createdGroupReservation02 = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(3), new DateMidnight().plusDays(5), 3, null);
	}

	@Test
	public void verifyGroupReservationRoomSelection() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		List<Room> allRooms = roomDao.findByPrecedence(true);

		int i = 0;
		int totalCapacity = 0;
		Iterator<Room> allRoomIterator = allRooms.iterator();
		while (allRoomIterator.hasNext()) {
			i++;
			Room room = allRoomIterator.next();
			totalCapacity += room.getCapacity();

			GroupReservationBean createdGroupReservationWithLessThanTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusMonths(i).minusDays(4), new DateMidnight().plusMonths(i).minusDays(1), totalCapacity - 1, null);
			Assert.assertEquals(i, createdGroupReservationWithLessThanTotalCapacity.getRooms().size());
			GroupReservationBean createdGroupReservationWithTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusMonths(i).minusDays(1), new DateMidnight().plusMonths(i).plusDays(1), totalCapacity, null);
			Assert.assertEquals(i, createdGroupReservationWithTotalCapacity.getRooms().size());
			GroupReservationBean createdGroupReservationWithMoreThanTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusMonths(i).plusDays(1), new DateMidnight().plusMonths(i).plusDays(4), totalCapacity + 1, null);
			Assert.assertEquals(allRoomIterator.hasNext() ? (i + 1) : i, createdGroupReservationWithMoreThanTotalCapacity.getRooms().size());
		}
	}

	@Test
	public void createGroupReservationWithOneReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight arrival = new DateMidnight();
		DateMidnight departure = arrival.plusDays(3);
		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrival, departure);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
		Assert.assertEquals(arrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(createdGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getReservations().size());
		Assert.assertEquals(arrival.toDate(), createdGroupReservation.getReservations().get(0).getArrival());
		Assert.assertEquals(departure.toDate(), createdGroupReservation.getReservations().get(0).getDeparture());
	}

	@Test
	public void createGroupReservationWithMultipleReservations() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = arrival00.plusDays(1);
		DateMidnight arrival02 = arrival00.plusDays(2);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusMonths(1);
		DateMidnight departure01 = departure00.minusDays(1);
		DateMidnight departure02 = departure00.minusDays(2);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
		Assert.assertEquals(arrival00.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(createdGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getReservations().size());
	}
	
	@Test(expected = GroupReservationNonconsecutiveException.class)
	public void createGroupReservationWithMultipleNonconsecutiveReservations() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = new DateMidnight().plusDays(10);
		DateMidnight arrival02 = new DateMidnight().plusDays(20);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusDays(5);
		DateMidnight departure01 = new DateMidnight().plusDays(15);
		DateMidnight departure02 = new DateMidnight().plusDays(25);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, null);
	}
	
	@Test(expected = GroupReservationNonconsecutiveException.class)
	public void createGroupReservationWithMultipleNonoverlappingReservations() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = new DateMidnight().plusDays(4);
		DateMidnight arrival02 = new DateMidnight().plusDays(7);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusDays(3);
		DateMidnight departure01 = new DateMidnight().plusDays(6);
		DateMidnight departure02 = new DateMidnight().plusDays(9);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
	}

	@Test
	public void updateGroupReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight initialArrival = new DateMidnight();
		DateMidnight initialDeparture = initialArrival.plusDays(3);
		long initialGuests = 3;
		String initialComment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), initialArrival, initialDeparture, initialGuests, initialComment);
		Assert.assertEquals(initialArrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(initialDeparture.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(initialGuests, createdGroupReservation.getGuests());
		Assert.assertEquals(initialComment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(createdGroupReservation.getReservations().isEmpty());

		DateMidnight updatedArrival = new DateMidnight();
		DateMidnight updatedDeparture = initialArrival.plusDays(5);
		long updatedGuests = 5;
		String updatedComment = "updated";
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), updatedArrival, updatedDeparture, updatedGuests, updatedComment);
		Assert.assertEquals(updatedArrival.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(updatedDeparture.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(updatedGuests, updatedGroupReservation.getGuests());
		Assert.assertEquals(updatedComment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(updatedGroupReservation.getReservations().isEmpty());
	}

	@Test
	public void updateGroupReservationAddOneReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight initialArrival = new DateMidnight();
		DateMidnight initialDeparture = initialArrival.plusDays(3);
		long initialGuests = 3;
		String initialComment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), initialArrival, initialDeparture, initialGuests, initialComment);
		Assert.assertEquals(initialArrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(initialDeparture.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(initialGuests, createdGroupReservation.getGuests());
		Assert.assertEquals(initialComment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(createdGroupReservation.getReservations().isEmpty());

		Set<ReservationVo> reservationVoSet = createReservationVoSet(initialArrival, initialDeparture);
		String updatedComment = "updated";
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, updatedComment);
		Assert.assertEquals(initialArrival.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(initialDeparture.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), updatedGroupReservation.getGuests());
		Assert.assertEquals(updatedComment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(updatedGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), updatedGroupReservation.getReservations().size());
	}
	
	@Test(expected = GroupReservationNonconsecutiveException.class)
	public void updateGroupReservationAddNonconsecutiveReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight initialArrival = new DateMidnight();
		DateMidnight initialDeparture = initialArrival.plusDays(3);
		long initialGuests = 3;
		String initialComment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), initialArrival, initialDeparture, initialGuests, initialComment);
		Assert.assertEquals(initialArrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(initialDeparture.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(initialGuests, createdGroupReservation.getGuests());
		Assert.assertEquals(initialComment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(createdGroupReservation.getReservations().isEmpty());

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = new DateMidnight().plusDays(7);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		
		DateMidnight departure00 = new DateMidnight().plusDays(4);
		DateMidnight departure01 = new DateMidnight().plusDays(9);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		
		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, null);
	}

	@Test
	public void updateGroupReservationAddMultipleReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight initialArrival = new DateMidnight();
		DateMidnight initialDeparture = initialArrival.plusDays(3);
		long initialGuests = 3;
		String initialComment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), initialArrival, initialDeparture, initialGuests, initialComment);
		Assert.assertEquals(initialArrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(initialDeparture.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(initialGuests, createdGroupReservation.getGuests());
		Assert.assertEquals(initialComment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(createdGroupReservation.getReservations().isEmpty());

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = arrival00.plusDays(1);
		DateMidnight arrival02 = arrival00.plusDays(2);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusMonths(1);
		DateMidnight departure01 = departure00.minusDays(1);
		DateMidnight departure02 = departure00.minusDays(2);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		String updatedComment = "updated";
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, updatedComment);
		Assert.assertEquals(arrival00.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), updatedGroupReservation.getGuests());
		Assert.assertEquals(updatedComment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(updatedGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), updatedGroupReservation.getReservations().size());
	}

	@Test(expected = GroupReservationConflictException.class)
	public void updateGroupReservationConflict() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdFirstGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
		GroupReservationBean createdSecondGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(4), new DateMidnight().plusDays(6), 3, null);
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdSecondGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
	}

	@Test
	public void updateGroupReservationRemoveOnlyReservation() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		DateMidnight arrival = new DateMidnight();
		DateMidnight departure = arrival.plusDays(3);
		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrival, departure);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
		Assert.assertEquals(arrival.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(createdGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getReservations().size());
		Assert.assertEquals(arrival.toDate(), createdGroupReservation.getReservations().get(0).getArrival());
		Assert.assertEquals(departure.toDate(), createdGroupReservation.getReservations().get(0).getDeparture());

		persistenceContextManager.flush();
		persistenceContextManager.clear();

		long guests = 5;
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), arrival, departure, guests, comment);
		Assert.assertEquals(arrival.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(departure.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(guests, updatedGroupReservation.getGuests());
		Assert.assertEquals(comment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(updatedGroupReservation.getReservations().isEmpty());
	}

	@Test
	public void updateGroupReservationRemoveOneReservations() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = arrival00.plusDays(1);
		DateMidnight arrival02 = arrival00.plusDays(2);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusMonths(1);
		DateMidnight departure01 = departure00.minusDays(1);
		DateMidnight departure02 = departure00.minusDays(2);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
		Assert.assertEquals(arrival00.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(createdGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getReservations().size());

		persistenceContextManager.flush();
		persistenceContextManager.clear();

		arrivalSet.remove(arrival02);
		departureSet.remove(departure02);
		Set<ReservationVo> updatedReservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), updatedReservationVoSet, comment);
		Assert.assertEquals(arrival00.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(updatedReservationVoSet.size(), updatedGroupReservation.getGuests());
		Assert.assertEquals(comment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(updatedGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(updatedReservationVoSet.size(), updatedGroupReservation.getReservations().size());
	}

	@Test
	public void updateGroupReservationRemoveAllReservations() throws UniqueConstraintException, GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = arrival00.plusDays(1);
		DateMidnight arrival02 = arrival00.plusDays(2);
		Set<DateMidnight> arrivalSet = new LinkedHashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusMonths(1);
		DateMidnight departure01 = departure00.minusDays(1);
		DateMidnight departure02 = departure00.minusDays(2);
		Set<DateMidnight> departureSet = new LinkedHashSet<DateMidnight>();
		departureSet.add(departure00);
		departureSet.add(departure01);
		departureSet.add(departure02);

		Set<ReservationVo> reservationVoSet = createReservationVoSet(arrivalSet, departureSet);
		String comment = null;
		GroupReservationBean createdGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), reservationVoSet, comment);
		Assert.assertEquals(arrival00.toDate(), createdGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), createdGroupReservation.getDeparture());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getGuests());
		Assert.assertEquals(comment, createdGroupReservation.getComment());
		Assert.assertFalse(createdGroupReservation.getRooms().isEmpty());
		Assert.assertFalse(createdGroupReservation.getReservations().isEmpty());
		Assert.assertEquals(reservationVoSet.size(), createdGroupReservation.getReservations().size());

		persistenceContextManager.flush();
		persistenceContextManager.clear();

		long guests = 5;
		GroupReservationBean updatedGroupReservation = groupReservationService.updateGroupReservation(createdGroupReservation.getGroupReservationId(), createdUser.getUserId(), createdUser.getUserId(), arrival00, departure00, guests, comment);
		Assert.assertEquals(arrival00.toDate(), updatedGroupReservation.getArrival());
		Assert.assertEquals(departure00.toDate(), updatedGroupReservation.getDeparture());
		Assert.assertEquals(guests, updatedGroupReservation.getGuests());
		Assert.assertEquals(comment, updatedGroupReservation.getComment());
		Assert.assertFalse(updatedGroupReservation.getRooms().isEmpty());
		Assert.assertTrue(updatedGroupReservation.getReservations().isEmpty());
	}

	protected Set<ReservationVo> createReservationVoSet(final DateMidnight arrival, final DateMidnight departure) {
		Set<ReservationVo> reservationVoSet = new LinkedHashSet<ReservationVo>();
		ReservationVo reservationVo = new ReservationVo(1, arrival, departure, "abc", "def");
		reservationVoSet.add(reservationVo);
		return reservationVoSet;
	}

	protected Set<ReservationVo> createReservationVoSet(final Set<DateMidnight> arrivalSet, final Set<DateMidnight> departureSet) {
		Iterator<DateMidnight> arrivalIterator = arrivalSet.iterator();
		Iterator<DateMidnight> departureIterator = departureSet.iterator();

		long precedence = 0;
		Set<ReservationVo> reservationVoSet = new LinkedHashSet<ReservationVo>();
		while (arrivalIterator.hasNext() && departureIterator.hasNext()) {
			ReservationVo reservationVo = new ReservationVo(++precedence, arrivalIterator.next(), departureIterator.next(), "abc", "def");
			reservationVoSet.add(reservationVo);
		}

		return reservationVoSet;
	}

	protected void doLogin(String username, String password) {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	protected void doLogout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}