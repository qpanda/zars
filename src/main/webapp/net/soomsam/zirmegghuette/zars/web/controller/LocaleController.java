package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;
import net.soomsam.zirmegghuette.zars.web.utils.SessionUtils;

import org.apache.log4j.Logger;
import org.primefaces.component.commandlink.CommandLink;

@Named
@SessionScoped
public class LocaleController implements Serializable {
	private final static Logger logger = Logger.getLogger(LocaleController.class);

	private final String commandLinkSelectLocaleAttributeName = "selectLocale";

	private String selectedLocale = null;

	public LocaleController() {
		super();
	}

	public String getCommandLinkSelectLocaleAttributeName() {
		return commandLinkSelectLocaleAttributeName;
	}

	public String getSelectedLocale() {
		return selectedLocale;
	}

	public void setSelectedLocale(final String selectedLocale) {
		this.selectedLocale = selectedLocale;
		logger.debug("set locale to [" + selectedLocale + "] for session [" + SessionUtils.determineSessionId() + "]");
	}

	public void setSelectedLocale(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink) commandLinkActionEvent.getComponent();
			final String commandLinkParameterValue = (String) commandLink.getAttributes().get(commandLinkSelectLocaleAttributeName);
			setSelectedLocale(commandLinkParameterValue);
		}
	}

	public List<SelectItem> getSelectLocales() {
		final Locale activeLocale = getActiveLocale();
		final List<Locale> supportedLocaleList = LocaleUtils.determineSupportedLocaleList();
		final List<SelectItem> selectLocaleItemList = new ArrayList<SelectItem>();
		for (final Locale supportedLocale : supportedLocaleList) {
			selectLocaleItemList.add(new SelectItem(supportedLocale.getDisplayLanguage(), supportedLocale.getDisplayLanguage(activeLocale)));
		}
		return selectLocaleItemList;
	}

	public Locale getActiveLocale() {
		if (null == selectedLocale) {
			return LocaleUtils.determineCurrentLocale();
		}

		final Map<String, Locale> supportedLocaleDisplayLanguageMap = LocaleUtils.determineSupportedLocaleDisplayLanguageMap();
		if (supportedLocaleDisplayLanguageMap.containsKey(selectedLocale)) {
			return supportedLocaleDisplayLanguageMap.get(selectedLocale);
		}

		return LocaleUtils.determineCurrentLocale();
	}

	public String changeLocale() {
		final Locale activeLocale = getActiveLocale();
		LocaleUtils.changeLocale(activeLocale);
		logger.debug("changed locale to [" + activeLocale + "] for session [" + SessionUtils.determineSessionId() + "]");
		return SessionUtils.determineRequestServletPath();
	}
}
