package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.persistence.dao.PreferenceDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;

import org.springframework.stereotype.Repository;

@Repository("preferenceDao")
public class JpaPreferenceDao extends JpaEntityDao<Preference> implements PreferenceDao {
	@Override
	protected Class<Preference> determineEntityClass() {
		return Preference.class;
	}

	@Override
	public Preference findPreference(final long userId, final PreferenceType preferenceType) {
		if (null == preferenceType) {
			throw new IllegalArgumentException("'preferenceType' must not be null");
		}

		final Query findPreferenceQuery = createNamedQuery(Preference.FINDPREFERENCE_QUERYNAME);
		findPreferenceQuery.setParameter("userId", userId);
		findPreferenceQuery.setParameter("name", preferenceType.getPreferenceName());

		try {
			return (Preference)findPreferenceQuery.getSingleResult();
		} catch (NoResultException noResultException) {
			return null;
		}
	}
}