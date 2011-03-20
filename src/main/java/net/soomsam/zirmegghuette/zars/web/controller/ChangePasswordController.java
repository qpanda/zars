package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ChangePasswordController implements Serializable {
	private final static Logger logger = Logger.getLogger(ChangePasswordController.class);

	@Inject
	private transient SecurityController securityController;

	@Inject
	private transient UserService userService;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationUserPasswordEmptyError}")
	@Length(max = User.COLUMNLENGTH_PASSWORD, message = "{sectionsApplicationUserPasswordLengthError}")
	private String confirmPassword;

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

	public String update() {
		if (!StringUtils.equals(password, confirmPassword)) {
			final FacesMessage uniqueConstraintFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, "sectionsApplicationUserPasswordInvalidError", FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
			return null;
		}

		logger.debug("changing password of current user [" + securityController.getCurrentUserId() + "]");
		userService.changePassword(password);
		return "changePasswordConfirmation";
	}
}