package net.soomsam.zirmegghuette.zars.enums;

public enum OperationType {
	OPERATION_ADD("OPERATION_ADD"), OPERATION_UPDATE("OPERATION_UPDATE"), OPERATION_DELETE("OPERATION_DELETE");

	private final String operationName;

	private OperationType(final String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
}
