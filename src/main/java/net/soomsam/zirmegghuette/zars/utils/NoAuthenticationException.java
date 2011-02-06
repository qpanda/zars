package net.soomsam.zirmegghuette.zars.utils;

@SuppressWarnings("serial")
public class NoAuthenticationException extends RuntimeException {
	public NoAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoAuthenticationException(String message) {
		super(message);
	}
}
