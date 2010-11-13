package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;

public interface PreferenceDao extends EntityDao<Preference> {
	public Preference findPreference(long userId, PreferenceType preferenceType);
}
