package net.soomsam.zirmegghuette.zars.service;

/**
 * The {@link ServiceException} is the superclass of all exceptions thrown by service operations. The nature of the
 * failure is described by the name of the subclass.
 * 
 * @author erich liebmann
 */
public class ServiceException extends RuntimeException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link ServiceException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public ServiceException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link ServiceException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link ServiceException}
	 */
	public ServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
