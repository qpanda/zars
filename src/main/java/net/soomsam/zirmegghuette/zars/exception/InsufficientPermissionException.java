package net.soomsam.zirmegghuette.zars.exception;

import net.soomsam.zirmegghuette.zars.enums.OperationType;

@SuppressWarnings("serial")
public class InsufficientPermissionException extends BusinessException {
	private final long userId;
	private final Long objectId;
	private final OperationType operationType;

	public InsufficientPermissionException(String message, long userId, OperationType operationType) {
		super(message);
		this.userId = userId;
		this.objectId = null;
		this.operationType = operationType;
	}

	public InsufficientPermissionException(String message, long userId, Long objectId, OperationType operationType) {
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
