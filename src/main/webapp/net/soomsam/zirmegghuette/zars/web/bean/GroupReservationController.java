package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEventImpl;
import org.primefaces.model.ScheduleModel;

@Named
@RequestScoped
public class GroupReservationController implements Serializable {
	private final ScheduleModel schedule;

	public GroupReservationController() {
		super();
		this.schedule = new DefaultScheduleModel();
		this.schedule.addEvent(new ScheduleEventImpl("test", new Date(), new Date()));
	}

	public ScheduleModel getSchedule() {
		return schedule;
	}
}
