package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class AdminUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(AdminUserController.class);

	@Inject
	private transient UserService userService;

	public List<UserBean> getAllUsers() {
		return userService.finaAllUsers();
	}

	public void doStuff() {
	}
}