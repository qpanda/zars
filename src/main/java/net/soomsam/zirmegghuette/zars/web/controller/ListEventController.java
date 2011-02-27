package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.EventService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.EventBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.springframework.context.annotation.Scope;

import com.sun.faces.util.MessageFactory;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ListEventController implements Serializable {
	private enum CriteriaFilterOption {
		LATEST, SPECIFIED_DATERANGE, SPECIFIED_USER;
	}

	private CriteriaFilterOption selectedCriteriaFilterOption = CriteriaFilterOption.LATEST;

	private Date dateRangeStartDate = new DateMidnight().minusMonths(1).toDate();

	private Date dateRangeEndDate = new DateMidnight().toDate();

	private Long selectedUserId;

	@Inject
	private transient EventService eventService;

	@Inject
	private transient UserService userService;

	public CriteriaFilterOption getSelectedCriteriaFilterOption() {
		return selectedCriteriaFilterOption;
	}

	public void setSelectedCriteriaFilterOption(final CriteriaFilterOption selectedCriteriaFilterOption) {
		this.selectedCriteriaFilterOption = selectedCriteriaFilterOption;
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

	public Long getSelectedUserId() {
		return selectedUserId;
	}

	public void setSelectedUserId(final Long selectedUserId) {
		this.selectedUserId = selectedUserId;
	}

	public void verifyFilterSettings() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			final FacesMessage invalidFilterSettingsFacesMessage = MessageFactory.getMessage("sectionsApplicationEventFilterSettingWarning", FacesMessage.SEVERITY_WARN, (Object[]) null);
			FacesContext.getCurrentInstance().addMessage(null, invalidFilterSettingsFacesMessage);
		}
	}

	public List<UserBean> getAvailableUsers() {
		return userService.findAllUsers();
	}

	public List<EventBean> getEvents() {
		return findEvents(new Pagination(0, 200));
	}

	protected List<EventBean> findEvents(final Pagination pagination) {
		if (CriteriaFilterOption.LATEST.equals(selectedCriteriaFilterOption)) {
			return eventService.findLatestEvents(pagination);
		} else if (CriteriaFilterOption.SPECIFIED_DATERANGE.equals(selectedCriteriaFilterOption)) {
			return eventService.findEventByOpenDateInterval(createSpecifiedDateRangeInterval(), pagination);
		} else if (CriteriaFilterOption.SPECIFIED_USER.equals(selectedCriteriaFilterOption)) {
			return eventService.findEventByUserId(selectedUserId, pagination);
		}

		throw new IllegalStateException("unsupported criteria filter option [" + selectedCriteriaFilterOption + "]");
	}

	protected Interval createSpecifiedDateRangeInterval() {
		final DateMidnight dateRangeStartDateMidnight = new DateMidnight(dateRangeStartDate);
		final DateMidnight dateRangeEndDateMidnight = new DateMidnight(dateRangeEndDate);
		return new Interval(dateRangeStartDateMidnight, dateRangeEndDateMidnight);
	}

	public String applyFilter() {
		return "listEvent?faces-redirect=true&includeViewParams=true";
	}
}