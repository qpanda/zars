package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateMidnight;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class AddGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(AddGroupReservationController.class);

	@Inject
	private transient UserService userService;

	@Inject
	private transient GroupReservationService groupReservationService;

	@NotNull(message = "{sectionsApplicationGroupReservationArrivalError}")
	private Date arrival;

	@NotNull(message = "{sectionsApplicationGroupReservationDepartureError}")
	private Date departure;

	@Min(value = 1, message = "{sectionsApplicationGroupReservationGuestsError}")
	@NotNull(message = "{sectionsApplicationGroupReservationGuestsError}")
	private Long guests;

	private Long selectedAccountantId;

	@Length(min = 0, max = 512, message = "{sectionsApplicationGroupReservationCommentError}")
	private String comment;

	private GroupReservationBean savedGroupReservation;

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		this.arrival = arrival;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Long getGuests() {
		return guests;
	}

	public void setGuests(Long guests) {
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

	public void setSelectedAccountantId(Long selectedAccountantId) {
		this.selectedAccountantId = selectedAccountantId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String create() {
		if (!validDateRange()) {
			final FacesMessage arrivalDepatureFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationArrivalDepartureError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, arrivalDepatureFacesMessage);
			return null;
		}
		
		logger.debug("creating group reservation [" + arrival + "]-[" + departure + "] for [" + guests + "] guests");
		savedGroupReservation = groupReservationService.createGroupReservation(selectedAccountantId /*TODO actual beneficiary is current user*/, selectedAccountantId, new DateMidnight(arrival), new DateMidnight(departure), guests, comment);
		return "addGroupReservationConfirmation";
	}

	protected boolean validDateRange() {
		DateMidnight arrivalDateMidnight = new DateMidnight(arrival);
		DateMidnight departureDateMidnight = new DateMidnight(departure);
		return departureDateMidnight.isAfter(arrivalDateMidnight);
	}
}