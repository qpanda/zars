package net.soomsam.zirmegghuette.zars.web.controller;

import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class AddGroupReservationController extends ModifyGroupReservationController {
	private final static Logger logger = Logger.getLogger(AddGroupReservationController.class);

	private boolean validNavigation = true;

	public AddGroupReservationController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultArrivalDate();
			setDefaultDepartureDate();
		}
	}

	public boolean isValidNavigation() {
		return validNavigation;
	}

	public void prepareGroupReservation() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			this.validNavigation = false;
			return;
		}

		if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && !SecurityUtils.hasRole(RoleType.ROLE_USER)) {
			this.validNavigation = false;
			final FacesMessage addNotAllowedFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationAddNotAllowedError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, addNotAllowedFacesMessage);
		}
	}

	@Override
	protected String modifyGroupReservation() throws GroupReservationConflictException, InsufficientPermissionException {
		logger.debug("creating group reservation with arrival/departure [" + arrival + "]-[" + departure + "], beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and for [" + guests + "] guests");
		savedGroupReservation = groupReservationService.createGroupReservation(selectedBeneficiaryId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
		return "addGroupReservationConfirmation";
	}

	@Override
	protected String modifyGroupReservation(final Set<ReservationVo> reservationVoSet) throws GroupReservationConflictException, InsufficientPermissionException {
		logger.debug("creating group reservation with beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and [" + determineReservationCount() + "] reservations");
		savedGroupReservation = groupReservationService.createGroupReservation(selectedBeneficiaryId, selectedAccountantId, reservationVoSet, comment);
		return "addGroupReservationConfirmation";
	}
}