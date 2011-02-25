package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Locale;

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

	public PreferenceBean getSavedTimezonePreference() {
		return savedTimezonePreference;
	}

	public void setSavedTimezonePreference(final PreferenceBean savedTimezonePreference) {
		this.savedTimezonePreference = savedTimezonePreference;
	}

	public PreferenceBean getSavedLocalePreference() {
		return savedLocalePreference;
	}

	public void setSavedLocalePreference(final PreferenceBean savedLocalePreference) {
		this.savedLocalePreference = savedLocalePreference;
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