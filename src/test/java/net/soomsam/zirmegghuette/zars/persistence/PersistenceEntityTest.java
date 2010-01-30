package net.soomsam.zirmegghuette.zars.persistence;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.apache.log4j.Logger;
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
	RoleDao roleDao;
	
	@Autowired
	UserDao userDao;

	@Test
	public void testCreateRoleUser() {
		Role roleUser = createRoleUser();
		logger.debug("persisted role 'user' as [" + roleUser + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(roleUser.getRoleId()));
	}
	
	@Test
	public void testCreateRoleAdmin() {
		Role roleAdmin = createRoleAdmin();
		logger.debug("persisted role 'admin' as [" + roleAdmin + "]");
		Assert.assertNotNull(roleDao.findByPrimaryKey(roleAdmin.getRoleId()));
	}
	
	@Test
	public void testCreateUserTest() {
		Role roleUser = createRoleUser();
		Role roleAdmin = createRoleAdmin();		
		User userTest = PersistenceEntityGenerator.createUserTest(roleUser, roleAdmin);
		userDao.persist(userTest);
		logger.debug("persisted user 'test' as [" + userTest + "]");
		
		User fetchedUserTest = userDao.findByPrimaryKey(userTest.getUserId());
		Assert.assertNotNull(fetchedUserTest);
		Assert.assertNotNull(fetchedUserTest.getRoles());
		Assert.assertEquals(2, fetchedUserTest.getRoles().size());
		Assert.assertTrue(fetchedUserTest.getRoles().contains(roleUser));
		Assert.assertTrue(fetchedUserTest.getRoles().contains(roleAdmin));
	}
	
	// TODO rooms
	// TODO groupreservation
	
	private Role createRoleUser() {
		Role roleUser = PersistenceEntityGenerator.createRoleUser();
		roleDao.persist(roleUser);
		return roleUser;
	}
	
	private Role createRoleAdmin() {
		Role roleAdmin = PersistenceEntityGenerator.createRoleAdmin();
		roleDao.persist(roleAdmin);
		return roleAdmin;
	}
	
	private User createUserTest() {
		Role roleUser = createRoleUser();
		Role roleAdmin = createRoleAdmin();		
		User userTest = PersistenceEntityGenerator.createUserTest(roleUser, roleAdmin);
		userDao.persist(userTest);
		return userTest;
	}
}
