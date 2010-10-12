package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;
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
	private enum BeneficiaryFilterOption {
		ALL_BENEFICIARY, CURRENT_BENEFICIARY;
	}

	private enum DateFilterOption {
		CURRENT_YEAR, SPECIFIED_RANGE, ALL;
	}

	private final static Logger logger = Logger.getLogger(AdminGroupReservationController.class);

	private final String commandLinkSelectedGroupReservationIdAttributeName = "selectedGroupReservationId";

	private Long selectedGroupReservationId;

	private BeneficiaryFilterOption selectedBeneficiaryFilterOption = BeneficiaryFilterOption.ALL_BENEFICIARY;

	private DateFilterOption selectedDateFilterOption = DateFilterOption.CURRENT_YEAR;

	private Date dateRangeStartDate;

	private Date dateRangeEndDate;

	private final LazyGroupReservationDataModel lazyGroupReservationDataModel = new LazyGroupReservationDataModel();

	@Inject
	private transient GroupReservationService groupReservationService;

	public AdminGroupReservationController() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			setDefaultDateRangeStartDate();
			setDefaultDateRangeEndDate();
		}
	}

	public String getCommandLinkSelectedGroupReservationIdAttributeName() {
		return commandLinkSelectedGroupReservationIdAttributeName;
	}

	public Long getSelectedGroupReservationId() {
		return selectedGroupReservationId;
	}

	public void setSelectedGroupReservationId(final Long selectedGroupReservationId) {
		this.selectedGroupReservationId = selectedGroupReservationId;
	}

	public BeneficiaryFilterOption getSelectedBeneficiaryFilterOption() {
		return selectedBeneficiaryFilterOption;
	}

	public void setSelectedBeneficiaryFilterOption(final BeneficiaryFilterOption selectedBeneficiaryFilterOption) {
		this.selectedBeneficiaryFilterOption = selectedBeneficiaryFilterOption;
	}

	public DateFilterOption getSelectedDateFilterOption() {
		return selectedDateFilterOption;
	}

	public void setSelectedDateFilterOption(final DateFilterOption selectedDateFilterOption) {
		this.selectedDateFilterOption = selectedDateFilterOption;
	}

	public Date getDateRangeStartDate() {
		return dateRangeStartDate;
	}

	public void setDateRangeStartDate(final Date dateRangeStartDate) {
		this.dateRangeStartDate = dateRangeStartDate;
	}

	public Date getDateRangeEndDate() {
		return dateRangeEndDate;
	}

	public void setDateRangeEndDate(final Date dateRangeEndDate) {
		this.dateRangeEndDate = dateRangeEndDate;
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

	private void setDefaultDateRangeStartDate() {
		this.dateRangeStartDate = new DateMidnight().toDate();
	}

	private void setDefaultDateRangeEndDate() {
		this.dateRangeEndDate = new DateMidnight().plusYears(1).toDate();
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

	public String applyFilter() {
		return null;
	}

	private class LazyGroupReservationDataModel extends LazyDataModel<GroupReservationBean> {
		@Override
		public int getRowCount() {
			// TODO exists countGroupReservation(Interval)
			// TODO countGroupReservation(Interval, User)
			// TODO countGroupReservation(User)
			// TODO countGroupReservation()

			// TODO determine date range from form parameters
			DateMidnight end = new DateMidnight();
			DateMidnight begin = end.minusYears(1);
			return (int) groupReservationService.countGroupReservation(new Interval(begin, end));
		}

		@Override
		public List<GroupReservationBean> fetchLazyData(final int firstResult, final int maxResults) {
			// TODO exists findGroupReservation(Interval, Pagination)
			// TODO findGroupReservation(Interval, User, Pagination)
			// TODO findGroupReservation(User, Pagination)
			// TODO findGroupReservation(Pagination)

			// TODO determine date range from form parameters
			DateMidnight end = new DateMidnight();
			DateMidnight begin = end.minusYears(1);
			return groupReservationService.findGroupReservation(new Interval(begin, end), new Pagination(firstResult, maxResults));
		}
	}
}