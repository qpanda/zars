package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

public interface SettingService {
	public void createSetting(String name, Object value);

	public SettingBean findSetting(String name);

	public void updateSetting(String name, Object value);
}
