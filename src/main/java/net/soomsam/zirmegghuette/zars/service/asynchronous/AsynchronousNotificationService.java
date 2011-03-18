package net.soomsam.zirmegghuette.zars.service.asynchronous;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.manager.MailManager;
import net.soomsam.zirmegghuette.zars.manager.TemplateManager;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.NotificationService;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.bean.BaseBean;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;

@Service("notificationService")
public class AsynchronousNotificationService implements NotificationService {
	@Autowired
	private PreferenceService preferenceService;

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private MailManager mailManager;

	@Async
	@Override
	public void sendGroupReservationNotification(final OperationType operationType, final GroupReservationBean groupReservationBean) {
		final List<User> notifyUserList = new ArrayList<User>(); // TODO finder, preference, and setting
		sendNotification(operationType, groupReservationBean, notifyUserList, "groupReservationNotification", "NOTIFICATION_GROUPRESERVATION");
	}

	protected void sendNotification(final OperationType operationType, final BaseBean operationData, final List<User> notifyUserList, final String templateName, final String messageName) {
		// TODO check email enabled setting here
		for (final User notifyUser : notifyUserList) {
			final Locale preferredLocale = preferenceService.determinePreferredLocale(notifyUser.getUserId());
			final TimeZone preferredTimeZone = preferenceService.determinePreferredTimeZone(notifyUser.getUserId());
			final TemplateHashModel templateModel = createTemplateModel(operationType, operationData);

			final String subject = "TODO"; // TODO localized enum lookup
			final String message = templateManager.generateDocument(templateName, templateModel, preferredLocale, preferredTimeZone);
			mailManager.sendMail(notifyUser.getEmailAddress(), subject, message);
		}
	}

	protected TemplateHashModel createTemplateModel(final OperationType operationType, final BaseBean operationData) {
		final SimpleHash templateModel = new SimpleHash();
		templateModel.put(createTemplateModelKey(operationType), operationType.getOperationName());
		templateModel.put(createTemplateModelKey(operationType), operationData);
		return templateModel;
	}

	protected String createTemplateModelKey(final Object object) {
		final String simpleName = object.getClass().getSimpleName();
		return StringUtils.uncapitalize(simpleName);
	}
}