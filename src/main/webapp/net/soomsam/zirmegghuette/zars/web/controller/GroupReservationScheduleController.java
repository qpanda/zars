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
	private GroupReservationBean selectedGroupReservation;

	@Inject
	private transient GroupReservationService groupReservationService;

	public ScheduleModel getGroupReservationScheduleModel() {
		return groupReservationScheduleModel;
	}

	public GroupReservationBean getSelectedGroupReservation() {
		return selectedGroupReservation;
	}

	public void onGroupReservationEventSelect(final ScheduleEntrySelectEvent scheduleEntrySelectEvent) {
		ScheduleEvent selectedGroupReservationScheduleEvent = scheduleEntrySelectEvent.getScheduleEvent();
		selectedGroupReservation = (GroupReservationBean)selectedGroupReservationScheduleEvent.getData();
	}

	private class LazyGroupReservationScheduleModel extends LazyScheduleModel {
		@Override
		public ScheduleEvent getEvent(final String id) {
			ScheduleEvent scheduleEvent = super.getEvent(id);
			if (null != scheduleEvent) {
				return scheduleEvent;
			}

			long groupReservationId = Long.valueOf(id);
			GroupReservationBean groupReservationBean = groupReservationService.retrieveGroupReservation(groupReservationId);
			return createScheduleEvent(groupReservationBean);
		}

		@Override
		public void loadEvents(final Date dateRangeStartDate, final Date dateRangeEndDate) {
			clear();
			List<GroupReservationBean> groupReservationBeanList = groupReservationService.findGroupReservation(createSelectedDateRangeInterval(dateRangeStartDate, dateRangeEndDate), null);
			for (GroupReservationBean groupReservationBean : groupReservationBeanList) {
				ScheduleEvent groupReservationScheduleEvent = createScheduleEvent(groupReservationBean);
				addEvent(groupReservationScheduleEvent);
			}
		}

		protected ScheduleEvent createScheduleEvent(final GroupReservationBean groupReservationBean) {
			// TODO name formatter
			return new LazyDefaultScheduleEvent(String.valueOf(groupReservationBean.getGroupReservationId()), groupReservationBean.getBeneficiary().getUsername(), groupReservationBean.getArrival(), groupReservationBean.getDeparture(), groupReservationBean);
		}

		protected Interval createSelectedDateRangeInterval(final Date dateRangeStartDate, final Date dateRangeEndDate) {
			DateMidnight dateRangeStartDateMidnight = new DateMidnight(dateRangeStartDate);
			DateMidnight dateRangeEndDateMidnight = new DateMidnight(dateRangeEndDate);
			return new Interval(dateRangeStartDateMidnight, dateRangeEndDateMidnight);
		}
	}

	private class LazyDefaultScheduleEvent extends DefaultScheduleEvent {
		public LazyDefaultScheduleEvent(final String id, final String title, final Date start, final Date end, final Object data) {
			super(title, start, end, data);
			super.setId(id);
		}

		@Override
		public String getId() {
			return super.getId();
		}

		@Override
		public void setId(final String id) {
			if (null == super.getId()) {
				super.setId(id);
			}
		}
	}
}
