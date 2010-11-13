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
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.apache.commons.lang.StringUtils;
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

	@Inject
	private transient PreferenceService preferenceService;

	@NotEmpty(message = "{sectionsApplicationUserUsernameError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationUserPasswordError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationUserPasswordError}")
	private String confirmPassword;

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

	public AddUserController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultSelectedTimezone();
		}
	}

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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
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

	protected void setDefaultSelectedTimezone() {
		selectedTimezone = LocaleUtils.determineDefaultTimezone().getID();
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

	public String create() {
		if (!StringUtils.equals(password, confirmPassword)) {
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage("sectionsApplicationUserPasswordError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
			return null;
		}

		logger.debug("creating user with username [" + username + "] and roles [" + determineSelectedRoleIds() + "]");
		try {
			savedUser = userService.createUser(username, password, emailAddress, firstName, lastName, determineSelectedRoleIds());
			savedTimezonePreference = preferenceService.createPreference(savedUser.getUserId(), PreferenceType.TIMEZONE, selectedTimezone);
			return "addUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}