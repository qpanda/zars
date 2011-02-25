package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.dao.PreferenceDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("preferenceDao")
public class JpaPreferenceDao extends JpaEntityDao<Preference> implements PreferenceDao {
	@Autowired
	private UserDao userDao;

	private final BeanWrapper beanWrapper = new BeanWrapperImpl();

	@Override
	protected Class<Preference> determineEntityClass() {
		return Preference.class;
	}

	@Override
	public Preference findByUserIdAndPreferenceType(final long userId, final PreferenceType preferenceType) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final TypedQuery<Preference> findPreferenceTypedQuery = createNamedTypedQuery(Preference.FINDPREFERENCE_QUERYNAME);
		findPreferenceTypedQuery.setParameter("userId", userId);
		findPreferenceTypedQuery.setParameter("name", preferenceType.getPreferenceName());

		try {
			return findPreferenceTypedQuery.getSingleResult();
		} catch (final NoResultException noResultException) {
			return null;
		}
	}

	@Override
	public Preference create(final long userId, final PreferenceType preferenceType, final Object value) {
		final User user = userDao.retrieveByPrimaryKey(userId);

		if (null == value) {
			return new Preference(user, preferenceType.getPreferenceName(), Object.class.getCanonicalName());
		}

		final String preferenceValue = beanWrapper.convertIfNecessary(value, String.class);
		final String preferenceValueType = value.getClass().getCanonicalName();
		return new Preference(user, preferenceType.getPreferenceName(), preferenceValue, preferenceValueType);
	}

	@Override
	public Preference update(final long userId, final PreferenceType preferenceType, final Object value) {
		final Preference preference = findByUserIdAndPreferenceType(userId, preferenceType);
		if (null == preference) {
			throw new EntityNotFoundException("preference [" + preferenceType.getPreferenceName() + "] for user with id [" + userId + "] does not exist");
		}

		if (null == value) {
			preference.setValue(null);
			preference.setType(Object.class.getCanonicalName());
		} else {
			final String preferenceValue = beanWrapper.convertIfNecessary(value, String.class);
			final String preferenceValueType = value.getClass().getCanonicalName();
			preference.setValue(preferenceValue);
			preference.setType(preferenceValueType);
		}

		return preference;
	}
}