package net.soomsam.zirmegghuette.zars.service.transactional;

import net.soomsam.zirmegghuette.zars.persistence.dao.SettingDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Setting;
import net.soomsam.zirmegghuette.zars.service.ServiceException;
import net.soomsam.zirmegghuette.zars.service.SettingService;
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("settingService")
@Transactional(timeout = 1000)
public class TransactionalSettingService implements SettingService {
	private final static Logger logger = Logger.getLogger(TransactionalSettingService.class);

	@Autowired
	private SettingDao settingDao;

	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Override
	public SettingBean createSetting(final String name, final Object value) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		Setting setting;
		if (null == value) {
			setting = new Setting(name, Object.class.getCanonicalName());
		} else {
			String settingValue = beanWrapper.convertIfNecessary(value, String.class);
			String settingType = value.getClass().getCanonicalName();
			setting = new Setting(name, settingValue, settingType);
		}

		settingDao.persist(setting);
		logger.debug("persisting setting [" + setting + "]");
		return convert(setting);
	}

	@Override
	@Transactional(readOnly = true)
	public SettingBean findSetting(final String name) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		Setting setting = settingDao.findSetting(name);
		if (null == setting) {
			logger.debug("setting with name [" + name + "] does not exist");
			return null;
		}

		logger.debug("retrieved setting [" + setting + "]");
		return convert(setting);
	}

	@Override
	public SettingBean updateSetting(final String name, final Object value) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		Setting setting = settingDao.findSetting(name);
		if (null == setting) {
			throw new ServiceException("setting with name [" + name + "] does not exist");
		}

		if (null == value) {
			setting.setValue(null);
			setting.setType(Object.class.getCanonicalName());
		} else {
			String settingValue = beanWrapper.convertIfNecessary(value, String.class);
			String settingType = value.getClass().getCanonicalName();
			setting.setValue(settingValue);
			setting.setType(settingType);
		}

		logger.debug("updateding setting [" + setting + "]");
		return convert(setting);
	}

	protected SettingBean convert(final Setting setting) {
		if (!setting.hasValue()) {
			return new SettingBean(setting.getSettingId(), setting.getSettingTimestamp(), setting.getName(), Object.class);
		}

		try {
			Class settingType = Class.forName(setting.getType());
			Object settingValue = beanWrapper.convertIfNecessary(setting.getValue(), settingType);
			return new SettingBean(setting.getSettingId(), setting.getSettingTimestamp(), setting.getName(), settingValue, settingType);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new ServiceException("converting value for setting [" + setting + "] failed", classNotFoundException);
		}
	}
}