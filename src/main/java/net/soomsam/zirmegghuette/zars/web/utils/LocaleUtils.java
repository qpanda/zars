package net.soomsam.zirmegghuette.zars.web.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class LocaleUtils {
	public static Locale determineCurrentLocale() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return null;
		}

		final ExternalContext externalContext = facesContext.getExternalContext();
		if (null == externalContext) {
			return null;
		}

		return externalContext.getRequestLocale();
	}

	public static List<Locale> determineSupportedLocaleList() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return new ArrayList<Locale>();
		}

		final Application application = facesContext.getApplication();
		if (null == application) {
			return new ArrayList<Locale>();
		}

		final Iterator<Locale> supportedLocaleIterator = application.getSupportedLocales();
		final List<Locale> supportedLocaleList = new ArrayList<Locale>();
		while (supportedLocaleIterator.hasNext()) {
			supportedLocaleList.add(supportedLocaleIterator.next());
		}
		return supportedLocaleList;
	}

	public static Map<String, Locale> determineSupportedLocaleDisplayLanguageMap() {
		final List<Locale> supportedLocaleList = determineSupportedLocaleList();
		final Map<String, Locale> supportedLocaleDisplayLanguageMap = new HashMap<String, Locale>();
		for (final Locale supportedLocale : supportedLocaleList) {
			supportedLocaleDisplayLanguageMap.put(supportedLocale.getDisplayLanguage(), supportedLocale);
		}
		return supportedLocaleDisplayLanguageMap;
	}

	public static Locale determineSupportedLocale(final String localeDisplayName) {
		final List<Locale> supportedLocaleList = determineSupportedLocaleList();
		for (final Locale supportedLocale : supportedLocaleList) {
			if (supportedLocale.getDisplayName().equals(localeDisplayName)) {
				return supportedLocale;
			}
		}

		return determineCurrentLocale();
	}
	
	public static String determineLocaleDisplayName(Locale preferredLocale, Locale locale) {
		return locale.getDisplayName(preferredLocale);
	}

	public static void changeLocale(final String localeDisplayName) {
		final Locale locale = determineSupportedLocale(localeDisplayName);
		changeLocale(locale);
	}

	public static void changeLocale(final Locale locale) {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null != facesContext) {
			final UIViewRoot uiViewRoot = facesContext.getViewRoot();
			if (null != uiViewRoot) {
				uiViewRoot.setLocale(locale);
			}
		}
	}

	public static TimeZone determineDefaultTimezone() {
		return TimeZone.getDefault();
	}
	
	public static TimeZone determineSupportedTimezone(String timezoneId) {
		return TimeZone.getTimeZone(timezoneId);
	}
	
	public static String determineTimezoneDisplayName(Locale preferredLocale, TimeZone timezone) {
		return timezone.getID() + " - " + timezone.getDisplayName(preferredLocale);
	}

	public static List<TimeZone> determineSupportedTimezoneList() {
		final String supportedTimezoneIdPrefix = "^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*";
		final List<TimeZone> supportedTimezoneList = new ArrayList<TimeZone>();

		for (final String availableTimezoneId : TimeZone.getAvailableIDs()) {
			if (availableTimezoneId.matches(supportedTimezoneIdPrefix)) {
				supportedTimezoneList.add(TimeZone.getTimeZone(availableTimezoneId));
			}
		}

		Collections.sort(supportedTimezoneList, new Comparator<TimeZone>() {
			@Override
			public int compare(final TimeZone timezoneA, final TimeZone timezoneB) {
				return timezoneA.getID().compareTo(timezoneB.getID());
			}
		});

		return supportedTimezoneList;
	}
	
	public static SimpleDateFormat determinePreferredDateFormat(Locale preferredLocale, TimeZone preferredTimeZone) {
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, preferredLocale);
		simpleDateFormat.setTimeZone(preferredTimeZone);
		return simpleDateFormat;		
	}
	
	public static SimpleDateFormat determinePreferredDateTimeFormat(Locale preferredLocale, TimeZone preferredTimeZone) {
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, preferredLocale);
		simpleDateFormat.setTimeZone(preferredTimeZone);
		return simpleDateFormat;
	}
	
	public static SimpleDateFormat determinePreferredTimeFormat(Locale preferredLocale, TimeZone preferredTimeZone) {
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, preferredLocale);
		simpleDateFormat.setTimeZone(preferredTimeZone);
		return simpleDateFormat;
	}

	public static SimpleDateFormat determinePreferredTimestampFormat(Locale preferredLocale, TimeZone preferredTimeZone) {
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, preferredLocale);
		simpleDateFormat.setTimeZone(preferredTimeZone);
		return simpleDateFormat;
	}
}
