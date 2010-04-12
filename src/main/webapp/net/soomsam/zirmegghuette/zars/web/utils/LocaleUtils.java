package net.soomsam.zirmegghuette.zars.web.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class LocaleUtils {
	public static Locale determineCurrentLocale() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return null;
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		if (null == externalContext) {
			return null;
		}

		return externalContext.getRequestLocale();
	}

	public static List<Locale> determineSupportedLocaleList() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return new ArrayList<Locale>();
		}

		Application application = facesContext.getApplication();
		if (null == application) {
			return new ArrayList<Locale>();
		}

		Iterator<Locale> supportedLocaleIterator = application.getSupportedLocales();
		List<Locale> supportedLocaleList = new ArrayList<Locale>();
		while (supportedLocaleIterator.hasNext()) {
			supportedLocaleList.add(supportedLocaleIterator.next());
		}
		return supportedLocaleList;
	}

	public static Map<String, Locale> determineSupportedLocaleDisplayLanguageMap() {
		List<Locale> supportedLocaleList = determineSupportedLocaleList();
		Map<String, Locale> supportedLocaleDisplayLanguageMap = new HashMap<String, Locale>();
		for (Locale supportedLocale : supportedLocaleList) {
			supportedLocaleDisplayLanguageMap.put(supportedLocale.getDisplayLanguage(), supportedLocale);
		}
		return supportedLocaleDisplayLanguageMap;
	}

	public static void changeLocale(Locale locale) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null != facesContext) {
			UIViewRoot uiViewRoot = facesContext.getViewRoot();
			if (null != uiViewRoot) {
				uiViewRoot.setLocale(locale);
			}
		}
	}
}