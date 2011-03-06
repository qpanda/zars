package net.soomsam.zirmegghuette.zars.web.converter;

@SuppressWarnings("serial")
public class ConversionNotSupportedException extends RuntimeException {
	public ConversionNotSupportedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConversionNotSupportedException(final String message) {
		super(message);
	}
}
