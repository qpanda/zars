package net.soomsam.zirmegghuette.zars.exception;

@SuppressWarnings("serial")
public class UniqueConstraintException extends BusinessException {
	private final String uniqueConstraintField;

	public UniqueConstraintException(final String message, final String uniqueConstraintField, final Throwable cause) {
		super(message, cause);
		this.uniqueConstraintField = uniqueConstraintField;
	}

	public String getUniqueConstraintField() {
		return uniqueConstraintField;
	}
}
