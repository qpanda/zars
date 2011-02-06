package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class LoginController implements Serializable {
	private final static Logger logger = Logger.getLogger(LoginController.class);
	
	@Inject
	private transient SecurityController securityController;

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
	
	public void checkAlreadyAuthenticated() throws IOException {
		if (securityController.isFullyAuthenticated()) {
			logger.debug("fully authenticated user detected, redirecting to default target URL");
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect("/views/adminGroupReservation.jsf");
		}
	}

	public String login() throws IOException, ServletException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		RequestDispatcher requestDispatcher = ((ServletRequest) externalContext.getRequest()).getRequestDispatcher("/j_spring_security_check");
		requestDispatcher.forward((ServletRequest) externalContext.getRequest(), (ServletResponse) externalContext.getResponse());
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private void handleAuthenticationFailure() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		Object authenticationExceptionObject = sessionMap.get(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (authenticationExceptionObject instanceof AuthenticationException) {
			AuthenticationException authenticationException = (AuthenticationException) authenticationExceptionObject;
			Authentication authentication = authenticationException.getAuthentication();
			if (null != authentication) {
				setUsername(authentication.getName());
				logger.warn("authentication for user [" + getUsername() + "] failed with 'AuthenticationException' [" + authenticationException.getCause() + "] and message [" + authenticationException.getMessage() + "]");
			} else {
				logger.warn("authentication failed with 'AuthenticationException' [" + authenticationException.getCause() + "] and message [" + authenticationException.getMessage() + "]");
			}

			sessionMap.remove(WebAttributes.AUTHENTICATION_EXCEPTION);
			final FacesMessage authenticationFailedFacesMessage = MessageFactory.getMessage("sectionsWelcomeLoginAuthenticationError", FacesMessage.SEVERITY_ERROR, (Object[])null);
			FacesContext.getCurrentInstance().addMessage(null, authenticationFailedFacesMessage);
		}
	}
}