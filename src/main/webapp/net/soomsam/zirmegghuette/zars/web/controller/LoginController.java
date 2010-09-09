package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
	@Autowired
	private transient AuthenticationManager authenticationManager;

	@NotEmpty(message = "{sectionsWelcomeLoginUsernameError}")
	private String username;

	@NotEmpty(message = "{sectionsWelcomeLoginPasswordError}")
	private String password;

	private boolean loginFailed;

	private String loginFailedMessage;

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

	public boolean isLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(final boolean loginFailed) {
		this.loginFailed = loginFailed;
	}

	public String getLoginFailedMessage() {
		return loginFailedMessage;
	}

	public void setLoginFailedMessage(final String loginFailedMessage) {
		this.loginFailedMessage = loginFailedMessage;
	}

	public String login() {
		try {
			Authentication authenticationToken = new UsernamePasswordAuthenticationToken(getUsername(), getPassword());
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (AuthenticationException authenticationException) {
			// TODO error message
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			return null;
		}

		return "adminGroupReservation?faces-redirect=true";
	}
}