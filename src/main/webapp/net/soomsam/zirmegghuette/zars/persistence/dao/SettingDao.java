package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.enums.SettingType;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;

public interface SettingDao extends EntityDao<Setting> {
	public Setting findBySettingType(final SettingType settingType);

	public Setting create(final SettingType settingType, final Object value);

	public Setting update(final SettingType settingType, final Object value);
}
