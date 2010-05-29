package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;

public interface SettingDao extends EntityDao<Setting> {
	public Setting findSetting(String name);
}
