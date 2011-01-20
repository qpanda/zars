package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateMidnight;
import org.primefaces.component.calendar.Calendar;

import com.sun.faces.util.MessageFactory;

public abstract class ModifyGroupReservationController implements Serializable {
	protected final static String RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX = "reservationArrival";
	protected final static String RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX = "reservationDepature";
	protected final static String RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX = "reservationFirstName";
	protected final static String RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX = "reservationLastName";

	@Inject
	protected transient SettingController settingController;

	@Inject
	protected transient SecurityController securityController;

	@Inject
	protected transient UserService userService;

	@Inject
	protected transient GroupReservationService groupReservationService;

	protected Date arrival;

	protected Date departure;

	@Min(value = 1, message = "{sectionsApplicationGroupReservationGuestsError}")
	@NotNull(message = "{sectionsApplicationGroupReservationGuestsError}")
	protected Long guests;

	protected Long selectedAccountantId;

	protected Long selectedBeneficiaryId;

	@Length(min = 0, max = 512, message = "{sectionsApplicationGroupReservationCommentError}")
	protected String comment;

	protected Calendar arrivalCalendar;

	protected Calendar departureCalendar;

	protected HtmlInputText guestsInputText;

	protected HtmlPanelGrid reservationPanelGrid;

	protected GroupReservationBean savedGroupReservation;

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(final Date arrival) {
		this.arrival = arrival;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(final Date departure) {
		this.departure = departure;
	}

	public Long getGuests() {
		return guests;
	}

	public void setGuests(final Long guests) {
		this.guests = guests;
	}

	public GroupReservationBean getSavedGroupReservation() {
		return savedGroupReservation;
	}

	public List<UserBean> getAvailableAccountants() {
		return userService.findUsers(RoleType.ROLE_ACCOUNTANT);
	}

	public Long getSelectedAccountantId() {
		return selectedAccountantId;
	}

	public void setSelectedAccountantId(final Long selectedAccountantId) {
		this.selectedAccountantId = selectedAccountantId;
	}

	public List<UserBean> getAvailableBeneficiaries() {
		if (SecurityUtils.hasRole(RoleType.ROLE_ADMIN)) {
			return userService.findUsers(RoleType.ROLE_USER);
		} else if (SecurityUtils.hasRole(RoleType.ROLE_USER)) {
			List<UserBean> currentUserList = new ArrayList<UserBean>();
			currentUserList.add(securityController.getCurrentUser());
			return currentUserList;
		} else if (SecurityUtils.hasRole(RoleType.ROLE_ACCOUNTANT)) {
			return new ArrayList<UserBean>();
		}

		throw new IllegalStateException("current user [" + SecurityUtils.determineUsername() + "] has none of the roles [" + RoleType.values() + "]");
	}

	public Long getSelectedBeneficiaryId() {
		return selectedBeneficiaryId;
	}

	public void setSelectedBeneficiaryId(final Long selectedBeneficiaryId) {
		this.selectedBeneficiaryId = selectedBeneficiaryId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public HtmlPanelGrid getReservationPanelGrid() {
		return reservationPanelGrid;
	}

	public void setReservationPanelGrid(final HtmlPanelGrid reservationPanelGrid) {
		this.reservationPanelGrid = reservationPanelGrid;
	}

	public Calendar getArrivalCalendar() {
		return arrivalCalendar;
	}

	public void setArrivalCalendar(final Calendar arrivalCalendar) {
		this.arrivalCalendar = arrivalCalendar;
	}

	public Calendar getDepartureCalendar() {
		return departureCalendar;
	}

	public void setDepartureCalendar(final Calendar departureCalendar) {
		this.departureCalendar = departureCalendar;
	}

	public HtmlInputText getGuestsInputText() {
		return guestsInputText;
	}

	public void setGuestsInputText(final HtmlInputText guestsInputText) {
		this.guestsInputText = guestsInputText;
	}

	public void addReservation(final ActionEvent addReservationCommandLinkActionEvent) {
		addReservationComponents();

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

	public void removeReservation(final ActionEvent removeReservationCommandLinkActionEvent) {
		if (0 != determineReservationCount()) {
			for (int i = 0; i < reservationPanelGrid.getColumns(); ++i) {
				reservationPanelGrid.getChildren().remove(reservationPanelGrid.getChildCount() - 1);
			}

			guestsInputText.setValue(determineReservationCount());
			guestsInputText.setSubmittedValue(determineReservationCount());
			guestsInputText.setLocalValueSet(false);
			guestsInputText.setValid(true);
		}

		if (0 == determineReservationCount()) {
			setDefaultArrivalDate();
			arrivalCalendar.setDisabled(false);
			arrivalCalendar.resetValue();

			setDefaultDepartureDate();
			departureCalendar.setDisabled(false);
			departureCalendar.resetValue();

			guestsInputText.setDisabled(false);
			guestsInputText.resetValue();
		}
	}

	protected void addReservationComponents() {
		int reservationPanelRow = determineReservationCount() + 1;
		addReservationArrivalCalendarComponent(reservationPanelRow);
		addReservationDepartureCalendarComponent(reservationPanelRow);
		addReservationFirstNameInputTextComponent(reservationPanelRow);
		addReservationLastNameInputTextComponent(reservationPanelRow);
	}

	protected void addReservationArrivalCalendarComponent(final int reservationPanelRow) {
		Calendar templateArrivalCalendar = (Calendar)determineReservationComponent(determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow - 1));
		if (null == templateArrivalCalendar) {
			templateArrivalCalendar = arrivalCalendar;
		}

		String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar reservationArrivalCalendarComponent = createReservationCalendarComponent(reservationArrivalCalendarComponentId, templateArrivalCalendar);
		reservationPanelGrid.getChildren().add(reservationArrivalCalendarComponent);
	}

	protected void addReservationDepartureCalendarComponent(final int reservationPanelRow) {
		Calendar templateDepartureCalendar = (Calendar)determineReservationComponent(determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow - 1));
		if (null == templateDepartureCalendar) {
			templateDepartureCalendar = departureCalendar;
		}

		String reservationDepartureCalendarComponentId = determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar reservationDepartureCalendarComponent = createReservationCalendarComponent(reservationDepartureCalendarComponentId, templateDepartureCalendar);
		reservationPanelGrid.getChildren().add(reservationDepartureCalendarComponent);
	}

	protected void addReservationFirstNameInputTextComponent(final int reservationPanelRow) {
		String reservationFirstNameInputTextComponentId = determineReservationComponentId(RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText reservationFirstNameInputTextComponent = createReservationInputTextComponent();
		reservationFirstNameInputTextComponent.setId(reservationFirstNameInputTextComponentId);
		reservationFirstNameInputTextComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationReservationGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(reservationFirstNameInputTextComponent);
	}

	protected void addReservationLastNameInputTextComponent(final int reservationPanelRow) {
		String reservationLastNameInputTextComponentId = determineReservationComponentId(RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText reservationLastNameInputTextComponent = createReservationInputTextComponent();
		reservationLastNameInputTextComponent.setId(reservationLastNameInputTextComponentId);
		reservationLastNameInputTextComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationReservationGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(reservationLastNameInputTextComponent);
	}

	protected Calendar createReservationCalendarComponent(final String reservationCalendarComponentId, final Calendar templateCalendar) {
		Calendar reservationCalendarComponent = new Calendar();
		reservationCalendarComponent.setId(reservationCalendarComponentId);
		reservationCalendarComponent.setMode("popup");
		reservationCalendarComponent.setShowOn("button");
		reservationCalendarComponent.setPopupIconOnly(true);
		reservationCalendarComponent.setReadOnlyInputText(true);
		reservationCalendarComponent.setLocale(settingController.getPreferredLocale());
		reservationCalendarComponent.setPattern(settingController.getPreferredDateFormatPattern());
		reservationCalendarComponent.setTimeZone(settingController.getPreferredTimeZone());
		reservationCalendarComponent.setRequired(true);
		reservationCalendarComponent.setInputStyleClass("applicationFormInput");

		if (null != templateCalendar) {
			String templateCalendarSubmittedValue = (String)templateCalendar.getSubmittedValue();
			if (!StringUtils.isEmpty(templateCalendarSubmittedValue)) {
				reservationCalendarComponent.setValue(templateCalendar.getValue());
				reservationCalendarComponent.setSubmittedValue(templateCalendar.getSubmittedValue());
				reservationCalendarComponent.setLocalValueSet(templateCalendar.isLocalValueSet());
				reservationCalendarComponent.setValid(templateCalendar.isValid());
			}
		}

		return reservationCalendarComponent;
	}

	protected HtmlInputText createReservationInputTextComponent() {
		HtmlInputText reservationInputTextComponent = new HtmlInputText();
		reservationInputTextComponent.setStyleClass("applicationFormInput");
		reservationInputTextComponent.setRequired(true);
		return reservationInputTextComponent;
	}

	protected int determineReservationCount() {
		if (0 != (reservationPanelGrid.getChildCount() % reservationPanelGrid.getColumns())) {
			throw new IllegalStateException("invalid number of components in panel grid [" + reservationPanelGrid.getId() + "]");
		}

		int reservationComponentCount = reservationPanelGrid.getChildCount() - reservationPanelGrid.getColumns();
		return reservationComponentCount / reservationPanelGrid.getColumns();
	}

	protected String determineReservationComponentId(final String reservationComponentIdPrefix, final int reservationPanelRow) {
		return reservationComponentIdPrefix + reservationPanelRow;
	}

	protected UIComponent determineReservationComponent(final String reservationComponentId) {
		List<UIComponent> reservationComponentList = reservationPanelGrid.getChildren();
		for (UIComponent reservationComponent : reservationComponentList) {
			if (reservationComponentId.equals(reservationComponent.getId())) {
				return reservationComponent;
			}
		}

		return null;
	}

	protected void setDefaultArrivalDate() {
		arrival = new DateMidnight().toDate();
	}

	protected void setDefaultDepartureDate() {
		departure = new DateMidnight().plusDays(1).toDate();
	}

	protected boolean validArrivalDepartureDateRange(final Date arrival, final Date departure) {
		DateMidnight arrivalDateMidnight = new DateMidnight(arrival);
		DateMidnight departureDateMidnight = new DateMidnight(departure);
		return departureDateMidnight.isAfter(arrivalDateMidnight);
	}

	protected Long determineBeneficiaryId() {
		if (SecurityUtils.hasRole(RoleType.ROLE_ADMIN)) {
			return selectedBeneficiaryId;
		} else if (SecurityUtils.hasRole(RoleType.ROLE_USER)) {
			return securityController.getCurrentUserId();
		} else if (SecurityUtils.hasRole(RoleType.ROLE_ACCOUNTANT)) {
			return null;
		}

		throw new IllegalStateException("current user [" + SecurityUtils.determineUsername() + "] has none of the roles [" + RoleType.values() + "]");
	}

	public String save() {
		selectedBeneficiaryId = determineBeneficiaryId();

		if (0 == determineReservationCount()) {
			return saveGroupReservation();
		}

		return saveGroupReservationWithReservations();
	}

	protected String saveGroupReservation() {
		if (!validArrivalDepartureDateRange(arrival, departure)) {
			String arrivalValue = settingController.getPreferredDateFormat().format(arrival);
			String departureValue = settingController.getPreferredDateFormat().format(departure);
			final FacesMessage arrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, arrivalValue, departureValue);
			FacesContext.getCurrentInstance().addMessage(null, arrivalDepatureFacesMessage);
			return null;
		}

		if (null == selectedBeneficiaryId) {
			final FacesMessage beneficiaryFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationBeneficiaryError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, beneficiaryFacesMessage);
			return null;
		}

		try {
			return modifyGroupReservation();
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = settingController.getPreferredDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = settingController.getPreferredDateFormat().format(conflictingGroupReservation.getDeparture());
				final FacesMessage groupReservationConflictFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationConflictError", FacesMessage.SEVERITY_ERROR, conflictingGroupReservation.getGroupReservationId(), arrivalValue, departureValue, conflictingGroupReservation.getBeneficiary().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, groupReservationConflictFacesMessage);
			}
		} catch (final InsufficientPermissionException insufficientPermissionException) {
			final FacesMessage insufficientPermissionFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationModificationNotAllowedError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, insufficientPermissionFacesMessage);
		}

