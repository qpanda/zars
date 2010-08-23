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
	public List<GroupReservation> findGroupReservationByClosedDateInterval(final Interval closedDateInterval) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query findGroupReservationByClosedStartEndIntervalQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByClosedStartEndIntervalQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByClosedStartEndIntervalQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByClosedStartEndIntervalQuery.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<GroupReservation> findGroupReservationByOpenDateInterval(final Interval openDateInterval) {
		if (null == openDateInterval) {
			throw new IllegalArgumentException("'openDateInterval' must not be null");
		}

		final Query findGroupReservationByOpenStartEndIntervalQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByOpenStartEndIntervalQuery.setParameter("startDate", openDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByOpenStartEndIntervalQuery.setParameter("endDate", openDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByOpenStartEndIntervalQuery.getResultList();
	}
}
