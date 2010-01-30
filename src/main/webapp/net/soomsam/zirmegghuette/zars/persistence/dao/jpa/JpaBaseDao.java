package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.persistence.dao.BaseDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class JpaBaseDao<Entity extends BaseEntity> implements BaseDao<Entity> {
	/**
	 * the persistence context used for all JPA based database access
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * the JDBC template used for all JDBC based database access
	 */
	// @Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * this method provides the class/type of the persistence entity the DAO provides functionality for
	 * 
	 * @return the class/type of the persistence entity managed by this DAO
	 */
	protected abstract Class<Entity> determineEntityClass();

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void clear() {
		entityManager.clear();
	}

	@Override
	public Query createNamedQuery(final String queryName) {
		if (null == queryName) {
			throw new IllegalArgumentException("[queryName] must not be null");
		}

		return entityManager.createNamedQuery(queryName);
	}

	@Override
	public Query createQuery(final String jpQueryString) {
		if (null == jpQueryString) {
			throw new IllegalArgumentException("[jpQueryString] must not be null");
		}

		return entityManager.createQuery(jpQueryString);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entity> findAll() {
		final Query findAllQuery = entityManager.createQuery("select x from " + determineEntityClass().getName() + " as x");
		final List<Entity> allEntityList = Collections.checkedList(findAllQuery.getResultList(), determineEntityClass());
		return allEntityList;
	}

	@Override
	public Entity findByPrimaryKey(final Serializable primaryKey) {
		return entityManager.find(determineEntityClass(), primaryKey);
	}

	@Override
	public void flush() {
		entityManager.flush();
	}

	@Override
	public void merge(final Entity entity) {
		entityManager.merge(entity);
	}

	@Override
	public void persist(final Entity entity) {
		entityManager.persist(entity);
	}

	@Override
	public void refresh(final Entity entity) {
		entityManager.refresh(entity);
	}

	@Override
	public void remove(final Entity entity) {
		entityManager.remove(entity);
	}

	@Override
	public Entity retrieveByPrimaryKey(final Serializable primaryKey) throws EntityNotFoundException {
		final Entity entity = entityManager.find(determineEntityClass(), primaryKey);
		if (entity == null) {
			throw new EntityNotFoundException("entity of type [" + determineEntityClass().getSimpleName() + " with primary key [" + primaryKey + "] not found");
		}

		return entity;
	}
}
