package net.soomsam.zirmegghuette.zars.web.controller;

import java.util.Set;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class AddGroupReservationController extends ModifyGroupReservationController {
	private final static Logger logger = Logger.getLogger(AddGroupReservationController.class);

	public AddGroupReservationController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultArrivalDate();
			setDefaultDepartureDate();
		}
	}

	@Override
	protected String modifyGroupReservation() throws GroupReservationConflictException {
		// TODO selectAccountantId: actual beneficiary is current user
		logger.debug("creating group reservation with arrival/departure [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		savedGroupReservation = groupReservationService.createGroupReservation(selectedAccountantId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
		return "addGroupReservationConfirmation";
	}

	@Override
	protected String modifyGroupReservation(Set<ReservationVo> reservationVoSet) throws GroupReservationConflictException {
		logger.debug("creating group reservation with [" + determineReservationCount() + "] reservations");
		// TODO selectAccountantId: actual beneficiary is current user
		savedGroupReservation = groupReservationService.createGroupReservation(selectedAccountantId, selectedAccountantId, reservationVoSet, comment);
		return "addGroupReservationConfirmation";
	}
}