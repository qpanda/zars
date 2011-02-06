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
import javax.validation.constraints.Pattern;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class AddUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(AddUserController.class);

	@Inject
	private transient UserService userService;

	@Inject
	private transient PreferenceService preferenceService;

	@Inject
	private transient SettingController settingController;

	@NotEmpty(message = "{sectionsApplicationUserUsernameEmptyError}")
	@Length(max = User.COLUMNLENGTH_USERNAME, message = "{sectionsApplicationUserUsernameLengthError}")
	@Pattern(regexp = "[\\p{L}\\p{N}]+[\\p{L}\\p{N}._-]*", message = "{sectionsApplicationUserUsernameInvalidError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String confirmPassword;

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

	public AddUserController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultSelectedTimezoneId();
			setDefaultSelectedLocaleDisplayName();
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

	protected void setDefaultSelectedTimezoneId() {
		selectedTimezoneId = (String)PreferenceType.TIMEZONE.getPreferenceDefaultValue();
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

	protected void setDefaultSelectedLocaleDisplayName() {
		selectedLocaleDisplayName = (String)PreferenceType.LOCALE.getPreferenceDefaultValue();
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

	public String create() {
		if (!StringUtils.equals(password, confirmPassword)) {
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage("sectionsApplicationUserPasswordInvalidError", FacesMessage.SEVERITY_ERROR, (Object[])null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
			return null;
		}

		logger.debug("creating user with username [" + username + "] and roles [" + determineSelectedRoleIds() + "]");
		try {
			savedUser = userService.createUser(username, password, emailAddress, firstName, lastName, determineSelectedRoleIds());
			savedTimezonePreference = preferenceService.createPreference(savedUser.getUserId(), PreferenceType.TIMEZONE, selectedTimezoneId);
			savedLocalePreference = preferenceService.createPreference(savedUser.getUserId(), PreferenceType.LOCALE, selectedLocaleDisplayName);
			return "addUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageFactory.getMessage(uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR, (Object[])null);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}