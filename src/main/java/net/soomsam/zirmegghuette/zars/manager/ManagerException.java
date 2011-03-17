package net.soomsam.zirmegghuette.zars.manager;

/**
 * The {@link ManagerException} is the superclass of all exceptions thrown by service operations. The nature of the
 * failure is described by the name of the subclass.
 * 
 * @author erich liebmann
 */
public class ManagerException extends RuntimeException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link ManagerException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public ManagerException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link ManagerException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link ManagerException}
	 */
	public ManagerException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
