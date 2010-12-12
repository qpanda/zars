package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

@Repository("groupReservationDao")
public class JpaGroupReservationDao extends JpaEntityDao<GroupReservation> implements GroupReservationDao {
	@Override
	protected Class<GroupReservation> determineEntityClass() {
		return GroupReservation.class;
	}

	@Override
	public long countGroupReservationByClosedDateInterval(final Interval closedDateInterval) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query countGroupReservationByClosedDateIntervalQuery = createNamedQuery(GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		countGroupReservationByClosedDateIntervalQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		countGroupReservationByClosedDateIntervalQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return (Long) countGroupReservationByClosedDateIntervalQuery.getSingleResult();
	}

	@Override
	public long countGroupReservationByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query countGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery = createNamedQuery(GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME);
		countGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("beneficiaryId", beneficiaryId);
		countGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		countGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return (Long) countGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.getSingleResult();
	}

	@Override
	public long countGroupReservationByBeneficiaryId(final long beneficiaryId) {
		final Query countGroupReservationByBeneficiaryIdQuery = createNamedQuery(GroupReservation.COUNTGROUPRESERVATION_BENEFICIARYID_QUERYNAME);
		countGroupReservationByBeneficiaryIdQuery.setParameter("beneficiaryId", beneficiaryId);
		return (Long) countGroupReservationByBeneficiaryIdQuery.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GroupReservation> findGroupReservationByClosedDateInterval(final Interval closedDateInterval, final Pagination pagination) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query findGroupReservationByClosedDateIntervalQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByClosedDateIntervalQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByClosedDateIntervalQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);

		if (null != pagination) {
			findGroupReservationByClosedDateIntervalQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByClosedDateIntervalQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByClosedDateIntervalQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findGroupReservationByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval, final Pagination pagination) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("beneficiaryId", beneficiaryId);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);

		if (null != pagination) {
			findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByClosedDateIntervalAndBeneficiaryIdQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findGroupReservationByBeneficiaryId(final long beneficiaryId, final Pagination pagination) {
		final Query findGroupReservationByBeneficiaryIdQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATION_BENEFICIARYID_QUERYNAME);
		findGroupReservationByBeneficiaryIdQuery.setParameter("beneficiaryId", beneficiaryId);

		if (null != pagination) {
			findGroupReservationByBeneficiaryIdQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByBeneficiaryIdQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByBeneficiaryIdQuery.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GroupReservation> findGroupReservationByOpenDateInterval(final Interval openDateInterval) {
		if (null == openDateInterval) {
			throw new IllegalArgumentException("'openDateInterval' must not be null");
		}

		final Query findGroupReservationByOpenDateIntervalQuery = createNamedQuery(GroupReservation.FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByOpenDateIntervalQuery.setParameter("startDate", openDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByOpenDateIntervalQuery.setParameter("endDate", openDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByOpenDateIntervalQuery.getResultList();
	}
}
