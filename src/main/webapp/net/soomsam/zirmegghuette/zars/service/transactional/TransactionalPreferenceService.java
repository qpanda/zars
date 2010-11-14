package net.soomsam.zirmegghuette.zars.service.transactional;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.dao.PreferenceDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.ServiceException;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("preferenceService")
@Transactional(timeout = 1000)
public class TransactionalPreferenceService implements PreferenceService {
	private final static Logger logger = Logger.getLogger(TransactionalPreferenceService.class);

	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Autowired
	private PreferenceDao preferenceDao;

	@Override
	public PreferenceBean createPreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final Preference preference = preferenceDao.create(userId, preferenceType, value);
		preferenceDao.persist(preference);
		logger.debug("persisting preference [" + preference + "]");
		return map(preference);
	}

	@Override
	@Transactional(readOnly = true)
	public PreferenceBean findPreference(final long userId, final PreferenceType preferenceType) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		Preference preference = preferenceDao.findByUserIdAndPreferenceType(userId, preferenceType);
		if (null == preference) {
			logger.debug("preference with name [" + preferenceType + "] does not exist");
			return null;
		}

		logger.debug("retrieved preference [" + preference + "]");
		return map(preference);
	}

	@Override
	public PreferenceBean updatePreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		Preference preference = preferenceDao.update(userId, preferenceType, value);
		logger.debug("updateding preference [" + preference + "]");
		return map(preference);
	}

	protected PreferenceBean map(final Preference preference) {
		if (!preference.hasValue()) {
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), Object.class);
		}

		try {
			Class preferenceValueType = Class.forName(preference.getType());
			Object preferenceValue = beanWrapper.convertIfNecessary(preference.getValue(), preferenceValueType);
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), preferenceValue, preferenceValueType);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new ServiceException("converting value for preference [" + preference + "] failed", classNotFoundException);
		}
	}
}