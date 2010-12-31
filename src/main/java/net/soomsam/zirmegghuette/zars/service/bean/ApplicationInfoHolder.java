package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Properties;

public class ApplicationInfoHolder {
	private Properties applicationInfoProperties = new Properties();

	public Properties getApplicationInfoProperties() {
		return applicationInfoProperties;
	}

	public void setApplicationInfoProperties(final Properties applicationInfoProperties) {
		this.applicationInfoProperties = applicationInfoProperties;
	}
}
