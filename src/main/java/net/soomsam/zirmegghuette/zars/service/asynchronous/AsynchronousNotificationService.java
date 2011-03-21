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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;

@Service("notificationService")
public class AsynchronousNotificationService implements NotificationService {
	@Autowired
	private UserService userService;

	@Autowired
	private PreferenceService preferenceService;

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private MailManager mailManager;

	@Async
	@Override
	public void sendGroupReservationNotification(final OperationType operationType, final GroupReservationBean groupReservationBean) {
		// TODO check setting to see if email sending is enabled

		final UserBean booker = groupReservationBean.getBooker();
		final UserBean beneficiary = groupReservationBean.getBeneficiary();
		final UserBean accountant = groupReservationBean.getAccountant();
		final List<UserBean> adminUserList = userService.findUsers(RoleType.ROLE_ADMIN);

		final Set<UserBean> notifyUserBeanSet = new HashSet<UserBean>();
		notifyUserBeanSet.add(booker);
		notifyUserBeanSet.add(beneficiary);
		notifyUserBeanSet.add(accountant);
		notifyUserBeanSet.addAll(adminUserList);

		sendNotification(operationType, groupReservationBean, notifyUserBeanSet, "groupReservationNotification", "NOTIFICATION_GROUPRESERVATION");
	}

	protected void sendNotification(final OperationType operationType, final BaseBean operationData, final Set<UserBean> notifyUserBeanSet, final String templateName, final String messageName) {
		for (final UserBean notifyUserBean : notifyUserBeanSet) {
			// TODO check notification preference
			// TODO check if user is enabled, disabled users should not get notified
			// TODO check if user has email address
			final Locale preferredLocale = preferenceService.determinePreferredLocale(notifyUserBean.getUserId());
			final TimeZone preferredTimeZone = preferenceService.determinePreferredTimeZone(notifyUserBean.getUserId());
			final TemplateHashModel templateModel = createTemplateModel(operationType, operationData);

			final String subject = MessageUtils.obtainMessage(ResourceBundleType.DISPLAY_MESSAGES, messageName, preferredLocale);
			final String message = templateManager.generateDocument(templateName, templateModel, preferredLocale, preferredTimeZone);
			mailManager.sendMail(notifyUserBean.getEmailAddress(), subject, message);
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