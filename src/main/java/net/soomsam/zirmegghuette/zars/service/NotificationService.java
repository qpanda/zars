package net.soomsam.zirmegghuette.zars.service;

import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

public interface NotificationService {
	public void sendGroupReservationNotification(final OperationType operationType, final GroupReservationBean groupReservationBean);
}
