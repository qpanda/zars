package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class GroupReservationController implements Serializable {
	private final ScheduleModel schedule;

	public GroupReservationController() {
		super();
		this.schedule = new DefaultScheduleModel();
		this.schedule.addEvent(new DefaultScheduleEvent("test", new Date(), new Date()));
	}

	public ScheduleModel getSchedule() {
		return schedule;
	}
}
