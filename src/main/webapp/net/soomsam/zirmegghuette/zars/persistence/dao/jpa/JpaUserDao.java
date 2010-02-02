package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class JpaUserDao extends JpaEntityDao<User> implements UserDao {
	@Override
	protected Class<User> determineEntityClass() {
		return User.class;
	}
}
