package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.web.utils.JsfContextUtils;

import org.apache.log4j.Logger;

@Named
@SessionScoped
public class LocaleBean implements Serializable {
	private final static Logger logger = Logger.getLogger(LocaleBean.class);

	private String selectedLocale = null;

	private Map<String, Locale> supportedLocaleMap = null;

	public LocaleBean() {
		super();
	}

	protected synchronized void determineSupportedLocales() {
		if (null == supportedLocaleMap) {
			supportedLocaleMap = new HashMap<String, Locale>();
			Iterator<Locale> supportedLocaleIterator = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
			while (supportedLocaleIterator.hasNext()) {
				Locale supportedLocale = supportedLocaleIterator.next();
				supportedLocaleMap.put(supportedLocale.getDisplayLanguage(), supportedLocale);
			}
		}
	}

	public String getSelectedLocale() {
		return selectedLocale;
	}

	public void setSelectedLocale(String selectedLocale) {
		this.selectedLocale = selectedLocale;
	}

	public List<SelectItem> getSelectLocales() {
		determineSupportedLocales();

		List<SelectItem> selectLocaleItemList = new ArrayList<SelectItem>();
		Set<String> supportedLocaleDisplayLanguageSet = supportedLocaleMap.keySet();
		for (String supportedLocaleDisplayLanguage : supportedLocaleDisplayLanguageSet) {
			selectLocaleItemList.add(new SelectItem(supportedLocaleDisplayLanguage));
		}
		return selectLocaleItemList;
	}

	public Locale getActiveLocale() {
		if ((null == selectedLocale) || (null == supportedLocaleMap)) {
			return FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
		}

		return supportedLocaleMap.get(selectedLocale);
	}

	public String select() {
		Locale activeLocale = getActiveLocale();
		logger.debug("changing locale to [" + activeLocale + "] for session [" + JsfContextUtils.determineSessionId() + "]");
		FacesContext.getCurrentInstance().getViewRoot().setLocale(activeLocale);
		return "tterms";
	}
}
