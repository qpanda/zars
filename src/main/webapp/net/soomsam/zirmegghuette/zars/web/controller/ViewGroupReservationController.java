package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class ViewGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(ViewGroupReservationController.class);

	@Inject
	protected transient GroupReservationService groupReservationService;

	private boolean validNavigation = true;

	private Long groupReservationId;

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

	public GroupReservationBean getSavedGroupReservation() {
		return savedGroupReservation;
	}

	public void setSavedGroupReservation(final GroupReservationBean savedGroupReservation) {
		this.savedGroupReservation = savedGroupReservation;
	}

	public void retrieveGroupReservation() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (null == this.groupReservationId) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
			return;
		}

		try {
			savedGroupReservation = groupReservationService.retrieveGroupReservation(groupReservationId);
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
		}
	}
}