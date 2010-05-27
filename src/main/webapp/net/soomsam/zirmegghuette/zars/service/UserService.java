package net.soomsam.zirmegghuette.zars.service;

import java.util.List;

import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

public interface UserService {
	public List<RoleBean> findAllRoles();
}
