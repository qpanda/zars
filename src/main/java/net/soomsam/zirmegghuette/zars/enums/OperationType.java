package net.soomsam.zirmegghuette.zars.enums;

public enum OperationType {
	ADD("ADD"), UPDATE("UPDATE"), DELETE("DELETE");

	private final String operationName;

	private OperationType(final String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
}
