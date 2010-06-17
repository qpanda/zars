package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.PersistenceException;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class JpaUserDao extends JpaEntityDao<User> implements UserDao {
	@Override
	protected Class<User> determineEntityClass() {
		return User.class;
	}

	@Override
	public void remove(final User entity) {
		throw new OperationNotSupportedException("[" + JpaUserDao.class.getSimpleName() + "] does not support operation 'remove'");
	}

	@Override
	public void createUser(final User user) throws UniqueConstraintException {
		try {
			super.persist(user);
		} catch (final PersistenceException persistenceException) {
			final Throwable persistenceExceptionCause = persistenceException.getCause();
			if (persistenceExceptionCause instanceof ConstraintViolationException) {
				final ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceExceptionCause;
				final String uniqueConstraintName = constraintViolationException.getConstraintName();
				if (StringUtils.containsIgnoreCase(uniqueConstraintName, User.COLUMNNAME_USERNAME) || StringUtils.containsIgnoreCase(uniqueConstraintName, User.COLUMNNAME_EMAILADDRESS)) {
					throw new UniqueConstraintException("unable to create user [" + user + "], unique constraint [" + uniqueConstraintName + "] violated", uniqueConstraintName, constraintViolationException);
				}
			}

			throw persistenceException;
		}
	}
}
