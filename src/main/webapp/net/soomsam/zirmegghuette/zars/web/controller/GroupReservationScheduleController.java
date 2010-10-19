package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class GroupReservationScheduleController implements Serializable {
	private final ScheduleModel groupReservationScheduleModel = new LazyGroupReservationScheduleModel();
	private ScheduleEvent selectedGroupReservationScheduleEvent;

	@Inject
	private transient GroupReservationService groupReservationService;

	public ScheduleModel getGroupReservationScheduleModel() {
		return groupReservationScheduleModel;
	}

	public ScheduleEvent getSelectedGroupReservationScheduleEvent() {
		return selectedGroupReservationScheduleEvent;
	}

	public void onGroupReservationEventSelect(final ScheduleEntrySelectEvent scheduleEntrySelectEvent) {
		selectedGroupReservationScheduleEvent = scheduleEntrySelectEvent.getScheduleEvent();

		// String pagePath="myPage.jsf";
		// FacesContext context = FacesContext.getCurrentInstance();
		// context.getExternalContext().redirect(pagePath);
		// context.responseComplete();
	}

	private class LazyGroupReservationScheduleModel extends LazyScheduleModel {
		@Override
		public void loadEvents(final Date dateRangeStartDate, final Date dateRangeEndDate) {
			groupReservationScheduleModel.clear();
			List<GroupReservationBean> groupReservationBeanList = groupReservationService.findGroupReservation(createSelectedDateRangeInterval(dateRangeStartDate, dateRangeEndDate), null);
			for (GroupReservationBean groupReservationBean : groupReservationBeanList) {
				ScheduleEvent groupReservationScheduleEvent = new DefaultScheduleEvent(groupReservationBean.getBeneficiary().getUsername(), groupReservationBean.getArrival(), groupReservationBean.getDeparture(), groupReservationBean);
				groupReservationScheduleModel.addEvent(groupReservationScheduleEvent);
			}
		}

		protected Interval createSelectedDateRangeInterval(final Date dateRangeStartDate, final Date dateRangeEndDate) {
			DateMidnight dateRangeStartDateMidnight = new DateMidnight(dateRangeStartDate);
			DateMidnight dateRangeEndDateMidnight = new DateMidnight(dateRangeEndDate);
			return new Interval(dateRangeStartDateMidnight, dateRangeEndDateMidnight);
		}
	}
}
