package net.soomsam.zirmegghuette.zars.web.converter;

public class ConversionNotSupportedException extends RuntimeException {
	public ConversionNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionNotSupportedException(String message) {
		super(message);
	}
}
