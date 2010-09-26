package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("session")
public class SecurityController implements Serializable {
	private final static Logger logger = Logger.getLogger(SecurityController.class);

	public boolean hasRole(String role) {
		return SecurityUtils.hasRole(role);
	}
}
