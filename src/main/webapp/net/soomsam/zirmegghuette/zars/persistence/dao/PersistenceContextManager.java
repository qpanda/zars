package net.soomsam.zirmegghuette.zars.persistence.dao;


/**
 * common interface for all persistence context managers
 * 
 * @author erich liebmann
 * @param <Entity>
 *            the persistence entity type/class this DAO operates with
 */
public interface PersistenceContextManager {
	/**
	 * synchronises the persistence context with the database effectively storing all the changes to the persistence
	 * entities to the database
	 */
	public void flush();

	/**
	 * clears the persistence context effectively ensuring that entities can be garbage collected
	 */
	public void clear();
}
