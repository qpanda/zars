package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class AddGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(AddGroupReservationController.class);

	@Inject
	private transient UserService userService;
	
	@Inject
	private transient GroupReservationService groupReservationService;

	private Date arrival;

	private Date departure;

	private long guests;

	private List<UserBean> availableAccountants;

	private Long selectedAccountantId;
	
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

	public long getGuests() {
		return guests;
	}

	public void setGuests(long guests) {
		this.guests = guests;
	}

	public GroupReservationBean getSavedGroupReservation() {
		return savedGroupReservation;
	}
	
	public List<UserBean> getAvailableAccountants() {
		if (null == availableAccountants) {
			availableAccountants = userService.findUsers(RoleType.ROLE_ACCOUNTANT);
		}

		return availableAccountants;
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
		return null;
	}
}