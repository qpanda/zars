package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class EditUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(EditUserController.class);

	private Long userId;

	@Inject
	private transient UserService userService;

	private UserBean editUser;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public void retrieveUser() {
		if (null == userId) {
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationEditUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		} else {
			System.out.println("### should retrieve user with id " + userId + " here");
		}
	}

	public String save() {
		return null;
	}
}