package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.bean.ApplicationInfoHolder;

@Named
@ApplicationScoped
public class ApplicationInfoController implements Serializable {
	@Inject
	private transient ApplicationInfoHolder applicationInfoHolder;

	public Properties getApplicationInfo() {
		return applicationInfoHolder.getApplicationInfoProperties();
	}
}
