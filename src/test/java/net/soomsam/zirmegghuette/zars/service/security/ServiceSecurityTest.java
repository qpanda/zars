package net.soomsam.zirmegghuette.zars.service.security;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.utils.NoAuthenticationException;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
public class ServiceSecurityTest {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Test
	public void testAuthentication() {
		login("admin", "admin");
		logout();
	}

	@Test(expected = BadCredentialsException.class)
	public void testAuthenticationFailure() {
		login("wrong", "wrong");
		logout();
	}

	@Test
	public void verifyAdminRoles() {
		login("admin", "admin");
		Assert.assertTrue(SecurityUtils.hasRole(RoleType.ROLE_ADMIN));
		Assert.assertFalse(SecurityUtils.hasRole(RoleType.ROLE_USER));
		Assert.assertFalse(SecurityUtils.hasRole(RoleType.ROLE_ACCOUNTANT));
		logout();
	}

	@Test
	public void verifyUserRoles() throws UniqueConstraintException {
		login("admin", "admin");
		createUser("abc", "def");
		logout();
		login("abc", "def");
		Assert.assertFalse(SecurityUtils.hasRole(RoleType.ROLE_ADMIN));
		Assert.assertTrue(SecurityUtils.hasRole(RoleType.ROLE_USER));
		Assert.assertFalse(SecurityUtils.hasRole(RoleType.ROLE_ACCOUNTANT));
		logout();
	}

	@Test
	public void verifyAdminUsername() {
		login("admin", "admin");
		Assert.assertEquals("admin", SecurityUtils.determineUsername());
		logout();
	}

	@Test
	public void verifyDummyUsername() throws UniqueConstraintException {
		login("admin", "admin");
		createUser("abc", "def");
		logout();
		login("abc", "def");
		Assert.assertEquals("abc", SecurityUtils.determineUsername());
		logout();
	}

	@Test(expected = NoAuthenticationException.class)
	public void verifyNoAuthentication() {
		SecurityUtils.determineAuthentication();
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void invokeServiceWithoutAuthentication() {
		userService.enableUser(0);
	}

	@Test
	public void invokeServiceWithAuthentication() {
		login("admin", "admin");
		userService.enableUser(1);
		logout();
	}

	@Test(expected = AccessDeniedException.class)
	public void invokeServiceWithWrongAuthentication() throws UniqueConstraintException {
		login("admin", "admin");
		createUser("abc", "def");
		logout();
		login("abc", "def");
		userService.enableUser(0);
		logout();
	}

	protected void login(final String username, final String password) {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	protected void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	protected UserBean createUser(final String username, final String password) throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		return userService.createUser(username, password, "ghi@jkl.mno", "pqr", "stu", roleIdSet);
	}
}
