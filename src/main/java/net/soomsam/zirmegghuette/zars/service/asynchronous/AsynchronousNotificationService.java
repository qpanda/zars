package net.soomsam.zirmegghuette.zars.service.asynchronous;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import net.soomsam.zirmegghuette.zars.enums.OperationType;
import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.manager.MailManager;
import net.soomsam.zirmegghuette.zars.manager.TemplateManager;
import net.soomsam.zirmegghuette.zars.service.NotificationService;
import net.soomsam.zirmegghuette.zars.service.PreferenceService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.BaseBean;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;

@Service("notificationService")
public class AsynchronousNotificationService implements NotificationService {
	private final static Logger logger = Logger.getLogger(AsynchronousNotificationService.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PreferenceService preferenceService;

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private MailManager mailManager;

	@Value("${notification.javamail.enabled}")
	private boolean notificationEnabled;

	@Async
	@Override
	public void sendGroupReservationNotification(final OperationType operationType, final GroupReservationBean groupReservationBean) {
		final UserBean booker = groupReservationBean.getBooker();
		final UserBean beneficiary = groupReservationBean.getBeneficiary();
		final UserBean accountant = groupReservationBean.getAccountant();
		final List<UserBean> adminUserList = userService.findUsers(RoleType.ROLE_ADMIN);

		final Set<UserBean> notifyUserBeanSet = new HashSet<UserBean>();
		notifyUserBeanSet.add(booker);
		notifyUserBeanSet.add(beneficiary);
		notifyUserBeanSet.add(accountant);
		notifyUserBeanSet.addAll(adminUserList);

		sendNotifications(operationType, groupReservationBean, notifyUserBeanSet, "groupReservationNotification", "NOTIFICATION_GROUPRESERVATION");
	}

	protected void sendNotifications(final OperationType operationType, final BaseBean operationData, final Set<UserBean> notifyUserBeanSet, final String templateName, final String messageName) {
		if (!notificationEnabled) {
			logger.debug("notifications disabled, not sending notification for operation [" + operationType.getOperationName() + "] on [" + operationData + "]");
			return;
		}

		for (final UserBean notifyUserBean : notifyUserBeanSet) {
			sendNotification(operationType, operationData, notifyUserBean, templateName, messageName);
		}
	}

	protected void sendNotification(final OperationType operationType, final BaseBean operationData, final UserBean notifyUserBean, final String templateName, final String messageName) {
		if (!notifyUserBean.isEnabled()) {
			logger.debug("user [" + notifyUserBean + "] is disabled, not sending notification for operation [" + operationType.getOperationName() + "] on [" + operationData + "]");
			return;
		}

		if (!notifyUserBean.hasEmailAddress()) {
			logger.debug("user [" + notifyUserBean + "] has no email address, not sending notification for operation [" + operationType.getOperationName() + "] on [" + operationData + "]");
			return;
		}

		// TODO check notification preference
		final Locale preferredLocale = preferenceService.determinePreferredLocale(notifyUserBean.getUserId());
		final TimeZone preferredTimeZone = preferenceService.determinePreferredTimeZone(notifyUserBean.getUserId());
		final TemplateHashModel templateModel = createTemplateModel(operationType, operationData);

		final String subject = MessageUtils.obtainMessage(ResourceBundleType.DISPLAY_MESSAGES, messageName, preferredLocale);
		final String message = templateManager.generateDocument(templateName, templateModel, preferredLocale, preferredTimeZone);
		mailManager.sendMail(notifyUserBean.getEmailAddress(), subject, message);
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