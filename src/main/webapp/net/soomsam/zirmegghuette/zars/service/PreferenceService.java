package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;

public interface PreferenceService {
	public PreferenceBean createPreference(long userId, PreferenceType preferenceType, Object value);

	public PreferenceBean findPreference(long userId, PreferenceType preferenceType);

	public PreferenceBean updatePreference(long userId, PreferenceType preferenceType, Object value);
}
