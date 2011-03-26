package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.enums.NotificationType;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

public interface NotificationService {
	public void sendGroupReservationNotification(final NotificationType notificationType, final GroupReservationBean groupReservationBean);

	public void sendUserNotification(final NotificationType notificationType, final UserBean userBean);
}
