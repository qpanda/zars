package net.soomsam.zirmegghuette.zars.manager.stateless;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import net.soomsam.zirmegghuette.zars.manager.ManagerException;
import net.soomsam.zirmegghuette.zars.manager.TemplateManager;
import net.soomsam.zirmegghuette.zars.web.utils.LocaleUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;

@Component("templateManager")
public class StatelessTemplateManager implements TemplateManager {
	@Autowired
	private Configuration freemarkerConfiguration;

	@Override
	public String generateDocument(final String templateName, final TemplateHashModel templateModel, final Locale preferredLocale, final TimeZone preferredTimeZone) {
		final StringWriter stringMessageWriter = new StringWriter();
		final Template template = prepareTemplate(templateName, preferredLocale, preferredTimeZone);

		try {
			template.process(templateModel, stringMessageWriter);
			return stringMessageWriter.toString();
		} catch (final TemplateException templateException) {
			throw new ManagerException("unable to process template [" + templateName + "] with template model [" + templateModel + "]", templateException);
		} catch (final IOException ioException) {
			throw new ManagerException("unable to generate output for template [" + templateName + "] with template model [" + templateModel + "]", ioException);
		}
	}

	protected Template prepareTemplate(final String templateName, final Locale preferredLocale, final TimeZone preferredTimeZone) {
		try {
			final Template template = freemarkerConfiguration.getTemplate(templateName);
			applyLocaleSettings(template, preferredLocale, preferredTimeZone);
			return template;
		} catch (final IOException ioException) {
			throw new ManagerException("loading template [" + templateName + "] failed", ioException);
		}
	}

	protected void applyLocaleSettings(final Template template, final Locale preferredLocale, final TimeZone preferredTimeZone) {
		final SimpleDateFormat preferredDateTimeFormat = LocaleUtils.determinePreferredDateTimeFormat(preferredLocale, preferredTimeZone);
		final SimpleDateFormat preferredDateFormat = LocaleUtils.determinePreferredDateFormat(preferredLocale, preferredTimeZone);
		final SimpleDateFormat preferredTimeFormat = LocaleUtils.determinePreferredTimeFormat(preferredLocale, preferredTimeZone);

		template.setLocale(preferredLocale);
		template.setTimeZone(preferredTimeZone);
		template.setDateTimeFormat(preferredDateTimeFormat.toPattern());
		template.setDateFormat(preferredDateFormat.toPattern());
		template.setTimeFormat(preferredTimeFormat.toPattern());
	}
}
