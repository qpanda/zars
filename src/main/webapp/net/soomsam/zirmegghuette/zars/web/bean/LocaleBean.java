package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;
import net.soomsam.zirmegghuette.zars.web.utils.SessionUtils;

import org.apache.log4j.Logger;

@Named
@SessionScoped
public class LocaleBean implements Serializable {
	private final static Logger logger = Logger.getLogger(LocaleBean.class);

	private String selectedLocale = null;

	public LocaleBean() {
		super();
	}

	public String getSelectedLocale() {
		return selectedLocale;
	}

	public void setSelectedLocale(String selectedLocale) {
		this.selectedLocale = selectedLocale;
	}

	public List<SelectItem> getSelectLocales() {
		Locale activeLocale = getActiveLocale();
		List<Locale> supportedLocaleList = LocaleUtils.determineSupportedLocaleList();
		List<SelectItem> selectLocaleItemList = new ArrayList<SelectItem>();
		for (Locale supportedLocale : supportedLocaleList) {
			selectLocaleItemList.add(new SelectItem(supportedLocale.getDisplayLanguage(), supportedLocale.getDisplayLanguage(activeLocale)));
		}
		return selectLocaleItemList;
	}

	public Locale getActiveLocale() {
		if (null == selectedLocale) {
			return LocaleUtils.determineCurrentLocale();
		}

		Map<String, Locale> supportedLocaleDisplayLanguageMap = LocaleUtils.determineSupportedLocaleDisplayLanguageMap();
		if (supportedLocaleDisplayLanguageMap.containsKey(selectedLocale)) {
			return supportedLocaleDisplayLanguageMap.get(selectedLocale);
		}

		return LocaleUtils.determineCurrentLocale();
	}

	public String changeLocale() {
		Locale activeLocale = getActiveLocale();
		LocaleUtils.changeLocale(activeLocale);
		logger.debug("changed locale to [" + activeLocale + "] for session [" + SessionUtils.determineSessionId() + "]");
		return "tterms";
	}
}
