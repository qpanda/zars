package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

public interface UserService {
	public void createAllRoles();

	public List<RoleBean> findAllRoles();

	public long createUser(String username, String password, String emailAddress, String firstName, String lastName, Set<Long> roleIdSet);
}
