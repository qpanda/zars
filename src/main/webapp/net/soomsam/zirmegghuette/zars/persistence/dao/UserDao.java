package net.soomsam.zirmegghuette.zars.persistence.dao;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

public interface UserDao extends EntityDao<User> {
	public void createUser(User user) throws UniqueConstraintException;
}
