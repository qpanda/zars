package net.soomsam.zirmegghuette.persistence.dao.jpa;

import net.soomsam.zirmegghuette.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.persistence.entity.Room;

import org.springframework.stereotype.Repository;

@Repository("roomDao")
public class JpaRoomDao extends JpaBaseDao<Room> implements RoomDao {
	@Override
	protected Class<Room> determineEntityClass() {
		return Room.class;
	}
}
