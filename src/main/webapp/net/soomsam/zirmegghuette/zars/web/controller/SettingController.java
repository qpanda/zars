package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
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

	public TimeZone getPreferredTimeZone() {
		if (null == preferenceService) {
			preferredTimeZone = determinePreferredTimeZone();
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
}
