package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.io.Serializable;
import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;

/**
 * common interface for all DAOs
 * 
 * @author erich liebmann
 * @param <Entity>
 *            the persistence entity type the DAO operates with
 */
public interface EntityDao<Entity extends BaseEntity> {
	/**
	 * persists the given persistence entity (and any associated entities with {@link CascadeType#PERSIST}) to the
	 * database
	 * 
	 * @param entity
	 *            the persistence entity to save to the database
	 */
	public void persist(Entity entity);

	/**
	 * merges the given persistence entity (and any associated entities with {@link CascadeType#MERGE}) with the
	 * persistence context
	 * 
	 * @param entity
	 *            the persistence entity to merg with the persistence context
	 */
	public void merge(Entity entity);

	/**
	 * attempts to retrieve the persistence entity with the given primary key from the database
	 * 
	 * <p>
	 * note: if you need to check whether the an entity with the given primary key exists use the
	 * {@link EntityDao#findByPrimaryKey(Serializable)} method and check for <code>null</code>.
	 * </p>
	 * 
	 * @param primaryKey
	 *            the primary key of the persistence entity to retrieve
	 * @return the persistence entity with the given primary key
	 * @throws EntityNotFoundException
	 *             thrown if the persistence entity with the given primary key does not exist
	 */
	public Entity retrieveByPrimaryKey(Serializable primaryKey) throws EntityNotFoundException;

	/**
	 * retrieves the persistence entity with the given primary key from the database
	 * 
	 * @param primaryKey
	 *            the primary key of the persistence entity to retrieve
	 * @return the persistence entity with the given primary key or <code>null</code> if no entity with the given
	 *         primary key exists
	 */
	public Entity findByPrimaryKey(Serializable primaryKey);

	/**
	 * retrieves all persistence entities with the type/class managed by the concrete DAO implementation
	 * 
	 * @return a {@link List} of all persistence entities with the type/class found in the database
	 */
	public List<Entity> findAll();

	/**
	 * refreshes the persistence entity with the data stored in the database (potentially) overwriting any changes made
	 * to the entity in the persistence context
	 * 
	 * @param entity
	 *            the persistence entity to refresh with the data from the database
	 */
	public void refresh(Entity entity);

	/**
	 * removes the persistence entity from the persistence context
	 * 
	 * @param entity
	 *            the persistence entity to remove from the persistence context
	 * @throws OperationNotSupportedException
	 *             if the DAO implementation does not support remove
	 */
	public void remove(Entity entity) throws OperationNotSupportedException;
}