		return null;
	}

	protected String saveGroupReservationWithReservations() {
		Set<ReservationVo> reservationVoSet = new HashSet<ReservationVo>();
		for (int i = 1; i <= determineReservationCount(); ++i) {
			String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, i);
			Calendar reservationArrivalCalendarComponent = (Calendar)determineReservationComponent(reservationArrivalCalendarComponentId);
			Date reservationArrival = (Date)reservationArrivalCalendarComponent.getValue();

			String reservationDepartureCalendarComponentId = determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, i);
			Calendar reservationDepartureCalendarComponent = (Calendar)determineReservationComponent(reservationDepartureCalendarComponentId);
			Date reservationDeparture = (Date)reservationDepartureCalendarComponent.getValue();

			if (!validArrivalDepartureDateRange(reservationArrival, reservationDeparture)) {
				String reservationArrivalValue = settingController.getPreferredDateFormat().format(reservationArrival);
				String reservationDepatureValue = settingController.getPreferredDateFormat().format(reservationDeparture);
				final FacesMessage reservationArrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, reservationArrivalValue, reservationDepatureValue, i);
				FacesContext.getCurrentInstance().addMessage(null, reservationArrivalDepatureFacesMessage);
				return null;
			}

			String reservationFirstNameInputTextComponentId = determineReservationComponentId(RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX, i);
			HtmlInputText reservationFirstNameInputTextComponent = (HtmlInputText)determineReservationComponent(reservationFirstNameInputTextComponentId);
			String reservationFirstName = (String)reservationFirstNameInputTextComponent.getValue();

			String reservationLastNameInputTextComponentId = determineReservationComponentId(RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX, i);
			HtmlInputText reservationLastNameInputTextComponent = (HtmlInputText)determineReservationComponent(reservationLastNameInputTextComponentId);
			String reservationLastName = (String)reservationLastNameInputTextComponent.getValue();

			ReservationVo reservationVo = new ReservationVo(i, new DateMidnight(reservationArrival), new DateMidnight(reservationDeparture), reservationFirstName, reservationLastName);
			reservationVoSet.add(reservationVo);
		}

		if (null == selectedBeneficiaryId) {
			final FacesMessage beneficiaryFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationBeneficiaryError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, beneficiaryFacesMessage);
			return null;
		}

		try {
			return modifyGroupReservation(reservationVoSet);
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = settingController.getPreferredDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = settingController.getPreferredDateFormat().format(conflictingGroupReservation.getDeparture());
				final FacesMessage groupReservationConflictFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationConflictError", FacesMessage.SEVERITY_ERROR, conflictingGroupReservation.getGroupReservationId(), arrivalValue, departureValue, conflictingGroupReservation.getBeneficiary().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, groupReservationConflictFacesMessage);
			}
		} catch (final InsufficientPermissionException insufficientPermissionException) {
			final FacesMessage insufficientPermissionFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationModificationNotAllowedError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, insufficientPermissionFacesMessage);
		}

		return null;
	}

	protected abstract String modifyGroupReservation() throws GroupReservationConflictException, InsufficientPermissionException;

	protected abstract String modifyGroupReservation(Set<ReservationVo> reservationVoSet) throws GroupReservationConflictException, InsufficientPermissionException;
}