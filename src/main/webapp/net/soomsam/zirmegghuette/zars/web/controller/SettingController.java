package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("session")
public class SettingController implements Serializable {
	private final static Logger logger = Logger.getLogger(SettingController.class);

	@Inject
	private transient PreferenceService preferenceService;

	private TimeZone preferredTimeZone;

	private Locale preferredLocale;

	public void resetPreferredTimeZone() {
		preferredTimeZone = null;
	}

	public TimeZone getPreferredTimeZone() {
		if (null == preferredTimeZone) {
			preferredTimeZone = determinePreferredTimeZone();
			logger.debug("using preferred timezone [" + preferredTimeZone.getID() + "] rather than default timezone [" + TimeZone.getDefault().getID() + "]");
		}

		return preferredTimeZone;
	}

	protected TimeZone determinePreferredTimeZone() {
		PreferenceBean currentUserTimezonePreference = preferenceService.findCurrentUserPreference(PreferenceType.TIMEZONE);
		if (null != currentUserTimezonePreference) {
			String currentUserTimezoneId = (String)currentUserTimezonePreference.getValue();
			return TimeZone.getTimeZone(currentUserTimezoneId);
		}

		return LocaleUtils.determineDefaultTimezone();
	}

	public void resetPreferredLocale() {
		preferredLocale = null;
	}

	public Locale getPreferredLocale() {
		if (null == preferredLocale) {
			preferredLocale = determinePreferredLocale();
			logger.debug("using preferred locale [" + preferredLocale.getDisplayName() + "]");
		}

		return preferredLocale;
	}

	protected Locale determinePreferredLocale() {
		PreferenceBean currentUserLocalePreference = preferenceService.findCurrentUserPreference(PreferenceType.LOCALE);
		if (null != currentUserLocalePreference) {
			String currentUserLocaleDisplayName = (String)currentUserLocalePreference.getValue();
			return LocaleUtils.determineSupportedLocale(currentUserLocaleDisplayName);
		}

		return LocaleUtils.determineCurrentLocale();
	}

	public SimpleDateFormat getPreferredDateFormat() {
		// TODO we should pre-create SimpleDataFormat objects
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, getPreferredLocale());
		simpleDateFormat.setTimeZone(getPreferredTimeZone());
		return simpleDateFormat;
	}

	public String getPreferredDateFormatPattern() {
		// TODO we should pre-create patterns
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, getPreferredLocale());
		simpleDateFormat.setTimeZone(getPreferredTimeZone());
		return simpleDateFormat.toPattern();
	}

	public SimpleDateFormat getPreferredDateTimeFormat() {
		// TODO we should pre-create SimpleDataFormat objects
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getPreferredLocale());
		simpleDateFormat.setTimeZone(getPreferredTimeZone());
		return simpleDateFormat;
	}

	public String getPreferredDateTimeFormatPattern() {
		// TODO we should pre-create patterns
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getPreferredLocale());
		simpleDateFormat.setTimeZone(getPreferredTimeZone());
		return simpleDateFormat.toPattern();
	}
}
