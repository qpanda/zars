package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.persistence.dao.EntityDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class JpaEntityDao<Entity extends BaseEntity> implements EntityDao<Entity> {
	/**
	 * the maximum result set size retrieved from the database to protect the application from running out of memory
	 */
	public static int QUERY_MAXRESULTS = 10000;

	/**
	 * the persistence context used for all JPA based database access
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * the JDBC template used for all JDBC based database access
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * this method provides the class/type of the persistence entity the DAO provides functionality for
	 * 
	 * @return the class/type of the persistence entity managed by this DAO
	 */
	protected abstract Class<Entity> determineEntityClass();

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entity> findAll() {
		final Query findAllQuery = entityManager.createQuery("select x from " + determineEntityClass().getName() + " as x");
		final List<Entity> allEntityList = Collections.checkedList(findAllQuery.getResultList(), determineEntityClass());
		return allEntityList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Entity> findAll(final Pagination pagination) {
		if (null == pagination) {
			throw new IllegalArgumentException("'pagination' must not be null");
		}

		final Query findAllQuery = entityManager.createQuery("select x from " + determineEntityClass().getName() + " as x");
		findAllQuery.setFirstResult(pagination.getFirstResult());
		findAllQuery.setMaxResults(pagination.getMaxResults());
		final List<Entity> allEntityList = Collections.checkedList(findAllQuery.getResultList(), determineEntityClass());
		return allEntityList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public long countAll() {
		final Query countAllQuery = entityManager.createQuery("select count(x) from " + determineEntityClass().getName() + " as x");
		return (Long) countAllQuery.getSingleResult();
	}

	@Override
	public Entity findByPrimaryKey(final Serializable primaryKey) {
		return entityManager.find(determineEntityClass(), primaryKey);
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
	public void removeAll(final Set<Entity> entitySet) {
		Iterator<Entity> entityIterator = entitySet.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();
			entityManager.remove(entity);
		}
	}

	@Override
	public Entity retrieveByPrimaryKey(final Serializable primaryKey) throws EntityNotFoundException {
		final Entity entity = entityManager.find(determineEntityClass(), primaryKey);
		if (entity == null) {
			throw new EntityNotFoundException("entity of type [" + determineEntityClass().getSimpleName() + "] with primary key [" + primaryKey + "] not found");
		}

		return entity;
	}

	/**
	 * creates a {@link Query} object for the named query
	 * 
	 * @param queryName
	 *            the name of the query to create a query object for
	 * @return the {@link Query} object created on behalf of the named query
	 */
	protected Query createNamedQuery(final String queryName) {
		if (null == queryName) {
			throw new IllegalArgumentException("'queryName' must not be null");
		}

		Query namedQuery = entityManager.createNamedQuery(queryName);
		namedQuery.setMaxResults(QUERY_MAXRESULTS);
		return namedQuery;
	}

	/**
	 * dynamically creates a query from the JPQL string provided
	 * 
	 * @param jpQueryString
	 *            the JPQL string to create the query from
	 * @return the {@link Query} object created on behalf of the JPQL string
	 */
	protected Query createQuery(final String jpQueryString) {
		if (null == jpQueryString) {
			throw new IllegalArgumentException("'jpQueryString' must not be null");
		}

		Query query = entityManager.createQuery(jpQueryString);
		query.setMaxResults(QUERY_MAXRESULTS);
		return query;
	}
}
