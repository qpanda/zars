package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.enums.SettingType;
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

public interface SettingService {
	public SettingBean createSetting(SettingType settingType, Object value);

	public SettingBean findSetting(SettingType settingType);

	public SettingBean updateSetting(SettingType settingType, Object value);
}
