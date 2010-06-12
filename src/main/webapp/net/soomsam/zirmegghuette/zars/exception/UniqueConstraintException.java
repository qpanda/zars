package net.soomsam.zirmegghuette.zars.exception;

public class UniqueConstraintException extends BusinessException {
	private final String uniqueConstraintName;

	public UniqueConstraintException(String message, String uniqueConstraintName, Throwable cause) {
		super(message, cause);
		this.uniqueConstraintName = uniqueConstraintName;
	}

	public String getUniqueConstraintName() {
		return uniqueConstraintName;
	}
}
