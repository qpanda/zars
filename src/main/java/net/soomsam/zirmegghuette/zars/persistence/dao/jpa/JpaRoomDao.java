package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

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

	@Override
	public void removeAll(final Set<Room> entitySet) {
		throw new OperationNotSupportedException("[" + JpaRoomDao.class.getSimpleName() + "] does not support operation 'removeAll'");
	}

	@Override
	public List<Room> findByPrecedence(final boolean inUse) {
		final TypedQuery<Room> findRoomInUseByPrecedenceTypedQuery = createNamedTypedQuery(Room.FINDROOM_INUSE_BYPRECEDENCE_QUERYNAME);
		findRoomInUseByPrecedenceTypedQuery.setParameter("inUse", inUse);
		return findRoomInUseByPrecedenceTypedQuery.getResultList();
	}
}
