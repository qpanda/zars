package net.soomsam.zirmegghuette.zars.service;

import java.util.List;

import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

public interface UserService {
	public static final String ROLE_USER = "user";
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_ACCOUNTANT = "accountant";

	public void createAllRoles();

	public List<RoleBean> findAllRoles();
}
