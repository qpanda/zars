package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;

public interface PreferenceDao extends EntityDao<Preference> {
	public Preference findByUserIdAndPreferenceType(final long userId, final PreferenceType preferenceType);

	public Preference create(final long userId, final PreferenceType preferenceType, final Object value);

	public Preference update(final long userId, final PreferenceType preferenceType, final Object value);
}
