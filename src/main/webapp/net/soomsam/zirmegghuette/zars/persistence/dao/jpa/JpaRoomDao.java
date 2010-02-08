package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoomDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;

import org.springframework.stereotype.Repository;

@Repository("roomDao")
public class JpaRoomDao extends JpaEntityDao<Room> implements RoomDao {
	@Override
	protected Class<Room> determineEntityClass() {
		return Room.class;
	}

	@Override
	public void remove(final Room entity) {
		throw new OperationNotSupportedException("[" + JpaRoomDao.class.getSimpleName() + "] does not support operation 'remove'");
	}
}
