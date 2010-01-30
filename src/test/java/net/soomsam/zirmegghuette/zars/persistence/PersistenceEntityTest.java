package net.soomsam.zirmegghuette.zars.persistence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceException;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
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

	@Test
	public void testCreateUserRole() {
		Role userRole = createUserRole();
		logger.debug("persisted role 'user' as [" + userRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(userRole.getRoleId()));
	}
	
	@Test
	public void testCreateAdminRole() {
		Role adminRole = createAdminRole();
		logger.debug("persisted role 'admin' as [" + adminRole + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(adminRole.getRoleId()));
	}
	
	@Test
	public void testCreateTestUser() {
		Role userRole = createUserRole();
		Role adminRole = createAdminRole();		
		User testUser = PersistenceEntityGenerator.createUserTest(userRole, adminRole);
		userDao.persist(testUser);
		logger.debug("persisted user 'test' as [" + testUser + "]");
		
		User fetchedTestUser = userDao.findByPrimaryKey(testUser.getUserId());
		Assert.assertNotNull(fetchedTestUser);
		Assert.assertNotNull(fetchedTestUser.getRoles());
		Assert.assertEquals(2, fetchedTestUser.getRoles().size());
		Assert.assertTrue(fetchedTestUser.getRoles().contains(userRole));
		Assert.assertTrue(fetchedTestUser.getRoles().contains(adminRole));
	}
	
	@Test(expected = PersistenceException.class)
	public void testCreateTestUserWithoutValues() {
		User testUserWithoutRole = new User(null, null, true, createUserRole());
		userDao.persist(testUserWithoutRole);
		userDao.flush();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTestUserWithoutRole() {
		User testUserWithoutRole = new User("test", "test", true, (Role)null);
		userDao.persist(testUserWithoutRole);
		userDao.flush();
	}
	
	@Test(expected = InvalidStateException.class)
	public void testCreateTestUserWithoutRoles() {
		User testUserWithoutRoles = new User("test", "test", true, (Set<Role>)null);
		userDao.persist(testUserWithoutRoles);
		userDao.flush();
	}
	
	@Test
	public void testCreateFirstRoom() {
		Room firstRoom = createFirstRoom();
		logger.debug("persisted room 'first' as [" + firstRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(firstRoom.getRoomId()));
	}
	
	@Test
	public void testCreateSecondRoom() {
		Room secondRoom = createSecondRoom();
		logger.debug("persisted room 'second' as [" + secondRoom + "]");
		Assert.assertNotNull(roomDao.findByPrimaryKey(secondRoom.getRoomId()));
	}
	
	@Test
	public void testGroupReservation() {
		// TODO
		GroupReservation groupReservation = createGroupReservation();
		logger.debug("persisted group reservation as [" + groupReservation + "]");
	}
	
	private Role createUserRole() {
		Role userRole = PersistenceEntityGenerator.createUserRole();
		roleDao.persist(userRole);
		return userRole;
	}
	
	private Role createAdminRole() {
		Role adminRole = PersistenceEntityGenerator.createAdminRole();
		roleDao.persist(adminRole);
		return adminRole;
	}
	
	private Room createFirstRoom() {
		Room firstRoom = PersistenceEntityGenerator.createFirstRoom();
		roomDao.persist(firstRoom);
		return firstRoom;
	}
	
	private Room createSecondRoom() {
		Room secondRoom = PersistenceEntityGenerator.createSecondRoom();
		roomDao.persist(secondRoom);
		return secondRoom;
	}
	
	private User createTestUser() {
		Role userRole = createUserRole();
		Role adminRole = createAdminRole();		
		User testUser = PersistenceEntityGenerator.createUserTest(userRole, adminRole);
		userDao.persist(testUser);
		return testUser;
	}
	
	private GroupReservation createGroupReservation() {
		User testUser = createTestUser();
		Room firstRoom = createFirstRoom();
		return new GroupReservation(testUser, firstRoom);
	}
}
