package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("session")
public class AccessDeniedController implements Serializable {
	private final static Logger logger = Logger.getLogger(AccessDeniedController.class);

	public void prepareMessage() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		final FacesMessage accessDeniedFacesMessage = MessageFactory.getMessage("sectionsApplicationAccessDeniedError", FacesMessage.SEVERITY_FATAL, null);
		FacesContext.getCurrentInstance().addMessage(null, accessDeniedFacesMessage);
	}
}
