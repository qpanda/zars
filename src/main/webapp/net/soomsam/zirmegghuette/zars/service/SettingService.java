package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

public interface SettingService {
	public SettingBean createSetting(String name, Object value);

	public SettingBean findSetting(String name);

	public SettingBean updateSetting(String name, Object value);
}
