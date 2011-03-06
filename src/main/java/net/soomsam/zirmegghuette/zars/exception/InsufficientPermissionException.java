package net.soomsam.zirmegghuette.zars.exception;

import net.soomsam.zirmegghuette.zars.enums.OperationType;

@SuppressWarnings("serial")
public class InsufficientPermissionException extends BusinessException {
	private final long userId;
	private final Long objectId;
	private final OperationType operationType;

	public InsufficientPermissionException(final String message, final long userId, final OperationType operationType) {
		super(message);
		this.userId = userId;
		this.objectId = null;
		this.operationType = operationType;
	}

	public InsufficientPermissionException(final String message, final long userId, final Long objectId, final OperationType operationType) {
		super(message);
		this.userId = userId;
		this.objectId = objectId;
		this.operationType = operationType;
	}

	public long getUserId() {
		return userId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public OperationType getOperationType() {
		return operationType;
	}
}
