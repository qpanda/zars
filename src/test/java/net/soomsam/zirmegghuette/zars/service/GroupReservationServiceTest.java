package net.soomsam.zirmegghuette.zars.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;

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
public class GroupReservationServiceTest {
	private final static Logger logger = Logger.getLogger(GroupReservationServiceTest.class);

	@Autowired
	private UserService userService;

	@Autowired
	private GroupReservationService groupReservationService;

	@Autowired
	private RoomDao roomDao;

	@Test
	public void createGroupReservation() throws UniqueConstraintException, GroupReservationConflictException {
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
	public void createGroupReservationConflict() throws UniqueConstraintException, GroupReservationConflictException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdExistingGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
		GroupReservationBean createdConflictingGroupReservation = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(1), new DateMidnight().plusDays(5), 3, null);
	}

	@Test
	public void createGroupReservationWithoutConflict() throws UniqueConstraintException, GroupReservationConflictException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdGroupReservation01 = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(3), 3, null);
		GroupReservationBean createdGroupReservation02 = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(3), new DateMidnight().plusDays(5), 3, null);
	}

	@Test
	public void verifyGroupReservationRoomSelection() throws UniqueConstraintException, GroupReservationConflictException {
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

			GroupReservationBean createdGroupReservationWithLessThanTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().minusDays(3).plusMonths(i), new DateMidnight().minusDays(1).plusMonths(i), totalCapacity - 1, null);
			Assert.assertEquals(i, createdGroupReservationWithLessThanTotalCapacity.getRooms().size());
			GroupReservationBean createdGroupReservationWithTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().minusDays(1).plusMonths(i), new DateMidnight().plusDays(1).plusMonths(i), totalCapacity, null);
			Assert.assertEquals(i, createdGroupReservationWithTotalCapacity.getRooms().size());
			GroupReservationBean createdGroupReservationWithMoreThanTotalCapacity = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(1).plusMonths(i), new DateMidnight().plusDays(3).plusMonths(i), totalCapacity + 1, null);
			Assert.assertEquals(allRoomIterator.hasNext() ? (i + 1) : i, createdGroupReservationWithMoreThanTotalCapacity.getRooms().size());
		}
	}

	@Test
	public void createGroupReservationWithOneIndividualReservation() throws UniqueConstraintException, GroupReservationConflictException {
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
	public void createGroupReservationWithMultipleIndividualReservations() throws UniqueConstraintException, GroupReservationConflictException {
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);

		DateMidnight arrival00 = new DateMidnight();
		DateMidnight arrival01 = arrival00.plusDays(1);
		DateMidnight arrival02 = arrival00.plusDays(2);
		Set<DateMidnight> arrivalSet = new HashSet<DateMidnight>();
		arrivalSet.add(arrival00);
		arrivalSet.add(arrival01);
		arrivalSet.add(arrival02);

		DateMidnight departure00 = new DateMidnight().plusMonths(1);
		DateMidnight departure01 = departure00.minusDays(1);
		DateMidnight departure02 = departure00.minusDays(2);
		Set<DateMidnight> departureSet = new HashSet<DateMidnight>();
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

	protected Set<ReservationVo> createReservationVoSet(final DateMidnight arrival, final DateMidnight departure) {
		Set<ReservationVo> reservationVoSet = new HashSet<ReservationVo>();
		ReservationVo reservationVo = new ReservationVo(arrival, departure, "abc", "def");
		reservationVoSet.add(reservationVo);
		return reservationVoSet;
	}

	protected Set<ReservationVo> createReservationVoSet(final Set<DateMidnight> arrivalSet, final Set<DateMidnight> departureSet) {
		Iterator<DateMidnight> arrivalIterator = arrivalSet.iterator();
		Iterator<DateMidnight> departureIterator = departureSet.iterator();

		Set<ReservationVo> reservationVoSet = new HashSet<ReservationVo>();
		while (arrivalIterator.hasNext() && departureIterator.hasNext()) {
			ReservationVo reservationVo = new ReservationVo(arrivalIterator.next(), departureIterator.next(), "abc", "def");
			reservationVoSet.add(reservationVo);
		}

		return reservationVoSet;
	}
}