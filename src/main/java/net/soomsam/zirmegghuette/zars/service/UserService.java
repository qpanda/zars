package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.springframework.security.access.prepost.PreAuthorize;

public interface UserService {
	public void createAllRoles();

	public void createDefaultUsers();

	@PreAuthorize("isAuthenticated()")
	public List<RoleBean> findAllRoles();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserBean createUser(String username, String password, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet) throws UniqueConstraintException;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserBean updateUser(long userId, String username, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet) throws UniqueConstraintException;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserBean resetUser(long userId, String password, boolean enabled);

	@PreAuthorize("isAuthenticated()")
	public UserBean changePassword(String password);

	@PreAuthorize("isAuthenticated()")
	public UserBean changeUser(String emailAddress, String firstName, String lastName) throws UniqueConstraintException;

	@PreAuthorize("isAuthenticated()")
	public UserBean retrieveUser(long userId);

	@PreAuthorize("isAuthenticated()")
	public List<UserBean> findAllUsers();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void enableUser(long userId);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void disableUser(long userId);

	public List<UserBean> findUsers(final RoleType roleType);

	public UserBean retrieveUser(String username);

	@PreAuthorize("isAuthenticated()")
	public UserBean retrieveCurrentUser();
}
