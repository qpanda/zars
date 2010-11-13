package net.soomsam.zirmegghuette.zars.service.transactional;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.dao.PreferenceDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
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

	@Autowired
	private PreferenceDao preferenceDao;

	@Autowired
	private UserDao userDao;

	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Override
	public PreferenceBean createPreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final User user = userDao.retrieveByPrimaryKey(userId);
		Preference preference;
		if (null == value) {
			preference = new Preference(user, preferenceType.getPreferenceName(), Object.class.getCanonicalName());
		} else {
			String preferenceStringValue = beanWrapper.convertIfNecessary(value, String.class);
			String preferenceObjectType = value.getClass().getCanonicalName();
			preference = new Preference(user, preferenceType.getPreferenceName(), preferenceStringValue, preferenceObjectType);
		}

		preferenceDao.persist(preference);
		logger.debug("persisting preference [" + preference + "]");
		return convert(preference);
	}

	@Override
	@Transactional(readOnly = true)
	public PreferenceBean findPreference(final long userId, final PreferenceType preferenceType) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		Preference preference = preferenceDao.findPreference(userId, preferenceType);
		if (null == preference) {
			logger.debug("preference with name [" + preferenceType + "] does not exist");
			return null;
		}

		logger.debug("retrieved preference [" + preference + "]");
		return convert(preference);
	}

	@Override
	public PreferenceBean updatePreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		Preference preference = preferenceDao.findPreference(userId, preferenceType);
		if (null == preference) {
			throw new ServiceException("preference with name [" + preferenceType + "] does not exist");
		}

		if (null == value) {
			preference.setValue(null);
			preference.setType(Object.class.getCanonicalName());
		} else {
			String preferenceObjectValue = beanWrapper.convertIfNecessary(value, String.class);
			String preferenceObjectType = value.getClass().getCanonicalName();
			preference.setValue(preferenceObjectValue);
			preference.setType(preferenceObjectType);
		}

		logger.debug("updateding preference [" + preference + "]");
		return convert(preference);
	}

	protected PreferenceBean convert(final Preference preference) {
		if (!preference.hasValue()) {
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), Object.class);
		}

		try {
			Class preferenceObjectType = Class.forName(preference.getType());
			Object preferenceObjectValue = beanWrapper.convertIfNecessary(preference.getValue(), preferenceObjectType);
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), preferenceObjectValue, preferenceObjectType);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new ServiceException("converting value for preference [" + preference + "] failed", classNotFoundException);
		}
	}
}