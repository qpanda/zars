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
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.ReservationBean;
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
public class EditGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(EditGroupReservationController.class);

	private final static String ARRIVALCALENDAR_RESERVATIONCOMPONENT_IDPREFIX = "arrival";
	private final static String DEPARTURECALENDAR_RESERVATIONCOMPONENT_IDPREFIX = "depature";
	private final static String FIRSTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX = "firstName";
	private final static String LASTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX = "lastName";

	@Inject
	private transient LocaleController localController;

	@Inject
	private transient UserService userService;

	@Inject
	private transient GroupReservationService groupReservationService;

	@Inject
	private transient LocaleController localeController;

	private boolean validNavigation = true;

	private Long groupReservationId;

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

	public boolean isValidNavigation() {
		return validNavigation;
	}

	public Long getGroupReservationId() {
		return groupReservationId;
	}

	public void setGroupReservationId(final Long groupReservationId) {
		this.groupReservationId = groupReservationId;
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

	public void retrieveGroupReservation() {
		if (null != this.groupReservationId) {
			try {
				final GroupReservationBean groupReservationBean = groupReservationService.retrieveGroupReservation(groupReservationId);
				this.groupReservationId = groupReservationBean.getGroupReservationId();
				this.arrival = groupReservationBean.getArrival();
				this.departure = groupReservationBean.getDeparture();
				this.guests = groupReservationBean.getGuests();
				this.comment = groupReservationBean.getComment();
				this.selectedAccountantId = groupReservationBean.getAccountant().getUserId();

				List<ReservationBean> reservationBeanList = groupReservationBean.getReservations();
				if ((null != reservationBeanList) && !reservationBeanList.isEmpty()) {
					populateReservation(reservationBeanList);
				}
			} catch (final EntityNotFoundException entityNotFoundException) {
				this.validNavigation = false;
				// TODO change message
				final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
				FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			}
		}

		if (!FacesContext.getCurrentInstance().isPostback() && (null == this.groupReservationId)) {
			this.validNavigation = false;
			// TODO change message
			final FacesMessage invalidUserIdFacesMessage = MessageFactory.getMessage("sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		}
	}

	protected void populateReservation(final List<ReservationBean> reservationBeanList) {
		for (int i = 0; i < reservationBeanList.size(); ++i) {
			ReservationBean reservationBean = reservationBeanList.get(i);
			populateArrivalCalendarReservationComponent(i + 1, reservationBean.getArrival());
			populateDepartureCalendarReservationComponent(i + 1, reservationBean.getDeparture());
			populateFirstNameInputTextReservationComponent(i + 1, reservationBean.getFirstName());
			populateLastNameInputTextReservationComponent(i + 1, reservationBean.getLastName());
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

	protected void populateArrivalCalendarReservationComponent(final int reservationPanelRow, final Date individualReservationArrival) {
		String arrivalCalendarReservationComponentId = determineReservationComponentId(ARRIVALCALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar arrivalCalendarReservationComponent = createCalendarReservationComponent(arrivalCalendarReservationComponentId, null);
		arrivalCalendarReservationComponent.setValue(individualReservationArrival);
		arrivalCalendarReservationComponent.setValid(true);
		reservationPanelGrid.getChildren().add(arrivalCalendarReservationComponent);
	}

	protected void populateDepartureCalendarReservationComponent(final int reservationPanelRow, final Date individualReservationDeparture) {
		String departureCalendarReservationComponentId = determineReservationComponentId(DEPARTURECALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar departureCalendarReservationComponent = createCalendarReservationComponent(departureCalendarReservationComponentId, null);
		departureCalendarReservationComponent.setValue(individualReservationDeparture);
		departureCalendarReservationComponent.setValid(true);
		reservationPanelGrid.getChildren().add(departureCalendarReservationComponent);
	}

	protected void populateFirstNameInputTextReservationComponent(final int reservationPanelRow, final String individualReservationFirstName) {
		String firstNameInputTextReservationComponentId = determineReservationComponentId(FIRSTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText firstNameInputTextReservationComponent = createInputTextReservationComponent();
		firstNameInputTextReservationComponent.setValue(individualReservationFirstName);
		firstNameInputTextReservationComponent.setSubmittedValue(individualReservationFirstName);
		firstNameInputTextReservationComponent.setLocalValueSet(false);
		firstNameInputTextReservationComponent.setValid(true);
		firstNameInputTextReservationComponent.setId(firstNameInputTextReservationComponentId);
		firstNameInputTextReservationComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationIndividualGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(firstNameInputTextReservationComponent);
	}

	protected void populateLastNameInputTextReservationComponent(final int reservationPanelRow, final String individualReservationLastName) {
		String lastNameInputTextReservationComponentId = determineReservationComponentId(LASTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText lastNameInputTextReservationComponent = createInputTextReservationComponent();
		lastNameInputTextReservationComponent.setValue(individualReservationLastName);
		lastNameInputTextReservationComponent.setSubmittedValue(individualReservationLastName);
		lastNameInputTextReservationComponent.setLocalValueSet(false);
		lastNameInputTextReservationComponent.setValid(true);
		lastNameInputTextReservationComponent.setId(lastNameInputTextReservationComponentId);
		lastNameInputTextReservationComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationIndividualGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(lastNameInputTextReservationComponent);
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

	public void removeReservation(final ActionEvent commandLinkActionEvent) {
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
		addArrivalCalendarReservationComponent(reservationPanelRow);
		addDepartureCalendarReservationComponent(reservationPanelRow);
		addFirstNameInputTextReservationComponent(reservationPanelRow);
		addLastNameInputTextReservationComponent(reservationPanelRow);
	}

	protected void addArrivalCalendarReservationComponent(final int reservationPanelRow) {
		Calendar templateArrivalCalendar = (Calendar) determineReservationComponent(determineReservationComponentId(ARRIVALCALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow - 1));
		if (null == templateArrivalCalendar) {
			templateArrivalCalendar = arrivalCalendar;
		}

		String arrivalCalendarReservationComponentId = determineReservationComponentId(ARRIVALCALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar arrivalCalendarReservationComponent = createCalendarReservationComponent(arrivalCalendarReservationComponentId, templateArrivalCalendar);
		reservationPanelGrid.getChildren().add(arrivalCalendarReservationComponent);
	}

	protected void addDepartureCalendarReservationComponent(final int reservationPanelRow) {
		Calendar templateDepartureCalendar = (Calendar) determineReservationComponent(determineReservationComponentId(DEPARTURECALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow - 1));
		if (null == templateDepartureCalendar) {
			templateDepartureCalendar = departureCalendar;
		}

		String departureCalendarReservationComponentId = determineReservationComponentId(DEPARTURECALENDAR_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		Calendar departureCalendarReservationComponent = createCalendarReservationComponent(departureCalendarReservationComponentId, templateDepartureCalendar);
		reservationPanelGrid.getChildren().add(departureCalendarReservationComponent);
	}

	protected void addFirstNameInputTextReservationComponent(final int reservationPanelRow) {
		String firstNameInputTextReservationComponentId = determineReservationComponentId(FIRSTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText firstNameInputTextReservationComponent = createInputTextReservationComponent();
		firstNameInputTextReservationComponent.setId(firstNameInputTextReservationComponentId);
		firstNameInputTextReservationComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationIndividualGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(firstNameInputTextReservationComponent);
	}

	protected void addLastNameInputTextReservationComponent(final int reservationPanelRow) {
		String lastNameInputTextReservationComponentId = determineReservationComponentId(LASTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, reservationPanelRow);
		HtmlInputText lastNameInputTextReservationComponent = createInputTextReservationComponent();
		lastNameInputTextReservationComponent.setId(lastNameInputTextReservationComponentId);
		lastNameInputTextReservationComponent.setRequiredMessage(MessageFactory.getMessage("sectionsApplicationGroupReservationIndividualGuestNameError", reservationPanelRow).getDetail());
		reservationPanelGrid.getChildren().add(lastNameInputTextReservationComponent);
	}

	protected Calendar createCalendarReservationComponent(final String calendarReservationComponentId, final Calendar templateCalendar) {
		Calendar calendarReservationComponent = new Calendar();
		calendarReservationComponent.setId(calendarReservationComponentId);
		calendarReservationComponent.setMode("popup");
		calendarReservationComponent.setShowOn("button");
		calendarReservationComponent.setPopupIconOnly(true);
		calendarReservationComponent.setReadOnlyInputText(true);
		calendarReservationComponent.setLocale(localController.getActiveLocale());
		calendarReservationComponent.setPattern(localeController.getActiveDateFormatPattern());
		calendarReservationComponent.setRequired(true);
		calendarReservationComponent.setInputStyleClass("applicationFormInput");

		if (null != templateCalendar) {
			String templateCalendarSubmittedValue = (String) templateCalendar.getSubmittedValue();
			if (!StringUtils.isEmpty(templateCalendarSubmittedValue)) {
				calendarReservationComponent.setValue(templateCalendar.getValue());
				calendarReservationComponent.setSubmittedValue(templateCalendar.getSubmittedValue());
				calendarReservationComponent.setLocalValueSet(templateCalendar.isLocalValueSet());
				calendarReservationComponent.setValid(templateCalendar.isValid());
			}
		}

		return calendarReservationComponent;
	}

	protected HtmlInputText createInputTextReservationComponent() {
		HtmlInputText inputTextReservationComponent = new HtmlInputText();
		inputTextReservationComponent.setStyleClass("applicationFormInput");
		inputTextReservationComponent.setRequired(true);
		return inputTextReservationComponent;
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

		return createGroupReservationWithIndividualReservations();
	}

	protected String createGroupReservation() {
		if (!validArrivalDepartureDateRange(arrival, departure)) {
			String arrivalValue = localeController.getActiveDateFormat().format(arrival);
			String departureValue = localController.getActiveDateFormat().format(departure);
			final FacesMessage arrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, arrivalValue, departureValue);
			FacesContext.getCurrentInstance().addMessage(null, arrivalDepatureFacesMessage);
			return null;
		}

		logger.debug("creating group reservation [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		try {
			// TODO selectAccountantId: actual beneficiary is current user
			savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedAccountantId, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
			return "editGroupReservationConfirmation";
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = localController.getActiveDateFormat().format(conflictingGroupReservation.getDeparture());
				final FacesMessage groupReservationConflictFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationConflictError", FacesMessage.SEVERITY_ERROR, conflictingGroupReservation.getGroupReservationId(), arrivalValue, departureValue, conflictingGroupReservation.getBeneficiary().getUsername());
				FacesContext.getCurrentInstance().addMessage(null, groupReservationConflictFacesMessage);
			}
		}

		return null;
	}

	protected String createGroupReservationWithIndividualReservations() {
		Set<ReservationVo> reservationVoSet = new HashSet<ReservationVo>();
		for (int i = 1; i <= determineReservationCount(); ++i) {
			String arrivalCalendarReservationComponentId = determineReservationComponentId(ARRIVALCALENDAR_RESERVATIONCOMPONENT_IDPREFIX, i);
			Calendar arrivalCalendarReservationComponent = (Calendar) determineReservationComponent(arrivalCalendarReservationComponentId);
			Date individualReservationArrival = (Date) arrivalCalendarReservationComponent.getValue();

			String departureCalendarReservationComponentId = determineReservationComponentId(DEPARTURECALENDAR_RESERVATIONCOMPONENT_IDPREFIX, i);
			Calendar departureCalendarReservationComponent = (Calendar) determineReservationComponent(departureCalendarReservationComponentId);
			Date individualReservationDeparture = (Date) departureCalendarReservationComponent.getValue();

			if (!validArrivalDepartureDateRange(individualReservationArrival, individualReservationDeparture)) {
				String individualReservationArrivalValue = localeController.getActiveDateFormat().format(individualReservationArrival);
				String individualReservationDepartureValue = localController.getActiveDateFormat().format(individualReservationDeparture);
				final FacesMessage individualArrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationIndividualArrivalDepartureError", FacesMessage.SEVERITY_ERROR, individualReservationArrivalValue, individualReservationDepartureValue, i);
				FacesContext.getCurrentInstance().addMessage(null, individualArrivalDepatureFacesMessage);
				return null;
			}

			String firstNameReservationComponentId = determineReservationComponentId(FIRSTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, i);
			HtmlInputText firstNameReservationComponent = (HtmlInputText) determineReservationComponent(firstNameReservationComponentId);
			String individualReservationFirstName = (String) firstNameReservationComponent.getValue();

			String lastNameReservationComponentId = determineReservationComponentId(LASTNAMEINPUTTEXT_RESERVATIONCOMPONENT_IDPREFIX, i);
			HtmlInputText lastNameReservationComponent = (HtmlInputText) determineReservationComponent(lastNameReservationComponentId);
			String individualReservationLastName = (String) lastNameReservationComponent.getValue();

			ReservationVo reservationVo = new ReservationVo(i, new DateMidnight(individualReservationArrival), new DateMidnight(individualReservationDeparture), individualReservationFirstName, individualReservationLastName);
			reservationVoSet.add(reservationVo);
		}

		logger.debug("creating group reservation with [" + determineReservationCount() + "] individual reservations");
		try {
			// TODO selectAccountantId: actual beneficiary is current user
			savedGroupReservation = groupReservationService.updateGroupReservation(groupReservationId, selectedAccountantId, selectedAccountantId, reservationVoSet, comment);
			return "editGroupReservationConfirmation";
		} catch (final GroupReservationConflictException groupReservationConflictException) {
			List<GroupReservationBean> conflictingGroupReservationList = groupReservationConflictException.getConflictingGroupReservations();
			for (GroupReservationBean conflictingGroupReservation : conflictingGroupReservationList) {
				String arrivalValue = localeController.getActiveDateFormat().format(conflictingGroupReservation.getArrival());
				String departureValue = localController.getActiveDateFormat().format(conflictingGroupReservation.getDeparture());
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