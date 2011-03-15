package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ViewUserController implements Serializable {
	@Inject
	protected transient UserService userService;

	@Inject
	protected transient SettingController settingController;

	@Inject
	private transient PreferenceService preferenceService;

	private boolean validNavigation = true;

	private Long userId;

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

	public UserBean getSavedUser() {
		return savedUser;
	}

	public void setSavedUser(final UserBean savedUser) {
		this.savedUser = savedUser;
	}

	public String getSavedTimezonePreferenceDisplayName() {
		final Locale preferredLocale = settingController.getPreferredLocale();
		final String savedTimezoneIdPreference = (String) savedTimezonePreference.getValue();
		final TimeZone savedTimezonePreference = LocaleUtils.determineSupportedTimezone(savedTimezoneIdPreference);
		return LocaleUtils.determineTimezoneDisplayName(preferredLocale, savedTimezonePreference);
	}

	public String getSavedLocalePreferenceDisplayName() {
		final Locale preferredLocale = settingController.getPreferredLocale();
		final String savedLocalePreferenceDisplayName = (String) savedLocalePreference.getValue();
		final Locale savedLocalePreference = LocaleUtils.determineSupportedLocale(savedLocalePreferenceDisplayName);
		return LocaleUtils.determineLocaleDisplayName(preferredLocale, savedLocalePreference);
	}

	public String getInvalidUserIdMessage() {
		final Locale preferredLocale = settingController.getPreferredLocale();
		return MessageFactory.getMessage(preferredLocale, "sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, (Object[]) null).getSummary();
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
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, (Object[]) null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			return;
		}

		try {
			savedUser = userService.retrieveUser(userId);
			savedTimezonePreference = preferenceService.findPreference(userId, PreferenceType.TIMEZONE);
			savedLocalePreference = preferenceService.findPreference(userId, PreferenceType.LOCALE);
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, (Object[]) null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		}
	}
}