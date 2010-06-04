package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Named
@RequestScoped
public class AddUserController implements Serializable {
	@Inject
	private transient UserService userService;

	@NotEmpty(message = "{sectionsApplicationAddUserUserNameError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationAddUserPasswordError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationAddUserEmailAddressError}")
	@Email(message = "{sectionsApplicationAddUserEmailAddressError}")
	private String emailAddress;

	private String firstName;

	private String lastName;

	private List<RoleBean> availableRoles;

	private Set<Long> selectedRoleIds;

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

	public List<RoleBean> getAvailableRoles() {
		if (null == availableRoles) {
			availableRoles = userService.findAllRoles();
		}

		return availableRoles;
	}

	public Set<Long> getSelectedRoleIds() {
		return selectedRoleIds;
	}

	public void setSelectedRoleIds(final Set<Long> selectedRoleIds) {
		this.selectedRoleIds = selectedRoleIds;
	}

	public String create() {
		final UserBean userBean = userService.createUser(username, password, emailAddress, firstName, lastName, selectedRoleIds);
		return "groupReservation";
	}
}