package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;

import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("groupReservationService")
@Transactional(timeout = 1000)
public class TransactionalGroupReservationService implements GroupReservationService {
	@Autowired
	private GroupReservationDao groupReservationDao;

	@Override
	public List<GroupReservation> findGroupReservation(Interval dateInterval) {
		return groupReservationDao.findGroupReservation(dateInterval);
	}
}
