package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleEventImpl;
import org.primefaces.model.ScheduleModel;

@Named
@RequestScoped
public class GroupReservationScheduleBean implements Serializable {
	private final ScheduleModel<ScheduleEvent> schedule;

	public GroupReservationScheduleBean() {
		super();
		this.schedule = new ScheduleModel<ScheduleEvent>();
		this.schedule.addEvent(new ScheduleEventImpl("test", new Date(), new Date()));
	}

	public ScheduleModel<ScheduleEvent> getSchedule() {
		return schedule;
	}
}
