package net.soomsam.zirmegghuette.zars.enums;

public enum NotificationModeType {
	ENABLED("ENABLED"), DISABLED("DISABLED"), SIMULATE("SIMULATE");

	private final String notificationModeName;

	private NotificationModeType(final String notificationModeName) {
		this.notificationModeName = notificationModeName;
	}

	public String getNotificationModeName() {
		return notificationModeName;
	}
}
