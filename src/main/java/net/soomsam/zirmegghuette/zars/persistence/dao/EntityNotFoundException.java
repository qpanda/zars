package net.soomsam.zirmegghuette.zars.persistence.dao;

/**
 * The {@link EntityNotFoundException} is thrown if an {@link EntityDao#retrieveByPrimaryKey(java.io.Serializable)}
 * method if no persistence entity with the specified primary key exists.
 * 
 * @author erich liebmann
 */
public class EntityNotFoundException extends DaoException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link EntityNotFoundException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public EntityNotFoundException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link EntityNotFoundException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link EntityNotFoundException}
	 */
	public EntityNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
