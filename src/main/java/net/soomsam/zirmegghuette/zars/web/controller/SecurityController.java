package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("session")
public class SecurityController implements Serializable {
	private final static Logger logger = Logger.getLogger(SecurityController.class);

	@Inject
	protected transient UserService userService;
	
	private UserBean cachedCurrentUserBean;

	public synchronized UserBean getCurrentUser() {
		if (null == cachedCurrentUserBean) {
			cachedCurrentUserBean = userService.retrieveCurrentUser(); 
		}
		
		return cachedCurrentUserBean; 
	}

	public long getCurrentUserId() {
		UserBean currentUser = getCurrentUser();
		return currentUser.getUserId();
	}

	public boolean isCurrentUserAdmin() {
		return SecurityUtils.hasRole(RoleType.ROLE_ADMIN);
	}

	public boolean isFullyAuthenticated() {
		return SecurityUtils.isFullyAuthenticated();
	}
}
