package net.soomsam.zirmegghuette.zars.service.security;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.TestUtils;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
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
@ContextConfiguration(locations = { "classpath:/WEB-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class ServiceSecurityTest {
	private final static Logger logger = Logger.getLogger(ServiceSecurityTest.class);

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
		createUser("abc", "def");
		login("abc", "def");
		userService.enableUser(0);
		logout();
	}

	protected void login(String username, String password) {
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	protected void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	protected UserBean createUser(String username, String password) throws UniqueConstraintException {
		List<RoleBean> roleBeanList = userService.findAllRoles();
		Set<Long> roleIdSet = TestUtils.determineRoleIds(roleBeanList, RoleType.ROLE_USER);
		return userService.createUser(username, password, "ghi@jkl.mno", "pqr", "stu", roleIdSet);
	}
}
