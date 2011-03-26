package net.soomsam.zirmegghuette.zars.service.asynchronous;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import net.soomsam.zirmegghuette.zars.enums.NotificationModeType;
import net.soomsam.zirmegghuette.zars.enums.NotificationType;
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

	@Value("${notification.javamail.notificationMode}")
	private NotificationModeType notificationMode;

	@Value("${notification.javamail.notificationDomain}")
	private String notificationDomain;

	@Async
	@Override
	public void sendGroupReservationNotification(final NotificationType notificationType, final GroupReservationBean groupReservationBean) {
		try {
			final UserBean booker = groupReservationBean.getBooker();
			final UserBean beneficiary = groupReservationBean.getBeneficiary();
			final UserBean accountant = groupReservationBean.getAccountant();
			final List<UserBean> adminUserList = userService.findUsers(RoleType.ROLE_ADMIN);

			final Set<UserBean> notifyUserBeanSet = new HashSet<UserBean>();
			notifyUserBeanSet.add(booker);
			notifyUserBeanSet.add(beneficiary);
			notifyUserBeanSet.add(accountant);
			notifyUserBeanSet.addAll(adminUserList);

			sendNotifications(notificationType, groupReservationBean, notifyUserBeanSet, "groupReservationNotification.ftl", "NOTIFICATION_GROUPRESERVATION");
		} catch (final Exception exception) {
			logger.error("error while preparing, generating, or sending notification for operation [" + notificationType.getNotificationName() + "] on [" + groupReservationBean.getClass().getSimpleName() + "]: " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	@Async
	@Override
	public void sendUserNotification(final NotificationType notificationType, final UserBean userBean) {
		try {
			final Set<UserBean> notifyUserBeanSet = new HashSet<UserBean>();
			notifyUserBeanSet.add(userBean);

			sendNotifications(notificationType, userBean, notifyUserBeanSet, "userNotification.ftl", "NOTIFICATION_USER");
		} catch (final Exception exception) {
			logger.error("error while preparing, generating, or sending notification for operation [" + notificationType.getNotificationName() + "] on [" + userBean.getClass().getSimpleName() + "]: " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	protected void sendNotifications(final NotificationType notificationType, final BaseBean operationData, final Set<UserBean> notifyUserBeanSet, final String templateName, final String messageName) {
		if (NotificationModeType.DISABLED.equals(notificationMode)) {
			logger.debug("notifications disabled, not sending notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "]");
			return;
		}

		for (final UserBean notifyUserBean : notifyUserBeanSet) {
			sendNotification(notificationType, operationData, notifyUserBean, templateName, messageName);
		}
	}

	protected void sendNotification(final NotificationType notificationType, final BaseBean operationData, final UserBean notifyUserBean, final String templateName, final String messageName) {
		if (!notifyUserBean.isEnabled() && !NotificationType.NOTIFICATION_USER_DISABLE.equals(notificationType)) {
			logger.debug("user [" + notifyUserBean.getUsername() + "] is disabled, not sending notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "]");
			return;
		}

		if (!notifyUserBean.hasEmailAddress()) {
			logger.debug("user [" + notifyUserBean.getUsername() + "] has no email address, not sending notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "]");
			return;
		}

		final boolean emailNotification = preferenceService.determineNotification(notifyUserBean.getUserId());
		if (!emailNotification) {
			logger.debug("user [" + notifyUserBean.getUsername() + "] has no email notification disabled, not sending notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "]");
			return;
		}

		final TemplateHashModel templateModel = createTemplateModel(notificationType, operationData);

		final Locale preferredLocale = preferenceService.determinePreferredLocale(notifyUserBean.getUserId());
		final TimeZone preferredTimeZone = preferenceService.determinePreferredTimeZone(notifyUserBean.getUserId());
		final String subject = MessageUtils.obtainMessage(ResourceBundleType.ENUM_MESSAGES, messageName, preferredLocale);

		final String message = templateManager.generateDocument(templateName, templateModel, preferredLocale, preferredTimeZone);
		if (NotificationModeType.ENABLED.equals(notificationMode)) {
			mailManager.sendMail(notifyUserBean.getEmailAddress(), subject, message);
			logger.debug("sent notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "] to user [" + notifyUserBean.getUsername() + "] with subject [" + subject + "], locale [" + preferredLocale + "], and timezone [" + preferredTimeZone + "]");
		} else if (NotificationModeType.SIMULATE.equals(notificationMode)) {
			logger.debug("would send notification for operation [" + notificationType.getNotificationName() + "] on [" + operationData.getClass().getSimpleName() + "] to user [" + notifyUserBean.getUsername() + "] with subject [" + subject + "], locale [" + preferredLocale + "], and timezone [" + preferredTimeZone + "]\n" + message);
		}
	}

	protected TemplateHashModel createTemplateModel(final NotificationType notificationType, final BaseBean operationData) {
		final SimpleHash templateModel = new SimpleHash();
		templateModel.put("notificationDomain", notificationDomain);
		templateModel.put(createTemplateModelKey(notificationType), notificationType.getNotificationName());
		templateModel.put(createTemplateModelKey(operationData), operationData);
		return templateModel;
	}

	protected String createTemplateModelKey(final Object object) {
		final String simpleName = object.getClass().getSimpleName();
		return StringUtils.uncapitalize(simpleName);
	}
}