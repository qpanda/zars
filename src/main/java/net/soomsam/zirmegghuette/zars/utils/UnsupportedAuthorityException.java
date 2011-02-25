package net.soomsam.zirmegghuette.zars.utils;

@SuppressWarnings("serial")
public class UnsupportedAuthorityException extends RuntimeException {
	public UnsupportedAuthorityException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedAuthorityException(final String message) {
		super(message);
	}
}
