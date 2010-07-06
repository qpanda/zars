package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class AddUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(AddUserController.class);

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

	private List<Long> selectedRoleIds;

	private UserBean savedUser;

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

	public List<Long> getSelectedRoleIds() {
		return selectedRoleIds;
	}

	public void setSelectedRoleIds(final List<Long> selectedRoleIds) {
		this.selectedRoleIds = selectedRoleIds;
	}

	public Set<Long> determineSelectedRoleIds() {
		return new HashSet<Long>(selectedRoleIds);
	}

	public UserBean getSavedUser() {
		return savedUser;
	}

	public String create() {
		logger.debug("creating user with username [" + username + "] and roles [" + determineSelectedRoleIds() + "]");
		try {
			savedUser = userService.createUser(username, password, emailAddress, firstName, lastName, determineSelectedRoleIds());
			return "addUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationAddUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}