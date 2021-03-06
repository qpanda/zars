package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
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
@SuppressWarnings("serial")
public class SettingController implements Serializable {
	private final static Logger logger = Logger.getLogger(SettingController.class);

	@Inject
	private transient PreferenceService preferenceService;

	private TimeZone preferredTimeZone;

	private Locale preferredLocale;

	private ThreadLocal<SimpleDateFormat> preferredDateFormat;

	private ThreadLocal<SimpleDateFormat> preferredDateTimeFormat;

	private ThreadLocal<SimpleDateFormat> preferredTimestampFormat;

	public void resetPreferredTimeZone() {
		preferredTimeZone = null;
		preferredDateFormat = null;
		preferredDateTimeFormat = null;
		preferredTimestampFormat = null;
	}

	public synchronized TimeZone getPreferredTimeZone() {
		if (null == preferredTimeZone) {
			preferredTimeZone = determinePreferredTimeZone();
			logger.debug("using preferred timezone [" + preferredTimeZone.getID() + "] rather than default timezone [" + TimeZone.getDefault().getID() + "]");
		}

		return preferredTimeZone;
	}

	protected TimeZone determinePreferredTimeZone() {
		final PreferenceBean currentUserTimezonePreference = preferenceService.findCurrentUserPreference(PreferenceType.TIMEZONE);
		if (null != currentUserTimezonePreference) {
			final String currentUserTimezoneId = (String) currentUserTimezonePreference.getValue();
			return LocaleUtils.determineSupportedTimezone(currentUserTimezoneId);
		}

		return LocaleUtils.determineDefaultTimezone();
	}

	public void resetPreferredLocale() {
		preferredLocale = null;
		preferredDateFormat = null;
		preferredDateTimeFormat = null;
		preferredTimestampFormat = null;
	}

	public synchronized Locale getPreferredLocale() {
		if (null == preferredLocale) {
			preferredLocale = determinePreferredLocale();
			logger.debug("using preferred locale [" + preferredLocale.getDisplayName() + "]");
		}

		return preferredLocale;
	}

	protected Locale determinePreferredLocale() {
		final PreferenceBean currentUserLocalePreference = preferenceService.findCurrentUserPreference(PreferenceType.LOCALE);
		if (null != currentUserLocalePreference) {
			final String currentUserLocaleDisplayName = (String) currentUserLocalePreference.getValue();
			return LocaleUtils.determineSupportedLocale(currentUserLocaleDisplayName);
		}

		return LocaleUtils.determineCurrentLocale();
	}

	public synchronized SimpleDateFormat getPreferredDateFormat() {
		if (null == preferredDateFormat) {
			preferredDateFormat = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected synchronized SimpleDateFormat initialValue() {
					return LocaleUtils.determinePreferredDateFormat(getPreferredLocale(), getPreferredTimeZone());
				}
			};
		}

		return preferredDateFormat.get();
	}

	public String getPreferredDateFormatPattern() {
		return getPreferredDateFormat().toPattern();
	}

	public synchronized SimpleDateFormat getPreferredDateTimeFormat() {
		if (null == preferredDateTimeFormat) {
			preferredDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected synchronized SimpleDateFormat initialValue() {
					return LocaleUtils.determinePreferredDateTimeFormat(getPreferredLocale(), getPreferredTimeZone());
				}
			};
		}

		return preferredDateTimeFormat.get();
	}

	public String getPreferredDateTimeFormatPattern() {
		return getPreferredDateTimeFormat().toPattern();
	}

	public synchronized SimpleDateFormat getPreferredTimestampFormat() {
		if (null == preferredTimestampFormat) {
			preferredTimestampFormat = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected synchronized SimpleDateFormat initialValue() {
					return LocaleUtils.determinePreferredTimestampFormat(getPreferredLocale(), getPreferredTimeZone());					
				}
			};
		}

		return preferredTimestampFormat.get();
	}

	public String getPreferredTimestampFormatPattern() {
		return getPreferredTimestampFormat().toPattern();
	}
}