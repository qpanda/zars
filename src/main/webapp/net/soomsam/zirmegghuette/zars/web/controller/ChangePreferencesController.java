package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class ChangePreferencesController implements Serializable {
	private final static Logger logger = Logger.getLogger(ChangePreferencesController.class);

	@Inject
	private transient PreferenceService preferenceService;

	@Inject
	private transient SecurityController securityController;

	private List<TimeZone> availableTimezones;

	private String selectedTimezone;

	public List<TimeZone> getAvailableTimezones() {
		if (null == availableTimezones) {
			availableTimezones = LocaleUtils.determineSupportedTimezoneList();
		}

		return availableTimezones;
	}

	public String getSelectedTimezone() {
		return selectedTimezone;
	}

	public void setSelectedTimezone(final String selectedTimezone) {
		this.selectedTimezone = selectedTimezone;
	}

	public void retrievePreferences() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		final PreferenceBean timezonePreferenceBean = preferenceService.findCurrentUserPreference(PreferenceType.TIMEZONE);
		this.selectedTimezone = (String)timezonePreferenceBean.getValue();
	}

	public String update() {
		logger.debug("updating preferences of current user [" + securityController.getCurrentUserId() + "]");
		preferenceService.updateCurrentUserPreference(PreferenceType.TIMEZONE, selectedTimezone);
		return "changePreferencesConfirmation";
	}
}