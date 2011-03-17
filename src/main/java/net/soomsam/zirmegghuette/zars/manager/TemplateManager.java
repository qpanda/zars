package net.soomsam.zirmegghuette.zars.manager;

import java.util.Locale;
import java.util.TimeZone;

import freemarker.template.TemplateHashModel;

public interface TemplateManager {
	public String generateDocument(String templateName, TemplateHashModel templateModel, Locale preferredLocale, TimeZone preferredTimeZone);
}
