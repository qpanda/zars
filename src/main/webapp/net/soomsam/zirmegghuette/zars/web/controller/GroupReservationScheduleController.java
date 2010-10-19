package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class GroupReservationScheduleController implements Serializable {
	private final ScheduleModel schedule;

	public GroupReservationScheduleController() {
		super();
		this.schedule = new DefaultScheduleModel();
		this.schedule.addEvent(new DefaultScheduleEvent("test", new Date(), new Date()));
	}

	public ScheduleModel getSchedule() {
		return schedule;
	}

	public void onEventSelect(final ScheduleEntrySelectEvent scheduleEntrySelectEvent) {
		ScheduleEvent scheduleEvent = scheduleEntrySelectEvent.getScheduleEvent();
		System.out.println("###" + scheduleEvent.getTitle());
	}
}
