package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ChangePreferencesController implements Serializable {
	private final static Logger logger = Logger.getLogger(ChangePreferencesController.class);

	@Inject
	private transient UserService userService;

	@Inject
	private transient PreferenceService preferenceService;

	@Inject
	private transient SecurityController securityController;

	@Inject
	private transient SettingController settingController;

	private List<SelectItem> availableTimezones;

	private String selectedTimezoneId;

	private List<SelectItem> supportedLocales;

	private String selectedLocaleDisplayName;

	private boolean emailNotification;

	public List<SelectItem> getAvailableTimezones() {
		if (null == availableTimezones) {
			final Locale preferredLocale = settingController.getPreferredLocale();
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
			final Locale preferredLocale = settingController.getPreferredLocale();
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

	public boolean isEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(final boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	public void retrievePreferences() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		final PreferenceBean timezonePreferenceBean = preferenceService.findCurrentUserPreference(PreferenceType.TIMEZONE);
		this.selectedTimezoneId = (String) timezonePreferenceBean.getValue();

		final PreferenceBean localePreferenceBean = preferenceService.findCurrentUserPreference(PreferenceType.LOCALE);
		this.selectedLocaleDisplayName = (String) localePreferenceBean.getValue();

		final PreferenceBean notificationPreferenceBean = preferenceService.findCurrentUserPreference(PreferenceType.NOTIFICATION);
		this.emailNotification = (Boolean) notificationPreferenceBean.getValue();
	}

	public String update() {
		final UserBean userBean = userService.retrieveCurrentUser();
		final String emailAddress = userBean.getEmailAddress();
		if (StringUtils.isEmpty(emailAddress) && emailNotification) {
			final FacesMessage notificationErrorFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserNotificationPreferenceError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, notificationErrorFacesMessage);
			return null;
		}

		logger.debug("updating preferences of current user [" + securityController.getCurrentUserId() + "]");
		preferenceService.updateCurrentUserPreference(PreferenceType.TIMEZONE, selectedTimezoneId);
		preferenceService.updateCurrentUserPreference(PreferenceType.LOCALE, selectedLocaleDisplayName);
		preferenceService.updateCurrentUserPreference(PreferenceType.NOTIFICATION, emailNotification);
		settingController.resetPreferredTimeZone();
		settingController.resetPreferredLocale();
		LocaleUtils.changeLocale(selectedLocaleDisplayName);
		return "changePreferencesConfirmation";
	}
}