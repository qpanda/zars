package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Named
@SessionScoped
public class AddUserController implements Serializable {
	@NotEmpty(message = "{sectionsApplicationAddUserUserNameError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationAddUserPasswordError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationAddUserEmailAddressError}")
	@Email(message = "{sectionsApplicationAddUserEmailAddressError}")
	private String emailAddress;

	private String firstName;

	private String lastName;

	private Set<Long> roleIdSet = new HashSet<Long>();

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public Set<Long> getRoleIdSet() {
		return roleIdSet;
	}

	public void setRoleIdSet(final Set<Long> roleIdSet) {
		this.roleIdSet = roleIdSet;
	}

	public String create() {
		return null;
	}
}