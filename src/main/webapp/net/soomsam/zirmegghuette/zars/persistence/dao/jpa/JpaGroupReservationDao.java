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
	public List<GroupReservation> findGroupReservation(Interval dateInterval) {
		if (null == dateInterval) {
			throw new IllegalArgumentException("'dateInterval' must not be null");
		}

		Query findGroupReservationQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATION_QUERYNAME);
		findGroupReservationQuery.setParameter("startDate", dateInterval.getStart().toDate(), TemporalType.DATE);
		findGroupReservationQuery.setParameter("endDate", dateInterval.getEnd().toDate(), TemporalType.DATE);
		return findGroupReservationQuery.getResultList();
	}
}
