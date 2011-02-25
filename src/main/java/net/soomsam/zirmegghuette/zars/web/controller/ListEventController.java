package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.EventService;
import net.soomsam.zirmegghuette.zars.service.bean.EventBean;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ListEventController implements Serializable {
	private final static Logger logger = Logger.getLogger(ListEventController.class);

	@Inject
	private transient EventService eventService;

	public List<EventBean> getLatestEvents() {
		return eventService.findLatestEvents();
	}
}