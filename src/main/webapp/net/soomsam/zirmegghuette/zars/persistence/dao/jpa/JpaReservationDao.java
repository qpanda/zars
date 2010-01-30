package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.ReservationDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Reservation;

import org.springframework.stereotype.Repository;

@Repository("reservationDao")
public class JpaReservationDao extends JpaBaseDao<Reservation> implements ReservationDao {
	@Override
	protected Class<Reservation> determineEntityClass() {
		return Reservation.class;
	}
}
