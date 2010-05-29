package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.persistence.dao.SettingDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;

import org.springframework.stereotype.Repository;

@Repository("settingDao")
public class JpaSettingDao extends JpaEntityDao<Setting> implements SettingDao {
	@Override
	protected Class<Setting> determineEntityClass() {
		return Setting.class;
	}

	@Override
	public Setting findSetting(String name) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		final Query findSettingQuery = createNamedQuery(Setting.FINDSETTING_QUERYNAME);
		findSettingQuery.setParameter("name", name);

		try {
			return (Setting) findSettingQuery.getSingleResult();
		} catch (NoResultException noResultException) {
			return null;
		}
	}
}