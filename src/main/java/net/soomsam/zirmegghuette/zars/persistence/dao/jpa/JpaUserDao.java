package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.dao.PersistenceContextManager;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.utils.Pagination;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class JpaUserDao extends JpaEntityDao<User> implements UserDao {
	@Autowired
	private PersistenceContextManager persistenceContextManager;

	@Override
	protected Class<User> determineEntityClass() {
		return User.class;
	}

	@Override
	public void remove(final User entity) {
		throw new OperationNotSupportedException("[" + JpaUserDao.class.getSimpleName() + "] does not support operation 'remove'");
	}

	@Override
	public void removeAll(final Set<User> entitySet) {
		throw new OperationNotSupportedException("[" + JpaUserDao.class.getSimpleName() + "] does not support operation 'removeAll'");
	}

	@Override
	public void persistUser(final User user) throws UniqueConstraintException {
		try {
			super.persist(user);
			persistenceContextManager.flush();
		} catch (final PersistenceException persistenceException) {
			final Throwable persistenceExceptionCause = persistenceException.getCause();
			if (persistenceExceptionCause instanceof ConstraintViolationException) {
				final ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceExceptionCause;
				final String uniqueConstraintName = constraintViolationException.getConstraintName();
				if (StringUtils.containsIgnoreCase(uniqueConstraintName, User.COLUMNNAME_USERNAME)) {
					throw new UniqueConstraintException("unable to create user [" + user + "], unique constraint [" + uniqueConstraintName + "] violated by duplicated value in column [" + User.COLUMNNAME_USERNAME + "]", User.COLUMNNAME_USERNAME, constraintViolationException);
				} else if (StringUtils.containsIgnoreCase(uniqueConstraintName, User.COLUMNNAME_EMAILADDRESS)) {
					throw new UniqueConstraintException("unable to create user [" + user + "], unique constraint [" + uniqueConstraintName + "] violated by duplicated value in column [" + User.COLUMNNAME_EMAILADDRESS + "]", User.COLUMNNAME_EMAILADDRESS, constraintViolationException);
				}
			}

			throw persistenceException;
		}
	}

	@Override
	public List<User> findAll() {
		return findAll(null);
	}

	@Override
	public List<User> findAll(final Pagination pagination) {
		final TypedQuery<User> findUserTypedQuery = createNamedTypedQuery(User.FINDUSER_QUERYNAME);

		if (null != pagination) {
			findUserTypedQuery.setFirstResult(pagination.getFirstResult());
			findUserTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findUserTypedQuery.getResultList();
	}

	@Override
	public List<User> findByRoleId(final long roleId) {
		final TypedQuery<User> findUserByRoleIdTypedQuery = createNamedTypedQuery(User.FINDUSER_ROLEID_QUERYNAME);
		findUserByRoleIdTypedQuery.setParameter("roleId", roleId);
		return findUserByRoleIdTypedQuery.getResultList();
	}

	@Override
	public User retrieveByUsername(final String username) {
		if (null == username) {
			throw new IllegalArgumentException("'username' must not be null");
		}

		final TypedQuery<User> findUserByUsernameTypedQuery = createNamedTypedQuery(User.FINDUSER_USERNAME_QUERYNAME);
		findUserByUsernameTypedQuery.setParameter("username", username);

		try {
			return findUserByUsernameTypedQuery.getSingleResult();
		} catch (final NoResultException noResultException) {
			throw new EntityNotFoundException("user with username [" + username + "] not found", noResultException);
		}
	}

	@Override
	public User retrieveCurrentUser() {
		return retrieveByUsername(SecurityUtils.determineUsername());
	}
}
