package net.soomsam.zirmegghuette.persistence.dao.jpa;

import net.soomsam.zirmegghuette.persistence.dao.GroupReservationDao;
import net.soomsam.zirmegghuette.persistence.entity.GroupReservation;

import org.springframework.stereotype.Repository;

@Repository("groupReservationDao")
public class JpaGroupReservationDao extends JpaBaseDao<GroupReservation> implements GroupReservationDao {
	@Override
	protected Class<GroupReservation> determineEntityClass() {
		return GroupReservation.class;
	}
}
