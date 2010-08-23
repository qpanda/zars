package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;

import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

@Repository("groupReservationDao")
public class JpaGroupReservationDao extends JpaEntityDao<GroupReservation> implements GroupReservationDao {
	@Override
	protected Class<GroupReservation> determineEntityClass() {
		return GroupReservation.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GroupReservation> findGroupReservationInclusive(final Interval dateInterval) {
		if (null == dateInterval) {
			throw new IllegalArgumentException("'dateInterval' must not be null");
		}

		final Query findGroupReservationByStartDateEndDateQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONINCLUSIVE_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByStartDateEndDateQuery.setParameter("startDate", dateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByStartDateEndDateQuery.setParameter("endDate", dateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByStartDateEndDateQuery.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<GroupReservation> findGroupReservationExclusive(final Interval dateInterval) {
		if (null == dateInterval) {
			throw new IllegalArgumentException("'dateInterval' must not be null");
		}

		final Query findGroupReservationByStartDateEndDateQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONEXCLUSIVE_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByStartDateEndDateQuery.setParameter("startDate", dateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByStartDateEndDateQuery.setParameter("endDate", dateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByStartDateEndDateQuery.getResultList();
	}
}
