package net.soomsam.zirmegghuette.zars;

/**
 * The {@link TestUtilsException} is the thrown by {@link TestUtils} if an unexpected problem occured.
 * 
 * @author erich liebmann
 */
public class TestUtilsException extends RuntimeException {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link TestUtilsException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public TestUtilsException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link TestUtilsException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link TestUtilsException}
	 */
	public TestUtilsException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
