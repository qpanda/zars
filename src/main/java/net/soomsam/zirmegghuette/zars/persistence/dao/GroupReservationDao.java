package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;

public interface GroupReservationDao extends EntityDao<GroupReservation> {
	public long countByClosedDateInterval(final Interval closedDateInterval);

	public long countByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval);

	public long countByBeneficiaryId(final long beneficiaryId);

	public List<GroupReservation> findByClosedDateInterval(final Interval closedDateInterval, final Pagination pagination);

	public List<GroupReservation> findByClosedDateIntervalAndBeneficiaryId(long beneficiaryId, Interval closedDateInterval, Pagination pagination);

	public List<GroupReservation> findByBeneficiaryId(long beneficiaryId, Pagination pagination);

	public List<GroupReservation> findByOpenDateInterval(final Interval openDateInterval);
}
