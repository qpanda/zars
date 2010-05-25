package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.hibernate.validator.constraints.NotEmpty;

@Named
@SessionScoped
public class AddUserBean implements Serializable {
	@NotEmpty(message = "{sectionsApplicationAddUserUserNameError}")
	private String userName;

	@NotEmpty(message = "{sectionsApplicationAddUserPasswordError}")
	private String password;

	private String emailAddress;

	private String firstName;

	private String lastName;

	private Set<Long> roleIdSet = new HashSet<Long>();

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
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