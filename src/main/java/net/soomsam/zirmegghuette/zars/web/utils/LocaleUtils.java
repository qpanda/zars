package net.soomsam.zirmegghuette.zars.web.utils;

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
}
