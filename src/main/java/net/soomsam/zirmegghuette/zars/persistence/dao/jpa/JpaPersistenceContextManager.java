package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import net.soomsam.zirmegghuette.zars.persistence.dao.PersistenceContextManager;

import org.springframework.stereotype.Repository;

@Repository("persistenceContextManager")
public class JpaPersistenceContextManager implements PersistenceContextManager {
	/**
	 * the persistence context used for all JPA based database access
	 */
	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void clear() {
		entityManager.clear();
	}

	@Override
	public void flush() {
		entityManager.flush();
	}

	@Override
	public void setFlushMode(final FlushModeType flushMode) {
		entityManager.setFlushMode(flushMode);
	}
}
