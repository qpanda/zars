package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Named
@SessionScoped
public class LoginBean implements Serializable {
	@NotNull
	@Size(min = 2, message = "{sectionsWelcomeLoginUsernameError}")
	private String username;

	@NotNull
	@Size(min = 2, message = "{sectionsWelcomeLoginPasswordError}")
	private String password;

	private boolean loginFailed;

	private String loginFailedMessage;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(boolean loginFailed) {
		this.loginFailed = loginFailed;
	}

	public String getLoginFailedMessage() {
		return loginFailedMessage;
	}

	public void setLoginFailedMessage(String loginFailedMessage) {
		this.loginFailedMessage = loginFailedMessage;
	}

	public String login() {
		System.out.println("$$$ login");
		this.loginFailed = true;
		this.loginFailedMessage = "test";
		return null;
	}
}