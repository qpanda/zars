package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.exception.InsufficientPermissionException;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.model.LazyDataModel;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
public class AdminGroupReservationController implements Serializable {
	private final static Logger logger = Logger.getLogger(AdminGroupReservationController.class);

	private final String commandLinkSelectedGroupReservationIdAttributeName = "selectedGroupReservationId";

	private Long selectedGroupReservationId;

	private final LazyGroupReservationDataModel lazyGroupReservationDataModel = new LazyGroupReservationDataModel();

	@Inject
	private transient GroupReservationService groupReservationService;

	public String getCommandLinkSelectedGroupReservationIdAttributeName() {
		return commandLinkSelectedGroupReservationIdAttributeName;
	}

	public Long getSelectedGroupReservationId() {
		return selectedGroupReservationId;
	}

	public void setSelectedGroupReservationId(final Long selectedGroupReservationId) {
		this.selectedGroupReservationId = selectedGroupReservationId;
	}

	public LazyGroupReservationDataModel getLazyGroupReservationDataModel() {
		return lazyGroupReservationDataModel;
	}

	public void setSelectedGroupReservationId(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink) commandLinkActionEvent.getComponent();
			final Long commandLinkParameterValue = (Long) commandLink.getAttributes().get(commandLinkSelectedGroupReservationIdAttributeName);
			setSelectedGroupReservationId(commandLinkParameterValue);
		}
	}

	public String deleteGroupReservation() {
		logger.debug("deleting group reservation with groupReservationId [" + selectedGroupReservationId + "]");
		try {
			groupReservationService.deleteGroupReservation(selectedGroupReservationId);
			return "adminGroupReservation?faces-redirect=true";
		} catch (InsufficientPermissionException insufficientPermissionException) {
			final FacesMessage insufficientPermissionFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationDeletionNotAllowedError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, insufficientPermissionFacesMessage);
		}

		return null;
	}

	private class LazyGroupReservationDataModel extends LazyDataModel<GroupReservationBean> {
		@Override
		public List<GroupReservationBean> fetchLazyData(final int firstResult, final int maxResults) {
			// TODO determine date range from form parameters
			DateMidnight end = new DateMidnight();
			DateMidnight begin = end.minusYears(1);
			return groupReservationService.findGroupReservation(new Interval(begin, end), new Pagination(firstResult, maxResults));
		}
	}
}