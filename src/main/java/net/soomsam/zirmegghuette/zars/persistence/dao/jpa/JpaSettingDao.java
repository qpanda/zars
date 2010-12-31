package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.enums.SettingType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.dao.SettingDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Repository;

@Repository("settingDao")
public class JpaSettingDao extends JpaEntityDao<Setting> implements SettingDao {
	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Override
	protected Class<Setting> determineEntityClass() {
		return Setting.class;
	}

	@Override
	public Setting findBySettingType(final SettingType settingType) {
		if (null == settingType) {
			throw new IllegalArgumentException("'settingType' must not be null");
		}

		final Query findSettingQuery = createNamedQuery(Setting.FINDSETTING_QUERYNAME);
		findSettingQuery.setParameter("name", settingType.getSettingName());

		try {
			return (Setting)findSettingQuery.getSingleResult();
		} catch (NoResultException noResultException) {
			return null;
		}

	}

	@Override
	public Setting create(final SettingType settingType, final Object value) {
		if (null == value) {
			return new Setting(settingType.getSettingName(), Object.class.getCanonicalName());
		}

		String settingValue = beanWrapper.convertIfNecessary(value, String.class);
		String settingValueType = value.getClass().getCanonicalName();
		return new Setting(settingType.getSettingName(), settingValue, settingValueType);

	}

	@Override
	public Setting update(final SettingType settingType, final Object value) {
		Setting setting = findBySettingType(settingType);
		if (null == setting) {
			throw new EntityNotFoundException("setting [" + settingType.getSettingName() + "] does not exist");
		}

		if (null == value) {
			setting.setValue(null);
			setting.setType(Object.class.getCanonicalName());
		} else {
			String settingValue = beanWrapper.convertIfNecessary(value, String.class);
			String settingValueType = value.getClass().getCanonicalName();
			setting.setValue(settingValue);
			setting.setType(settingValueType);
		}

		return setting;
	}
}