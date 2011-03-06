package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.Room;

public interface RoomDao extends EntityDao<Room> {
	public List<Room> findByInUse(boolean inUse);
}
