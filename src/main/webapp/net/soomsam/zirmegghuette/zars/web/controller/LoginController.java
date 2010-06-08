package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.inject.Named;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class LoginController implements Serializable {
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
		System.out.println("$$$ login");
		this.loginFailed = true;
		this.loginFailedMessage = "test";
		return null;
	}
}