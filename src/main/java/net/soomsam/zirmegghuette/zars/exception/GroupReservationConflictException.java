package net.soomsam.zirmegghuette.zars.exception;

import java.util.List;

import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

@SuppressWarnings("serial")
public class GroupReservationConflictException extends BusinessException {
	private final List<GroupReservationBean> conflictingGroupReservations;

	public GroupReservationConflictException(final String message, final List<GroupReservationBean> conflictingGroupReservations) {
		super(message);
		this.conflictingGroupReservations = conflictingGroupReservations;
	}

	public List<GroupReservationBean> getConflictingGroupReservations() {
		return conflictingGroupReservations;
	}
}
