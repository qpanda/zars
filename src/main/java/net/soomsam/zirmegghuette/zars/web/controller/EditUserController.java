package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
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

	@Inject
	private transient SettingController settingController;

	private boolean validNavigation = true;

	private Long userId;

	@NotEmpty(message = "{sectionsApplicationUserUsernameEmptyError}")
	@Length(max = User.COLUMNLENGTH_USERNAME, message = "{sectionsApplicationUserUsernameLengthError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationUserEmailAddressEmptyError}")
	@Email(message = "{sectionsApplicationUserEmailAddressInvalidError}")
	@Length(max = User.COLUMNLENGTH_EMAILADDRESS, message = "{sectionsApplicationUserEmailAddressLengthError}")
	private String emailAddress;

	@Length(max = User.COLUMNLENGTH_FIRSTNAME, message = "{sectionsApplicationUserFirstNameLengthError}")
	private String firstName;

	@Length(max = User.COLUMNLENGTH_LASTNAME, message = "{sectionsApplicationUserLastNameLengthError}")
	private String lastName;

	private List<RoleBean> availableRoles;

	private List<Long> selectedRoleIds;

	private List<SelectItem> availableTimezones;

	private String selectedTimezoneId;

	private List<SelectItem> supportedLocales;

	private String selectedLocaleDisplayName;

	private UserBean savedUser;

	private PreferenceBean savedTimezonePreference;

	private PreferenceBean savedLocalePreference;

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

	public List<SelectItem> getAvailableTimezones() {
		if (null == availableTimezones) {
			Locale preferredLocale = settingController.getPreferredLocale();
			final List<TimeZone> availableTimezoneList = LocaleUtils.determineSupportedTimezoneList();
			availableTimezones = new ArrayList<SelectItem>();
			for (final TimeZone availableTimezone : availableTimezoneList) {
				availableTimezones.add(new SelectItem(availableTimezone.getID(), availableTimezone.getID() + " - " + availableTimezone.getDisplayName(preferredLocale)));
			}
		}

		return availableTimezones;
	}

	public String getSelectedTimezoneId() {
		return selectedTimezoneId;
	}

	public void setSelectedTimezoneId(final String selectedTimezoneId) {
		this.selectedTimezoneId = selectedTimezoneId;
	}

	public List<SelectItem> getSupportedLocales() {
		if (null == supportedLocales) {
			Locale preferredLocale = settingController.getPreferredLocale();
			final List<Locale> supportedLocaleList = LocaleUtils.determineSupportedLocaleList();
			supportedLocales = new ArrayList<SelectItem>();
			for (final Locale supportedLocale : supportedLocaleList) {
				supportedLocales.add(new SelectItem(supportedLocale.getDisplayName(), supportedLocale.getDisplayName(preferredLocale)));
			}
		}

		return supportedLocales;
	}

	public String getSelectedLocaleDisplayName() {
		return selectedLocaleDisplayName;
	}

	public void setSelectedLocaleDisplayName(final String selectedLocaleDisplayName) {
		this.selectedLocaleDisplayName = selectedLocaleDisplayName;
	}

	public UserBean getSavedUser() {
		return savedUser;
	}

	public PreferenceBean getSavedTimezonePreference() {
		return savedTimezonePreference;
	}

	public PreferenceBean getSavedLocalePreference() {
		return savedLocalePreference;
	}

	public String getInvalidUserIdMessage() {
		Locale preferredLocale = settingController.getPreferredLocale();
		return MessageFactory.getMessage(preferredLocale, "sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null).getSummary();
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

			final PreferenceBean timezonePreferenceBean = preferenceService.findPreference(userId, PreferenceType.TIMEZONE);
			this.selectedTimezoneId = (String)timezonePreferenceBean.getValue();

			final PreferenceBean localePreferenceBean = preferenceService.findPreference(userId, PreferenceType.LOCALE);
			this.selectedLocaleDisplayName = (String)localePreferenceBean.getValue();
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
			savedTimezonePreference = preferenceService.updatePreference(savedUser.getUserId(), PreferenceType.TIMEZONE, selectedTimezoneId);
			savedLocalePreference = preferenceService.updatePreference(savedUser.getUserId(), PreferenceType.LOCALE, selectedLocaleDisplayName);
			return "editUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}