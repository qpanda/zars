package net.soomsam.zirmegghuette.zars.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.ReservationBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.primefaces.component.calendar.Calendar;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class EditGroupReservationController extends ModifyGroupReservationController {
	private final static Logger logger = Logger.getLogger(EditGroupReservationController.class);

	private boolean validNavigation = true;

	private Long groupReservationId;

	public boolean isValidNavigation() {
		return validNavigation;
	}

	public Long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(final Long groupReservationId) {
		this.groupReservationId = groupReservationId;
	}

	public String getInvalidGroupReservationIdMessage() {
		Locale preferredLocale = settingController.getPreferredLocale();
		return MessageFactory.getMessage(preferredLocale, "sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR, null).getSummary();
	}

	public void retrieveGroupReservation() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			this.validNavigation = false;
			return;
		}

		if (null == this.groupReservationId) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
			return;
		}

		try {
			final GroupReservationBean groupReservationBean = groupReservationService.retrieveGroupReservation(groupReservationId);
			this.groupReservationId = groupReservationBean.getGroupReservationId();
			this.arrival = groupReservationBean.getArrival();
			this.departure = groupReservationBean.getDeparture();
			this.guests = groupReservationBean.getGuests();
			this.comment = groupReservationBean.getComment();
			this.selectedAccountantId = groupReservationBean.getAccountant().getUserId();
			this.selectedBeneficiaryId = groupReservationBean.getBeneficiary().getUserId();

			if (!SecurityUtils.hasRole(RoleType.ROLE_ADMIN) && selectedBeneficiaryId != securityController.getCurrentUserId()) {
				this.validNavigation = false;
				final FacesMessage modificationNotAllowedFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationModificationNotAllowedError", FacesMessage.SEVERITY_ERROR, null);
				FacesContext.getCurrentInstance().addMessage(null, modificationNotAllowedFacesMessage);
			}

			List<ReservationBean> reservationBeanList = groupReservationBean.getReservations();
			if ((null != reservationBeanList) && !reservationBeanList.isEmpty()) {
				populateReservation(reservationBeanList);
			}
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
		}
	}

	protected void populateReservation(final List<ReservationBean> reservationBeanList) {
		for (int i = 0; i < reservationBeanList.size(); ++i) {
			ReservationBean reservationBean = reservationBeanList.get(i);
			populateReservationArrivalCalendarComponent(i + 1, reservationBean.getArrival());
			populateReservationDepartureCalendarComponent(i + 1, reservationBean.getDeparture());
			populateReservationFirstNameInputTextComponent(i + 1, reservationBean.getFirstName());
			populateReservationLastNameInputTextComponent(i + 1, reservationBean.getLastName());
		}

		arrivalCalendar.setDisabled(true);
		arrivalCalendar.resetValue();

		departureCalendar.setDisabled(true);
		departureCalendar.resetValue();

		guestsInputText.setDisabled(true);
		guestsInputText.setValue(determineReservationCount());
		guestsInputText.setSubmittedValue(determineReservationCount());
		guestsInputText.setLocalValueSet(false);
		guestsInputText.setValid(true);
	}

	protected void populateReservationArrivalCalendarComponent(final int reservationPanelRow, final Date reservationArrival) {
		String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar reservationArrivalCalendarComponent = createReservationCalendarComponent(reservationArrivalCalendarComponentId, null);
		reservationArrivalCalendarComponent.setValue(reservationArrival);
		reservationArrivalCalendarComponent.setValid(true);
		reservationPanelGrid.getChildren().add(reservationArrivalCalendarComponent);
	}

	protected void populateReservationDepartureCalendarComponent(final int reservationPanelRow, final Date reservationDeparture) {
		String reservationDepartureCalendarComponentId = determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar reservationDepartureCalendarComponent = createReservationCalendarComponent(reservationDepartureCalendarComponentId, null);
		reservationDepartureCalendarComponent.setValue(reservationDeparture);
		reservationDepartureCalendarComponent.setValid(true);
		reservationPanelGrid.getChildren().add(reservationDepartureCalendarComponent);
	}

	protected void populateReservationFirstNameInputTextComponent(final int reservationPanelRow, final String reservationFirstName) {
		String reservationFirstNameInputTextComponent = determineReservationComponentId(RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText firstNameInputTextReservationComponent = createReservationInputTextComponent();
		firstNameInputTextReservationComponent.setValue(reservationFirstName);
		firstNameInputTextReservationComponent.setSubmittedValue(reservationFirstName);
		firstNameInputTextReservationComponent.setLocalValueSet(false);
		firstNameInputTextReservationComponent.setValid(true);
		firstNameInputTextReservationComponent.setId(reservationFirstNameInputTextComponent);
		firstNameInputTextReservationComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationReservationGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(firstNameInputTextReservationComponent);
	}

	protected void populateReservationLastNameInputTextComponent(final int reservationPanelRow, final String individualReservationLastName) {
		String reservationLastNameInputTextComponentId = determineReservationComponentId(RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText reservationLastNameInputTextComponent = createReservationInputTextComponent();
		reservationLastNameInputTextComponent.setValue(individualReservationLastName);
		reservationLastNameInputTextComponent.setSubmittedValue(individualReservationLastName);
		reservationLastNameInputTextComponent.setLocalValueSet(false);
		reservationLastNameInputTextComponent.setValid(true);
		reservationLastNameInputTextComponent.setId(reservationLastNameInputTextComponentId);
		reservationLastNameInputTextComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationReservationGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(reservationLastNameInputTextComponent);
	}

	@Override
	protected String modifyGroupReservation() throws GroupReservationConflictException, InsufficientPermissionException {
		logger.debug("updating group reservation with id [" + groupReservationId + "], beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and arrival/departure [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedBeneficiaryId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
		return "editGroupReservationConfirmation";
	}

	@Override
	protected String modifyGroupReservation(final Set<ReservationVo> reservationVoSet) throws GroupReservationConflictException, InsufficientPermissionException {
		logger.debug("updating group reservation with id [" + groupReservationId + "], beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and [" + determineReservationCount() + "] reservations");
		savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedBeneficiaryId, selectedAccountantId, reservationVoSet, comment);
		return "editGroupReservationConfirmation";
	}
}