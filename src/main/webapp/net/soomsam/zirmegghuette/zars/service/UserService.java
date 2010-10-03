package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.springframework.security.access.annotation.Secured;

public interface UserService {
	public void createAllRoles();

	public void createDefaultUsers();

	public List<RoleBean> findAllRoles();

	public UserBean createUser(String username, String password, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet) throws UniqueConstraintException;

	public UserBean updateUser(long userId, String username, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet) throws UniqueConstraintException;

	public UserBean resetUser(long userId, String password, boolean enabled);

	public UserBean retrieveUser(long userId);

	public List<UserBean> findAllUsers();

	@Secured("ROLE_ADMIN")
	public void enableUser(long userId);

	@Secured("ROLE_ADMIN")
	public void disableUser(long userId);

	public List<UserBean> findUsers(final RoleType roleType);

	public UserBean retrieveUser(String username);

	public UserBean retrieveCurrentUser();
}
