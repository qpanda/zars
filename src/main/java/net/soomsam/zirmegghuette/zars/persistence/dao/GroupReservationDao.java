package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.Interval;

public interface GroupReservationDao extends EntityDao<GroupReservation> {
	public long countGroupReservationByClosedDateInterval(final Interval closedDateInterval);

	public long countGroupReservationByClosedDateIntervalAndBeneficiaryId(final long beneficiaryId, final Interval closedDateInterval);

	public long countGroupReservationByBeneficiaryId(final long beneficiaryId);

	public List<GroupReservation> findGroupReservationByClosedDateInterval(final Interval closedDateInterval, final Pagination pagination);

	public List<GroupReservation> findGroupReservationByClosedDateIntervalAndBeneficiaryId(long beneficiaryId, Interval closedDateInterval, Pagination pagination);

	public List<GroupReservation> findGroupReservationByBeneficiaryId(long beneficiaryId, Pagination pagination);

	public List<GroupReservation> findGroupReservationByOpenDateInterval(final Interval openDateInterval);
}
