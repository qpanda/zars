package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class LoginController implements Serializable {
	private final static Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	private transient AuthenticationManager authenticationManager;

	@Inject
	protected transient LocaleController localeController;

	@NotEmpty(message = "{sectionsWelcomeLoginUsernameError}")
	private String username;

	@NotEmpty(message = "{sectionsWelcomeLoginPasswordError}")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String login() {
		try {
			Authentication authenticationToken = new UsernamePasswordAuthenticationToken(getUsername(), getPassword());
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (AuthenticationException authenticationException) {
			logger.warn("authentication for user [" + getUsername() + "] failed with 'AuthenticationException' [" + authenticationException.getCause() + "] and message [" + authenticationException.getMessage() + "]");
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage("sectionsWelcomeLoginAuthenticationError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
			return null;
		}

		return "adminGroupReservation?faces-redirect=true";
	}
}