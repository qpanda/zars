package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.EventService;
import net.soomsam.zirmegghuette.zars.service.bean.EventBean;

import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ListEventController implements Serializable {
	@Inject
	private transient EventService eventService;

	public List<EventBean> getLatestEvents() {
		return eventService.findLatestEvents();
	}
}