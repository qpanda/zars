package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
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
import javax.inject.Named;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.GroupReservationConflictException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.vo.ReservationVo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateMidnight;
import org.primefaces.component.calendar.Calendar;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class AddGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(AddGroupReservationController.class);

	private final static String RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX = "reservationArrival";
	private final static String RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX = "reservationDepature";
	private final static String RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX = "reservationFirstName";
	private final static String RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX = "reservationLastName";

	@Inject
	private transient LocaleController localeController;

	@Inject
	private transient UserService userService;

	@Inject
	private transient GroupReservationService groupReservationService;

	private Date arrival;

	private Date departure;

	@Min(value = 1, message = "{sectionsApplicationGroupReservationGuestsError}")
	@NotNull(message = "{sectionsApplicationGroupReservationGuestsError}")
	private Long guests;

	private Long selectedAccountantId;

	@Length(min = 0, max = 512, message = "{sectionsApplicationGroupReservationCommentError}")
	private String comment;

	private Calendar arrivalCalendar;

	private Calendar departureCalendar;

	private HtmlInputText guestsInputText;

	private HtmlPanelGrid reservationPanelGrid;

	private GroupReservationBean savedGroupReservation;

	public AddGroupReservationController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultArrivalDate();
			setDefaultDepartureDate();
		}
	}

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

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public HtmlPanelGrid getReservationPanelGrid() {
		return reservationPanelGrid;
	}

	public void setReservationPanelGrid(HtmlPanelGrid reservationPanelGrid) {
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
		Calendar templateArrivalCalendar = (Calendar) determineReservationComponent(determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow - 1));
		if (null == templateArrivalCalendar) {
			templateArrivalCalendar = arrivalCalendar;
		}

		String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar reservationArrivalCalendarComponent = createReservationCalendarComponent(reservationArrivalCalendarComponentId, templateArrivalCalendar);
		reservationPanelGrid.getChildren().add(reservationArrivalCalendarComponent);
	}

	protected void addReservationDepartureCalendarComponent(final int reservationPanelRow) {
		Calendar templateDepartureCalendar = (Calendar) determineReservationComponent(determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, reservationPanelRow - 1));
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
		reservationCalendarComponent.setLocale(localeController.getActiveLocale());
		reservationCalendarComponent.setPattern(localeController.getActiveDateFormatPattern());
		reservationCalendarComponent.setRequired(true);
		reservationCalendarComponent.setInputStyleClass("applicationFormInput");

		String templateCalendarSubmittedValue = (String) templateCalendar.getSubmittedValue();
		if (!StringUtils.isEmpty(templateCalendarSubmittedValue)) {
			reservationCalendarComponent.setValue(templateCalendar.getValue());
			reservationCalendarComponent.setSubmittedValue(templateCalendar.getSubmittedValue());
			reservationCalendarComponent.setLocalValueSet(templateCalendar.isLocalValueSet());
			reservationCalendarComponent.setValid(templateCalendar.isValid());
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

	public String create() {
		if (0 == determineReservationCount()) {
			return createGroupReservation();
		}

		return createGroupReservationWithReservations();
	}

	protected String createGroupReservation() {
		if (!validArrivalDepartureDateRange(arrival, departure)) {
			String arrivalValue = localeController.getActiveDateFormat().format(arrival);
			String departureValue = localeController.getActiveDateFormat().format(departure);
			final FacesMessage arrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, arrivalValue, departureValue);
			FacesContext.getCurrentInstance().addMessage(null, arrivalDepatureFacesMessage);
			return null;
		}

		logger.debug("creating group reservation [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		try {
			// TODO selectAccountantId: actual beneficiary is current user
			savedGroupReservation = groupReservationService.createGroupReservation(selectedAccountantId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
			return "addGroupReservationConfirmation";
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getDeparture());
				final FacesMessage groupReservationConflictFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationConflictError", FacesMessage.SEVERITY_ERROR, conflictingGroupReservation.getGroupReservationId(), arrivalValue, departureValue, conflictingGroupReservation.getBeneficiary().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, groupReservationConflictFacesMessage);
			}
		}

		return null;
	}

	protected String createGroupReservationWithReservations() {
		Set<ReservationVo> reservationVoSet = new HashSet<ReservationVo>();
		for (int i = 1; i <= determineReservationCount(); ++i) {
			String reservationArrivalCalendarComponentId = determineReservationComponentId(RESERVATIONARRIVAL_CALENDARCOMPONENT_IDPREFIX, i);
			Calendar reservationArrivalCalendarComponent = (Calendar) determineReservationComponent(reservationArrivalCalendarComponentId);
			Date reservationArrival = (Date) reservationArrivalCalendarComponent.getValue();

			String reservationDepartureCalendarComponentId = determineReservationComponentId(RESERVATIONDEPARTURE_CALENDARCOMPONENT_IDPREFIX, i);
			Calendar reservationDepartureCalendarComponent = (Calendar) determineReservationComponent(reservationDepartureCalendarComponentId);
			Date reservationDeparture = (Date) reservationDepartureCalendarComponent.getValue();

			if (!validArrivalDepartureDateRange(reservationArrival, reservationDeparture)) {
				String reservationArrivalValue = localeController.getActiveDateFormat().format(reservationArrival);
				String reservationDepatureValue = localeController.getActiveDateFormat().format(reservationDeparture);
				final FacesMessage reservationArrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, reservationArrivalValue, reservationDepatureValue, i);
				FacesContext.getCurrentInstance().addMessage(null, reservationArrivalDepatureFacesMessage);
				return null;
			}

			String reservationFirstNameInputTextComponentId = determineReservationComponentId(RESERVATIONFIRSTNAME_INPUTTEXTCOMPONENT_IDPREFIX, i);
			HtmlInputText reservationFirstNameInputTextComponent = (HtmlInputText) determineReservationComponent(reservationFirstNameInputTextComponentId);
			String reservationFirstName = (String) reservationFirstNameInputTextComponent.getValue();

			String reservationLastNameInputTextComponentId = determineReservationComponentId(RESERVATIONLASTNAME_INPUTTEXTCOMPONENT_IDPREFIX, i);
			HtmlInputText reservationLastNameInputTextComponent = (HtmlInputText) determineReservationComponent(reservationLastNameInputTextComponentId);
			String reservationLastName = (String) reservationLastNameInputTextComponent.getValue();

			ReservationVo reservationVo = new ReservationVo(i, new DateMidnight(reservationArrival), new DateMidnight(reservationDeparture), reservationFirstName, reservationLastName);
			reservationVoSet.add(reservationVo);
		}

		logger.debug("creating group reservation with [" + determineReservationCount() + "] reservations");
		try {
			// TODO selectAccountantId: actual beneficiary is current user
			savedGroupReservation = groupReservationService.createGroupReservation(selectedAccountantId, selectedAccountantId, reservationVoSet, comment);
			return "addGroupReservationConfirmation";
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getDeparture());
				final FacesMessage groupReservationConflictFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationConflictError", FacesMessage.SEVERITY_ERROR, conflictingGroupReservation.getGroupReservationId(), arrivalValue, departureValue, conflictingGroupReservation.getBeneficiary().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, groupReservationConflictFacesMessage);
			}
		}

		return null;
	}

	protected boolean validArrivalDepartureDateRange(final Date arrival, final Date departure) {
		DateMidnight arrivalDateMidnight = new DateMidnight(arrival);
		DateMidnight departureDateMidnight = new DateMidnight(departure);
		return departureDateMidnight.isAfter(arrivalDateMidnight);
	}
}