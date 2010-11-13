package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

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

	@Inject
	private transient PreferenceService preferenceService;

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

	private List<TimeZone> availableTimezones;

	private String selectedTimezone;

	private UserBean savedUser;

	private PreferenceBean savedTimezonePreference;

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

	public List<TimeZone> getAvailableTimezones() {
		if (null == availableTimezones) {
			availableTimezones = LocaleUtils.determineSupportedTimezoneList();
		}

		return availableTimezones;
	}

	public String getSelectedTimezone() {
		return selectedTimezone;
	}

	public void setSelectedTimezone(final String selectedTimezone) {
		this.selectedTimezone = selectedTimezone;
	}

	public UserBean getSavedUser() {
		return savedUser;
	}

	public PreferenceBean getSavedTimezonePreference() {
		return savedTimezonePreference;
	}

	public String getInvalidUserIdMessage() {
		return MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null).getSummary();
	}

	public void retrieveUser() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			this.validNavigation = false;
			return;
		}

		if (null == this.userId) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			return;
		}

		try {
			final UserBean userBean = userService.retrieveUser(this.userId);
			this.userId = userBean.getUserId();
			this.username = userBean.getUsername();
			this.emailAddress = userBean.getEmailAddress();
			this.firstName = userBean.getFirstName();
			this.lastName = userBean.getLastName();
			this.selectedRoleIds = userBean.getRoleIds();

			// TODO use retrieve instead of find here, requires adding preference to default user generator
			final PreferenceBean timezonePreferenceBean = preferenceService.findPreference(userId, PreferenceType.TIMEZONE);
			this.selectedTimezone = (String)timezonePreferenceBean.getValue();
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		}
	}

	public String update() {
		logger.debug("updating user with id [" + userId + "] and roles [" + determineSelectedRoleIds() + "]");
		try {
			savedUser = userService.updateUser(userId, username, emailAddress, firstName, lastName, determineSelectedRoleIds());
			savedTimezonePreference = preferenceService.updatePreference(savedUser.getUserId(), PreferenceType.TIMEZONE, selectedTimezone);
			return "editUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}