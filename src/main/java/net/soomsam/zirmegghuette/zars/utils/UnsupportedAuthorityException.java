package net.soomsam.zirmegghuette.zars.utils;

@SuppressWarnings("serial")
public class UnsupportedAuthorityException extends RuntimeException {
	public UnsupportedAuthorityException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedAuthorityException(String message) {
		super(message);
	}
}
