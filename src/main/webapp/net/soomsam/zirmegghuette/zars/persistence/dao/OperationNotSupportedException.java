package net.soomsam.zirmegghuette.zars.persistence.dao;

/**
 * The {@link OperationNotSupportedException} is thrown if an DAO implementation does not support the operation being
 * invoked.
 * 
 * @author erich liebmann
 */
public class OperationNotSupportedException extends DaoException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link OperationNotSupportedException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public OperationNotSupportedException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link OperationNotSupportedException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link OperationNotSupportedException}
	 */
	public OperationNotSupportedException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
