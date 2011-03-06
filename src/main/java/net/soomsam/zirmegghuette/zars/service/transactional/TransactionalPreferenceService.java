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

	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Autowired
	private PreferenceDao preferenceDao;

	@Autowired
	private UserDao userDao;

	@Override
	public PreferenceBean createPreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final Preference preference = preferenceDao.create(userId, preferenceType, value);
		preferenceDao.persist(preference);
		logger.debug("persisting preference [" + preference + "] for user with id [" + userId + "]");
		return map(preference);
	}

	@Override
	@Transactional(readOnly = true)
	public PreferenceBean findPreference(final long userId, final PreferenceType preferenceType) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final Preference preference = preferenceDao.findByUserIdAndPreferenceType(userId, preferenceType);
		if (null == preference) {
			logger.debug("preference with name [" + preferenceType.getPreferenceName() + "] for user with id [" + userId + "] does not exist");
			return null;
		}

		logger.debug("retrieved preference [" + preference + "] for user with id [" + userId + "]");
		return map(preference);
	}

	@Override
	public PreferenceBean updatePreference(final long userId, final PreferenceType preferenceType, final Object value) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final Preference preference = preferenceDao.update(userId, preferenceType, value);
		logger.debug("updating preference [" + preference + "] for user with id [" + userId + "]");
		return map(preference);
	}

	@Override
	public PreferenceBean findCurrentUserPreference(final PreferenceType preferenceType) {
		final User currentUser = userDao.retrieveCurrentUser();
		return findPreference(currentUser.getUserId(), preferenceType);
	}

	@Override
	public PreferenceBean updateCurrentUserPreference(final PreferenceType preferenceType, final Object value) {
		final User currentUser = userDao.retrieveCurrentUser();
		return updatePreference(currentUser.getUserId(), preferenceType, value);
	}

	protected PreferenceBean map(final Preference preference) {
		if (!preference.hasValue()) {
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), Object.class);
		}

		try {
			final Class<?> preferenceValueType = Class.forName(preference.getType());
			final Object preferenceValue = beanWrapper.convertIfNecessary(preference.getValue(), preferenceValueType);
			return new PreferenceBean(preference.getPreferenceId(), preference.getPreferenceTimestamp(), PreferenceType.valueOf(preference.getName()), preferenceValue, preferenceValueType);
		} catch (final ClassNotFoundException classNotFoundException) {
			throw new ServiceException("converting value for preference [" + preference + "] failed", classNotFoundException);
		}
	}
}