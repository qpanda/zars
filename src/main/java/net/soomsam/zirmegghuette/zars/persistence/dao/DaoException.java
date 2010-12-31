package net.soomsam.zirmegghuette.zars.persistence.dao;

/**
 * The {@link DaoException} is the superclass of all exceptions thrown by DAO operations. The nature of the failure is
 * described by the name of the subclass.
 * 
 * @author erich liebmann
 */
public class DaoException extends RuntimeException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link DaoException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public DaoException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link DaoException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link DaoException}
	 */
	public DaoException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
