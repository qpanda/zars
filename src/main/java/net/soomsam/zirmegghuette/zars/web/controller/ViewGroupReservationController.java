package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ViewGroupReservationController implements Serializable {
	@Inject
	protected transient GroupReservationService groupReservationService;

	@Inject
	protected transient SettingController settingController;

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
			savedGroupReservation = groupReservationService.retrieveGroupReservation(groupReservationId);
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidGroupReservationIdFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationGroupReservationGroupReservationIdError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, invalidGroupReservationIdFacesMessage);
		}
	}
}