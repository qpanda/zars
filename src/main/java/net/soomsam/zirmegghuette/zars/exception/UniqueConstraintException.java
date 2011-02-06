package net.soomsam.zirmegghuette.zars.exception;

@SuppressWarnings("serial")
public class UniqueConstraintException extends BusinessException {
	private final String uniqueConstraintField;

	public UniqueConstraintException(String message, String uniqueConstraintField, Throwable cause) {
		super(message, cause);
		this.uniqueConstraintField = uniqueConstraintField;
	}

	public String getUniqueConstraintField() {
		return uniqueConstraintField;
	}
}
