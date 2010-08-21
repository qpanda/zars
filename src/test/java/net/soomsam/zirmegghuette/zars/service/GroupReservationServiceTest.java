package net.soomsam.zirmegghuette.zars.service;

import java.util.HashSet;
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
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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
		List<Room> allRooms = roomDao.findByPrecedence(true);
		List<RoleBean> allRoles = userService.findAllRoles();
		Set<Long> createUserRoleIds = TestUtils.determineRoleIds(allRoles, RoleType.ROLE_USER);
		UserBean createdUser = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", createUserRoleIds);
		GroupReservationBean createdGroupReservationRequiringOneRoom = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight(), new DateMidnight().plusDays(1), allRooms.get(0).getCapacity(), null);
		Assert.assertEquals(1, createdGroupReservationRequiringOneRoom.getRooms().size());
		GroupReservationBean createdGroupReservationRequiringOneAndAHalfRooms = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(2), new DateMidnight().plusDays(3), allRooms.get(0).getCapacity() + 1, null);
		Assert.assertEquals(2, createdGroupReservationRequiringOneAndAHalfRooms.getRooms().size());
		GroupReservationBean createdGroupReservationRequiringTwoRooms = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(4), new DateMidnight().plusDays(5), allRooms.get(0).getCapacity() + allRooms.get(1).getCapacity(), null);
		Assert.assertEquals(2, createdGroupReservationRequiringTwoRooms.getRooms().size());
		GroupReservationBean createdGroupReservationRequiringTwoAndAHalfRooms = groupReservationService.createGroupReservation(createdUser.getUserId(), createdUser.getUserId(), new DateMidnight().plusDays(6), new DateMidnight().plusDays(7), allRooms.get(0).getCapacity() + allRooms.get(1).getCapacity() + 1, null);
		Assert.assertEquals(2, createdGroupReservationRequiringTwoAndAHalfRooms.getRooms().size());
	}
}