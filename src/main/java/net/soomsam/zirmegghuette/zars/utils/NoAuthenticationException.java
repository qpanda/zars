package net.soomsam.zirmegghuette.zars.utils;

@SuppressWarnings("serial")
public class NoAuthenticationException extends RuntimeException {
	public NoAuthenticationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoAuthenticationException(final String message) {
		super(message);
	}
}
