package net.soomsam.zirmegghuette.zars.web.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;

public class MessageUtils {
	public static FacesMessage obtainFacesMessage(final ResourceBundleType resourceBundleType, final String summaryMessageName, final Severity messageSeverity, final Object... messageParameters) {
		final String summaryMessage = obtainFacesMessage(resourceBundleType, summaryMessageName, messageParameters);
		return new FacesMessage(messageSeverity, summaryMessage, null);
	}

	public static FacesMessage obtainFacesMessage(final ResourceBundleType resourceBundleType, final String summaryMessageName, final String detailMessageName, final Severity messageSeverity, final Object... messageParameters) {
		final String summaryMessage = obtainFacesMessage(resourceBundleType, summaryMessageName, messageParameters);
		final String detailMessage = obtainFacesMessage(resourceBundleType, detailMessageName, messageParameters);
		return new FacesMessage(messageSeverity, summaryMessage, detailMessage);
	}

	public static String obtainFacesMessage(final ResourceBundleType resourceBundleType, final String messageName, final Object... messageParameters) {
		final String resourceBundleName = resourceBundleType.getResourceBundleName();
		final ResourceBundle resourceBundle = obtainFacesResourceBundle(resourceBundleName);
		try {
			final String messageText = resourceBundle.getString(messageName);
			if (null == messageParameters) {
				return messageText;
			}

			final MessageFormat messageFormat = new MessageFormat(messageText);
			messageFormat.setLocale(resourceBundle.getLocale());
			return messageFormat.format(messageParameters);
		} catch (final MissingResourceException missingResourceException) {
			return "message with name [" + messageName + "] not found in resource bundle [" + resourceBundleName + "]";
		}
	}

	public static ResourceBundle obtainFacesResourceBundle(final String resourceBundleName) {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final Application application = facesContext.getApplication();
		return application.getResourceBundle(facesContext, resourceBundleName);
	}

	public static String obtainMessage(final ResourceBundleType resourceBundleType, final String messageName, final Locale locale, final Object... messageParameters) {
		final String resourceBundleFileName = resourceBundleType.getResourceBundleFileName();
		final ResourceBundle resourceBundle = obtainResourceBundle(resourceBundleFileName, locale);
		try {
			final String messageText = resourceBundle.getString(messageName);
			if (null == messageParameters) {
				return messageText;
			}

			final MessageFormat messageFormat = new MessageFormat(messageText);
			messageFormat.setLocale(locale);
			return messageFormat.format(messageParameters);
		} catch (final MissingResourceException missingResourceException) {
			return "message with name [" + messageName + "] not found in resource bundle [" + resourceBundleFileName + "]";
		}
	}

	public static ResourceBundle obtainResourceBundle(final String resourceBundleFileName, final Locale locale) {
		return ResourceBundle.getBundle(resourceBundleFileName, locale, determineCurrentClassLoader(resourceBundleFileName));
	}

	protected static ClassLoader determineCurrentClassLoader(final Object fallbackClass) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (null != classLoader) {
			return classLoader;
		}

		return fallbackClass.getClass().getClassLoader();
	}
}
