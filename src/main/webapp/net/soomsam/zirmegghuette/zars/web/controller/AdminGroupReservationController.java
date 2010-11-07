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

	private BeneficiaryFilterOption selectedBeneficiaryFilterOption = BeneficiaryFilterOption.ALL_BENEFICIARY;

	private DateFilterOption selectedDateFilterOption = DateFilterOption.CURRENT_YEAR;

	private Date dateRangeStartDate = new DateMidnight().toDate();

	private Date dateRangeEndDate = new DateMidnight().plusYears(1).toDate();

	@Inject
	private transient GroupReservationService groupReservationService;

	@Inject
	protected transient SecurityController securityController;

	public String getCommandLinkSelectedGroupReservationIdAttributeName() {
		return commandLinkSelectedGroupReservationIdAttributeName;
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

	public void verifyFilterSettings() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			final FacesMessage invalidFilterSettingsFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationFilterSettingWarning", FacesMessage.SEVERITY_WARN, null);
			FacesContext.getCurrentInstance().addMessage(null, invalidFilterSettingsFacesMessage);
		}
	}

	public List<GroupReservationBean> getGroupReservations() {
		return findGroupReservations(null);
	}

	protected List<GroupReservationBean> findGroupReservations(final Pagination pagination) {
		if (BeneficiaryFilterOption.ALL_BENEFICIARY.equals(selectedBeneficiaryFilterOption)) {
			if (DateFilterOption.CURRENT_YEAR.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(createCurrentYearInterval(), pagination);
			} else if (DateFilterOption.SPECIFIED_RANGE.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(createSpecifiedDateRangeInterval(), pagination);
			} else if (DateFilterOption.ALL.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(pagination);
			}
		} else if (BeneficiaryFilterOption.CURRENT_BENEFICIARY.equals(selectedBeneficiaryFilterOption)) {
			long currentUserId = securityController.getCurrentUserId();
			if (DateFilterOption.CURRENT_YEAR.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(currentUserId, createCurrentYearInterval(), pagination);
			} else if (DateFilterOption.SPECIFIED_RANGE.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(currentUserId, createSpecifiedDateRangeInterval(), pagination);
			} else if (DateFilterOption.ALL.equals(selectedDateFilterOption)) {
				return groupReservationService.findGroupReservation(currentUserId, pagination);
			}
		}

		throw new IllegalStateException("unsupported filter combination with benificiary filter [" + selectedBeneficiaryFilterOption + "] and date filter [" + selectedDateFilterOption + "]");
	}

	protected Interval createCurrentYearInterval() {
		// implements BR003
		DateMidnight dateRangeStartDateMidnight = new DateMidnight().withDayOfYear(1);
		DateMidnight dateRangeEndDateMidnight = dateRangeStartDateMidnight.plusYears(1);
		return new Interval(dateRangeStartDateMidnight, dateRangeEndDateMidnight);
	}

	protected Interval createSpecifiedDateRangeInterval() {
		DateMidnight dateRangeStartDateMidnight = new DateMidnight(dateRangeStartDate);
		DateMidnight dateRangeEndDateMidnight = new DateMidnight(dateRangeEndDate);
		return new Interval(dateRangeStartDateMidnight, dateRangeEndDateMidnight);
	}

	public void onDeleteGroupReservation(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink)commandLinkActionEvent.getComponent();
			final Long selectedGroupReservationId = (Long)commandLink.getAttributes().get(commandLinkSelectedGroupReservationIdAttributeName);

			logger.debug("deleting group reservation with groupReservationId [" + selectedGroupReservationId + "]");
			try {
				groupReservationService.deleteGroupReservation(selectedGroupReservationId);
			} catch (InsufficientPermissionException insufficientPermissionException) {
				final FacesMessage insufficientPermissionFacesMessage = MessageFactory.getMessage("sectionsApplicationGroupReservationDeletionNotAllowedError", FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, insufficientPermissionFacesMessage);
			}
		}
	}

	public String applyFilter() {
		return "adminGroupReservation?faces-redirect=true&includeViewParams=true";
	}
}