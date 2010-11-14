package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.TimeZone;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

@Named
@Scope("session")
public class SettingController implements Serializable {
	private final static Logger logger = Logger.getLogger(SettingController.class);

	public TimeZone getDefaultTimeZone() {
		return TimeZone.getDefault();
	}
}
