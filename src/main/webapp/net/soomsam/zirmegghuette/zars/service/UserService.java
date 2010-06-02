package net.soomsam.zirmegghuette.zars.service;

import java.util.List;

import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

public interface UserService {
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_ACCOUNTANT = "ROLE_ACCOUNTANT";

	public void createAllRoles();

	public List<RoleBean> findAllRoles();
}
