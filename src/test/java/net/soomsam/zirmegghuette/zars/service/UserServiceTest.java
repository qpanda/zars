package net.soomsam.zirmegghuette.zars.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

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
public class UserServiceTest {
	private final static Logger logger = Logger.getLogger(UserServiceTest.class);

	@Autowired
	private UserService userService;

	@Test
	public void findAllRoles() {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Assert.assertTrue(containsRoleType(roleBeanList, RoleType.ROLE_ACCOUNTANT));
		Assert.assertTrue(containsRoleType(roleBeanList, RoleType.ROLE_ADMIN));
		Assert.assertTrue(containsRoleType(roleBeanList, RoleType.ROLE_USER));
	}
	
	@Test
	public void createUser() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
		Assert.assertTrue(containsRoleType(userBean.getRoles(), RoleType.ROLE_USER));
	}
	
	@Test(expected = IllegalArgumentException.class)	
	public void createUserWithoutRoles() throws UniqueConstraintException {
		userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", new HashSet<Long>());
	}
	
	@Test
	public void updateUser() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		
		Set<Long> initialRoleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		UserBean initialUserBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", initialRoleIdSet);
		Assert.assertTrue(containsRoleType(initialUserBean.getRoles(), RoleType.ROLE_USER));
		Assert.assertFalse(containsRoleType(initialUserBean.getRoles(), RoleType.ROLE_ADMIN));
		
		Set<Long> updatedRoleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER, RoleType.ROLE_ADMIN);
		UserBean updatedUserBean = userService.updateUser(initialUserBean.getUserId(), "abc", "ghi@jkl.mno", "pqr", "stu", updatedRoleIdSet);
		Assert.assertTrue(containsRoleType(updatedUserBean.getRoles(), RoleType.ROLE_USER));
		Assert.assertTrue(containsRoleType(updatedUserBean.getRoles(), RoleType.ROLE_ADMIN));
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void updateUserWithInvalidId() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		userService.updateUser(-1L, "abc", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
	}	
	
	@Test(expected = IllegalArgumentException.class)	
	public void updateUserWithoutRoles() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
		userService.updateUser(userBean.getUserId(), "abc", "ghi@jkl.mno", "pqr", "stu", new HashSet<Long>());
	}
	
	@Test
	public void findUserWithSingleRole() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
	
		List<UserBean> userBeanList = userService.findUsers(RoleType.ROLE_USER);
		Assert.assertTrue(containsUser(userBeanList, userBean.getUserId()));
	}
	
	@Test
	public void findUserWithMultipleRoles() throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = determineRoleIds(roleBeanList, RoleType.ROLE_USER, RoleType.ROLE_ADMIN);
		UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
		
		List<UserBean> userBeanList01 = userService.findUsers(RoleType.ROLE_USER);		
		Assert.assertTrue(containsUser(userBeanList01, userBean.getUserId()));
		List<UserBean> userBeanList02 = userService.findUsers(RoleType.ROLE_ADMIN);		
		Assert.assertTrue(containsUser(userBeanList02, userBean.getUserId()));
	}
		
	protected boolean containsRoleType(List<RoleBean> roleBeanList, RoleType roleType) {
		for (RoleBean roleBean : roleBeanList) {
			if (roleType.getRoleName().equals(roleBean.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
	protected Set<Long> determineRoleIds(List<RoleBean> roleBeanList, RoleType... roleTypeArray) {
		Set<Long> roleIdSet = new HashSet<Long>();
		for (RoleBean roleBean : roleBeanList) {
			for (RoleType roleType : roleTypeArray) {
				if (roleType.getRoleName().equals(roleBean.getName())) {
					roleIdSet.add(roleBean.getRoleId());
				}
			}
		}
		
		return roleIdSet;
	}
	
	protected boolean containsUser(List<UserBean> userBeanList, long userId) {
		for (UserBean userBean : userBeanList) {
			if (userId == userBean.getUserId()) {
				return true;
			}
		}
		
		return false;
	}
}