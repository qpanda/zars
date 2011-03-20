package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.enums.ResourceBundleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.web.utils.MessageUtils;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
@SuppressWarnings("serial")
public class ChangeUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(ChangeUserController.class);

	@Inject
	private transient SecurityController securityController;

	@Inject
	private transient UserService userService;

	@Email(message = "{sectionsApplicationUserEmailAddressInvalidError}")
	@Length(max = User.COLUMNLENGTH_EMAILADDRESS, message = "{sectionsApplicationUserEmailAddressLengthError}")
	private String emailAddress;

	@Length(max = User.COLUMNLENGTH_FIRSTNAME, message = "{sectionsApplicationUserFirstNameLengthError}")
	private String firstName;

	@Length(max = User.COLUMNLENGTH_LASTNAME, message = "{sectionsApplicationUserLastNameLengthError}")
	private String lastName;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public void retrieveUser() {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		final UserBean userBean = userService.retrieveCurrentUser();
		this.emailAddress = userBean.getEmailAddress();
		this.firstName = userBean.getFirstName();
		this.lastName = userBean.getLastName();
	}

	public String update() {
		logger.debug("changing email address, first name, and last name of current user [" + securityController.getCurrentUserId() + "]");
		try {
			userService.changeUser(emailAddress, firstName, lastName);
			return "changeUserConfirmation";
		} catch (final UniqueConstraintException uniqueConstraintException) {
			final String uniqueConstraintMessageId = "sectionsApplicationUserUnique" + uniqueConstraintException.getUniqueConstraintField().toUpperCase() + "Error";
			final FacesMessage uniqueConstraintFacesMessage = MessageUtils.obtainFacesMessage(ResourceBundleType.VALIDATION_MESSAGES, uniqueConstraintMessageId, FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, uniqueConstraintFacesMessage);
		}

		return null;
	}
}