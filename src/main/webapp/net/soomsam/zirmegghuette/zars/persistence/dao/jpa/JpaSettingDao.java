package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.SettingDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;

import org.springframework.stereotype.Repository;

@Repository("settingDao")
public class JpaSettingDao extends JpaEntityDao<Setting> implements SettingDao {
	@Override
	protected Class<Setting> determineEntityClass() {
		return Setting.class;
	}
}