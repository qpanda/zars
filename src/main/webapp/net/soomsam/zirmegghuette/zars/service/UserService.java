package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

public interface UserService {
	public void createAllRoles();

	public void createDefaultUsers();

	public List<RoleBean> findAllRoles();

	public UserBean createUser(String username, String password, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet) throws UniqueConstraintException;

	public List<UserBean> finaAllUsers();
}
