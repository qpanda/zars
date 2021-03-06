package net.soomsam.zirmegghuette.zars.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.NotificationType;
import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationNonconsecutiveException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.ReservationBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.primefaces.component.calendar.Calendar;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
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
		final Locale preferredLocale = settingController.getPreferredLocale();
		return MessageUtils.obtainMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationGroupReservationIdError", preferredLocale);
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
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR);
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
				final FacesMessage modificationNotAllowedFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationModificationNotAllowedError", FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, modificationNotAllowedFacesMessage);
			}

			final List<ReservationBean> reservationBeanList = groupReservationBean.getReservations();
			if ((null != reservationBeanList) && !reservationBeanList.isEmpty()) {
				populateReservation(reservationBeanList);
			}
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
		}
	}

	protected void populateReservation(final List<ReservationBean> reservationBeanList) {
		for (int i = 0; i < reservationBeanList.size(); ++i) {
			final ReservationBean reservationBean = reservationBeanList.get(i);
			populateReservationSelectedCheckboxComponent(i + 1);
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

	protected void populateReservationSelectedCheckboxComponent(final int reservationPanelRow) {
		final String reservationSelectedCheckboxComponentId = determineReservationComponentId(RESERVATIONSELECTED_CHECKBOXCOMPONENT_IDPREFIX, reservationPanelRow);
		final HtmlSelectBooleanCheckbox reservationSelectedCheckboxComponent = createReservationCheckboxComponent();
		reservationSelectedCheckboxComponent.getAttributes().put(RESERVATION_COMPONENT_ID, reservationSelectedCheckboxComponentId);
		reservationPanelGrid.getChildren().add(reservationSelectedCheckboxComponent);
	}

	protected void populateReservationArrivalCalendarComponent(final int reservationPanelRow, final Date reservationArrival) {
		final String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		final Calendar reservationArrivalCalendarComponent = createReservationCalendarComponent(reservationArrivalCalendarComponentId, null);
		reservationArrivalCalendarComponent.setValue(reservationArrival);
		reservationArrivalCalendarComponent.setValid(true);
		reservationPanelGrid.getChildren().add(reservationArrivalCalendarComponent);
	}

	protected void populateReservationDepartureCalendarComponent(final int reservationPanelRow, final Date reservationDeparture) {
		final String reservationDepartureCalendarComponentId = determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		final Calendar reservationDepartureCalendarComponent = createReservationCalendarComponent(reservationDepartureCalendarComponentId, null);
		reservationDepartureCalendarComponent.setValue(reservationDeparture);
		reservationDepartureCalendarComponent.setValid(true);
		reservationPanelGrid.getChildren().add(reservationDepartureCalendarComponent);
	}

	protected void populateReservationFirstNameInputTextComponent(final int reservationPanelRow, final String reservationFirstName) {
		final String reservationFirstNameInputTextComponentId = determineReservationComponentId(RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		final HtmlInputText firstNameInputTextReservationComponent = createReservationInputTextComponent();
		firstNameInputTextReservationComponent.setValue(reservationFirstName);
		firstNameInputTextReservationComponent.setSubmittedValue(reservationFirstName);
		firstNameInputTextReservationComponent.setLocalValueSet(false);
		firstNameInputTextReservationComponent.setValid(true);
		firstNameInputTextReservationComponent.setRequiredMessage(MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationReservationGuestFirstNameError", reservationPanelRow));
		firstNameInputTextReservationComponent.getAttributes().put(RESERVATION_COMPONENT_ID, reservationFirstNameInputTextComponentId);
		reservationPanelGrid.getChildren().add(firstNameInputTextReservationComponent);
	}

	protected void populateReservationLastNameInputTextComponent(final int reservationPanelRow, final String individualReservationLastName) {
		final String reservationLastNameInputTextComponentId = determineReservationComponentId(RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		final HtmlInputText reservationLastNameInputTextComponent = createReservationInputTextComponent();
		reservationLastNameInputTextComponent.setValue(individualReservationLastName);
		reservationLastNameInputTextComponent.setSubmittedValue(individualReservationLastName);
		reservationLastNameInputTextComponent.setLocalValueSet(false);
		reservationLastNameInputTextComponent.setValid(true);
		reservationLastNameInputTextComponent.setRequiredMessage(MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationReservationGuestLastNameError", reservationPanelRow));
		reservationLastNameInputTextComponent.getAttributes().put(RESERVATION_COMPONENT_ID, reservationLastNameInputTextComponentId);
		reservationPanelGrid.getChildren().add(reservationLastNameInputTextComponent);
	}

	@Override
	protected String modifyGroupReservation() throws GroupReservationConflictException, InsufficientPermissionException {
		logger.debug("updating group reservation with id [" + groupReservationId + "], beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and arrival/departure [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedBeneficiaryId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
		notificationService.sendGroupReservationNotification(NotificationType.NOTIFICATION_GROUPRESERVATION_UPDATE, savedGroupReservation);
		return "editGroupReservationConfirmation";
	}

	@Override
	protected String modifyGroupReservation(final Set<ReservationVo> reservationVoSet) throws GroupReservationConflictException, InsufficientPermissionException, GroupReservationNonconsecutiveException {
		logger.debug("updating group reservation with id [" + groupReservationId + "], beneficiaryId [" + selectedBeneficiaryId + "], accountantId [" + selectedAccountantId + "] and [" + determineReservationCount() + "] reservations");
		savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedBeneficiaryId, selectedAccountantId, reservationVoSet, comment);
		notificationService.sendGroupReservationNotification(NotificationType.NOTIFICATION_GROUPRESERVATION_UPDATE, savedGroupReservation);
		return "editGroupReservationConfirmation";
	}
}