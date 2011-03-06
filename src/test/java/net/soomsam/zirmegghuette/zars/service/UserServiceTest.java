package net.soomsam.zirmegghuette.zars.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

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
public class UserServiceTest {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Before
	public void login() {
		doLogin("admin", "admin");
	}

	@After
	public void logout() {
		doLogout();
	}

	@Test
	public void findAllRoles() {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		Assert.assertTrue(TestUtils.containsRoleType(roleBeanList, RoleType.ROLE_ACCOUNTANT));
		Assert.assertTrue(TestUtils.containsRoleType(roleBeanList, RoleType.ROLE_ADMIN));
		Assert.assertTrue(TestUtils.containsRoleType(roleBeanList, RoleType.ROLE_USER));
	}

	@Test
	public void createUser() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		final UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
		Assert.assertTrue(TestUtils.containsRoleType(userBean.getRoles(), RoleType.ROLE_USER));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserWithoutRoles() throws UniqueConstraintException {
		userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", new HashSet<Long>());
	}

	@Test
	public void updateUser() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();

		final Set<Long> initialRoleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		final UserBean initialUserBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", initialRoleIdSet);
		Assert.assertTrue(TestUtils.containsRoleType(initialUserBean.getRoles(), RoleType.ROLE_USER));
		Assert.assertFalse(TestUtils.containsRoleType(initialUserBean.getRoles(), RoleType.ROLE_ADMIN));

		final Set<Long> updatedRoleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER, RoleType.ROLE_ADMIN);
		final UserBean updatedUserBean = userService.updateUser(initialUserBean.getUserId(), "abc", "ghi@jkl.mno", "pqr", "stu", updatedRoleIdSet);
		Assert.assertTrue(TestUtils.containsRoleType(updatedUserBean.getRoles(), RoleType.ROLE_USER));
		Assert.assertTrue(TestUtils.containsRoleType(updatedUserBean.getRoles(), RoleType.ROLE_ADMIN));
	}

	@Test(expected = EntityNotFoundException.class)
	public void updateUserWithInvalidId() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		userService.updateUser(-1L, "abc", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateUserWithoutRoles() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		final UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);
		userService.updateUser(userBean.getUserId(), "abc", "ghi@jkl.mno", "pqr", "stu", new HashSet<Long>());
	}

	@Test
	public void findUserWithSingleRole() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		final UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);

		final List<UserBean> userBeanList = userService.findUsers(RoleType.ROLE_USER);
		Assert.assertTrue(TestUtils.containsUser(userBeanList, userBean.getUserId()));
	}

	@Test
	public void findUserWithMultipleRoles() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER, RoleType.ROLE_ADMIN);
		final UserBean userBean = userService.createUser("abc", "def", "ghi@jkl.mno", "pqr", "stu", roleIdSet);

		final List<UserBean> userBeanList01 = userService.findUsers(RoleType.ROLE_USER);
		Assert.assertTrue(TestUtils.containsUser(userBeanList01, userBean.getUserId()));
		final List<UserBean> userBeanList02 = userService.findUsers(RoleType.ROLE_ADMIN);
		Assert.assertTrue(TestUtils.containsUser(userBeanList02, userBean.getUserId()));
	}

	@Test
	public void findMultipleUsersWithRole() throws UniqueConstraintException {
		final List<RoleBean> roleBeanList = userService.findAllRoles();
		final Set<Long> roleIdSet01 = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		final UserBean userBean01 = userService.createUser("abc01", "def01", "ghi01@jkl.mno", "pqr01", "stu01", roleIdSet01);
		final Set<Long> roleIdSet02 = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_ADMIN);
		final UserBean userBean02 = userService.createUser("abc02", "def02", "ghi02@jkl.mno", "pqr02", "stu02", roleIdSet02);
		final Set<Long> roleIdSet03 = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_ACCOUNTANT);
		final UserBean userBean03 = userService.createUser("abc03", "def03", "ghi03@jkl.mno", "pqr03", "stu03", roleIdSet03);

		final List<UserBean> userBeanList01 = userService.findUsers(RoleType.ROLE_USER);
		Assert.assertTrue(TestUtils.containsUser(userBeanList01, userBean01.getUserId()));
		Assert.assertFalse(TestUtils.containsUser(userBeanList01, userBean02.getUserId()));
		Assert.assertFalse(TestUtils.containsUser(userBeanList01, userBean03.getUserId()));

		final List<UserBean> userBeanList02 = userService.findUsers(RoleType.ROLE_ADMIN);
		Assert.assertFalse(TestUtils.containsUser(userBeanList02, userBean01.getUserId()));
		Assert.assertTrue(TestUtils.containsUser(userBeanList02, userBean02.getUserId()));
		Assert.assertFalse(TestUtils.containsUser(userBeanList02, userBean03.getUserId()));

		final List<UserBean> userBeanList03 = userService.findUsers(RoleType.ROLE_ACCOUNTANT);
		Assert.assertFalse(TestUtils.containsUser(userBeanList03, userBean01.getUserId()));
		Assert.assertFalse(TestUtils.containsUser(userBeanList03, userBean02.getUserId()));
		Assert.assertTrue(TestUtils.containsUser(userBeanList03, userBean03.getUserId()));
	}

	protected void doLogin(final String username, final String password) {
		final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		final Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	protected void doLogout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}