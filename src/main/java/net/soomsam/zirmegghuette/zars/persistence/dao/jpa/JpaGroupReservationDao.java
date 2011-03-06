package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

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
	public long countByClosedDateInterval(final Interval closedDateInterval) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final Query countGroupReservationByClosedDateIntervalQuery = createNamedQuery(GroupReservation.COUNTGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		countGroupReservationByClosedDateIntervalQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		countGroupReservationByClosedDateIntervalQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return (Long) countGroupReservationByClosedDateIntervalQuery.getSingleResult();
	}

	@Override
	public long countByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval) {
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
	public long countByBeneficiaryId(final long beneficiaryId) {
		final Query countGroupReservationByBeneficiaryIdQuery = createNamedQuery(GroupReservation.COUNTGROUPRESERVATION_BENEFICIARYID_QUERYNAME);
		countGroupReservationByBeneficiaryIdQuery.setParameter("beneficiaryId", beneficiaryId);
		return (Long) countGroupReservationByBeneficiaryIdQuery.getSingleResult();
	}

	@Override
	public List<GroupReservation> findAll() {
		return findAll(null);
	}

	@Override
	public List<GroupReservation> findAll(final Pagination pagination) {
		final TypedQuery<GroupReservation> findGroupReservationTypedQuery = createNamedTypedQuery(GroupReservation.FINDGROUPRESERVATION_QUERYNAME);

		if (null != pagination) {
			findGroupReservationTypedQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationTypedQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findByClosedDateInterval(final Interval closedDateInterval, final Pagination pagination) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final TypedQuery<GroupReservation> findGroupReservationByClosedDateIntervalTypedQuery = createNamedTypedQuery(GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByClosedDateIntervalTypedQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByClosedDateIntervalTypedQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);

		if (null != pagination) {
			findGroupReservationByClosedDateIntervalTypedQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByClosedDateIntervalTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByClosedDateIntervalTypedQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval, final Pagination pagination) {
		if (null == closedDateInterval) {
			throw new IllegalArgumentException("'closedDateInterval' must not be null");
		}

		final TypedQuery<GroupReservation> findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery = createNamedTypedQuery(GroupReservation.FINDGROUPRESERVATIONCLOSEDINTERVAL_STARTDATE_ENDDATE_BENEFICIARYID_QUERYNAME);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.setParameter("beneficiaryId", beneficiaryId);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.setParameter("startDate", closedDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.setParameter("endDate", closedDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);

		if (null != pagination) {
			findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByClosedDateIntervalAndBeneficiaryIdTypedQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findByBeneficiaryId(final long beneficiaryId, final Pagination pagination) {
		final TypedQuery<GroupReservation> findGroupReservationByBeneficiaryIdTypedTypedQuery = createNamedTypedQuery(GroupReservation.FINDGROUPRESERVATION_BENEFICIARYID_QUERYNAME);
		findGroupReservationByBeneficiaryIdTypedTypedQuery.setParameter("beneficiaryId", beneficiaryId);

		if (null != pagination) {
			findGroupReservationByBeneficiaryIdTypedTypedQuery.setFirstResult(pagination.getFirstResult());
			findGroupReservationByBeneficiaryIdTypedTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findGroupReservationByBeneficiaryIdTypedTypedQuery.getResultList();
	}

	@Override
	public List<GroupReservation> findByOpenDateInterval(final Interval openDateInterval) {
		if (null == openDateInterval) {
			throw new IllegalArgumentException("'openDateInterval' must not be null");
		}

		final TypedQuery<GroupReservation> findGroupReservationByOpenDateIntervalQuery = createNamedTypedQuery(GroupReservation.FINDGROUPRESERVATIONOPENINTERVAL_STARTDATE_ENDDATE_QUERYNAME);
		findGroupReservationByOpenDateIntervalQuery.setParameter("startDate", openDateInterval.getStart().toDateMidnight().toDate(), TemporalType.DATE);
		findGroupReservationByOpenDateIntervalQuery.setParameter("endDate", openDateInterval.getEnd().toDateMidnight().toDate(), TemporalType.DATE);
		return findGroupReservationByOpenDateIntervalQuery.getResultList();
	}
}
