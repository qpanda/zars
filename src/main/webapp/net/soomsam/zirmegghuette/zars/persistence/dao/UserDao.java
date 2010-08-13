package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

public interface UserDao extends EntityDao<User> {
	public void persistUser(User user) throws UniqueConstraintException;
	
	public List<User> findByRoleId(final long roleId);
}
