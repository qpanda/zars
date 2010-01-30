package net.soomsam.zirmegghuette.persistence.dao.jpa;

import net.soomsam.zirmegghuette.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.persistence.entity.User;

import org.springframework.stereotype.Repository;

@Repository("userDao")
public class JpaUserDao extends JpaBaseDao<User> implements UserDao {
	@Override
	protected Class<User> determineEntityClass() {
		return User.class;
	}
}
