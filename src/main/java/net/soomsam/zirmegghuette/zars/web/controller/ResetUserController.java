package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.NotificationType;
import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.NotificationService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ResetUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(ResetUserController.class);

	@Inject
	private transient UserService userService;

	@Inject
	private transient SettingController settingController;

	@Inject
	private transient NotificationService notificationService;

	private boolean validNavigation = true;

	private Long userId;

	private String username;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String confirmPassword;

	private UserBean savedUser;

	public boolean isValidNavigation() {
		return validNavigation;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public UserBean getSavedUser() {
		return savedUser;
	}

	public String getInvalidUserIdMessage() {
		final Locale preferredLocale = settingController.getPreferredLocale();
		return MessageUtils.obtainMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserUserIdError", preferredLocale);
	}

	public void retrieveUser() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (FacesContext.getCurrentInstance().isValidationFailed()) {
			this.validNavigation = false;
			return;
		}

		if (null == this.userId) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
			return;
		}

		try {
			final UserBean userBean = userService.retrieveUser(this.userId);
			this.userId = userBean.getUserId();
			this.username = userBean.getUsername();
		} catch (final EntityNotFoundException entityNotFoundException) {
			this.validNavigation = false;
			final FacesMessage invalidUserIdFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserUserIdError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, invalidUserIdFacesMessage);
		}
	}

	public String reset() {
		if (!StringUtils.equals(password, confirmPassword)) {
			final FacesMessage invalidPasswordFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserPasswordInvalidError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, invalidPasswordFacesMessage);
			return null;
		}

		logger.debug("resetting user with id [" + userId + "]");
		savedUser = userService.resetUser(userId, password, true);
		notificationService.sendUserNotification(NotificationType.NOTIFICATION_USER_RESET, savedUser);
		return "resetUserConfirmation";
	}
}