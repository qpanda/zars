package net.soomsam.zirmegghuette.zars.exception;

/**
 * the {@link BusinessException} is used as a superclass for all checked exceptions throughout project.
 * 
 * <p>
 * the {@link BusinessException} should be used for expected conditions demanding an alternative response from a method
 * that can be expressed in terms of the method's intended purpose. the caller of the method expects these kinds of
 * conditions and has a strategy for coping with them.
 * </p>
 * 
 * <p>
 * the {@link BusinessException} is meant to act as an error code, hence create a separate subclass of
 * {@link BusinessException} for each different type of business error.
 * </p>
 * 
 * <p>
 * use the following considerations to determine whether to throw a {@link BusinessException} or a
 * {@link RuntimeException}
 * <ul>
 * <li><b><code>{@link BusinessException}</code> (contingency)</b>
 * <ul>
 * <li>is considered to be a part of the design</li>
 * <li>is expected to happen regularly but rarely</li>
 * <li>is taken care of by the upstream code that invokes the method</li>
 * <li>should be seen as an alternative return mode</li>
 * </ul>
 * </li>
 * <li><b><code>{@link RuntimeException}</code> (fault)</b>
 * <ul>
 * <li>is considered to be a nasty surprise</li>
 * <li>is expected to happen never or very rarely</li>
 * <li>is taken care of the people who know the method's implementation details and those who need to fix the problem</li>
 * <li>should be used for missing files, unavailable network resources, networking problems, programming bugs, hardware
 * malfunctions, and configuration mistakes</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * the distinction between <i>contingency</i> and <i>fault</i> is based on the <a
 * href="http://www.oracle.com/technology/pub/articles/dev2arch/2006/11/effective-exceptions.html">Effective Java
 * Exceptions</a> <a href="http://www.oracle.com/technology/community/welcome-bea/index.html">Dev2Dev</a> article.
 * </p>
 * 
 * @author erich liebmann
 */
public class BusinessException extends Exception {
	/**
	 * increment when attributes or method signatures are changed
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for {@link BusinessException}
	 * 
	 * @param message
	 *            the exception details message
	 */
	public BusinessException(final String message) {
		super(message);
	}

	/**
	 * constructor for {@link BusinessException}
	 * 
	 * @param message
	 *            the exception details message
	 * @param cause
	 *            the cause for the {@link BusinessException}
	 */
	public BusinessException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
