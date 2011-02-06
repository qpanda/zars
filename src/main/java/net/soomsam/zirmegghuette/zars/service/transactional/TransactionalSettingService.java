package net.soomsam.zirmegghuette.zars.service.transactional;

import net.soomsam.zirmegghuette.zars.enums.SettingType;
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
	public SettingBean createSetting(final SettingType settingType, final Object value) {
		if (null == settingType) {
			throw new IllegalArgumentException("'settingType' must not be null");
		}

		final Setting setting = settingDao.create(settingType, value);
		settingDao.persist(setting);
		logger.debug("persisting setting [" + setting + "]");
		return map(setting);
	}

	@Override
	public SettingBean findSetting(final SettingType settingType) {
		if (null == settingType) {
			throw new IllegalArgumentException("'settingType' must not be null");
		}

		Setting setting = settingDao.findBySettingType(settingType);
		if (null == setting) {
			logger.debug("setting with name [" + settingType.getSettingName() + "] does not exist");
			return null;
		}

		logger.debug("retrieved setting [" + setting + "]");
		return map(setting);
	}

	@Override
	public SettingBean updateSetting(final SettingType settingType, final Object value) {
		if (null == settingType) {
			throw new IllegalArgumentException("'settingType' must not be null");
		}

		Setting setting = settingDao.update(settingType, value);
		logger.debug("updating setting [" + setting + "]");
		return map(setting);
	}

	protected SettingBean map(final Setting setting) {
		if (!setting.hasValue()) {
			return new SettingBean(setting.getSettingId(), setting.getSettingTimestamp(), SettingType.valueOf(setting.getName()), Object.class);
		}

		try {
			Class<?> settingValueType = Class.forName(setting.getType());
			Object settingValue = beanWrapper.convertIfNecessary(setting.getValue(), settingValueType);
			return new SettingBean(setting.getSettingId(), setting.getSettingTimestamp(), SettingType.valueOf(setting.getName()), settingValue, settingValueType);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new ServiceException("converting value for setting [" + setting + "] failed", classNotFoundException);
		}
	}
}