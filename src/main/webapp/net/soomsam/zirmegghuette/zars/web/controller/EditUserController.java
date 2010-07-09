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
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
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
public class EditUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(EditUserController.class);

	@Inject
	private transient UserService userService;

	private boolean validNavigation = true;

	private Long userId;

	@NotEmpty(message = "{sectionsApplicationUserUsernameError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationUserEmailAddressError}")
	@Email(message = "{sectionsApplicationUserEmailAddressError}")
	private String emailAddress;

	private String firstName;

	private String lastName;

	private List<RoleBean> availableRoles;

	private List<Long> selectedRoleIds;

	private UserBean savedUser;

	public boolean isValidNavigation() {
		return validNavigation;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
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

	public void retrieveUser() {
		if (null != this.userId) {
			try {
				final UserBean userBean = userService.retrieveUser(this.userId);
				this.userId = userBean.getUserId();
				this.username = userBean.getUsername();
				this.emailAddress = userBean.getEmailAddress();
				this.firstName = userBean.getFirstName();
				this.lastName = userBean.getLastName();
				this.selectedRoleIds = userBean.getRoleIds();
			} catch (final EntityNotFoundException entityNotFoundException) {
				this.validNavigation = false;
				final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
				FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			}
		}

		if (!FacesContext.getCurrentInstance().isPostback() && (null == this.userId)) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		}
	}

	public String update() {
		logger.debug("updating user with id [" + userId + "] and roles [" + determineSelectedRoleIds() + "]");
		try {
			savedUser = userService.updateUser(userId, username, emailAddress, firstName, lastName, determineSelectedRoleIds());
			return "editUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}