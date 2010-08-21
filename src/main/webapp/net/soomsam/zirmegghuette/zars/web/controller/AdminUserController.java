package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;
import org.primefaces.component.commandlink.CommandLink;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class AdminUserController implements Serializable {
	private final static Logger logger = Logger.getLogger(AdminUserController.class);

	private final String commandLinkSelectedUserIdAttributeName = "selectedUserId";

	private Long selectedUserId;

	@Inject
	private transient UserService userService;

	public String getCommandLinkSelectedUserIdAttributeName() {
		return commandLinkSelectedUserIdAttributeName;
	}

	public Long getSelectedUserId() {
		return selectedUserId;
	}

	public void setSelectedUserId(final Long selectedUserId) {
		this.selectedUserId = selectedUserId;
	}

	public List<UserBean> getAllUsers() {
		return userService.findAllUsers();
	}

	public void setSelectedUserId(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink) commandLinkActionEvent.getComponent();
			final Long commandLinkParameterValue = (Long) commandLink.getAttributes().get(commandLinkSelectedUserIdAttributeName);
			setSelectedUserId(commandLinkParameterValue);
		}
	}

	public String disableUser() {
		userService.disableUser(selectedUserId);
		logger.debug("disabled user with id [" + selectedUserId + "]");
		return "adminUser?faces-redirect=true";
	}

	public String enableUser() {
		userService.enableUser(selectedUserId);
		logger.debug("enabled user with id [" + selectedUserId + "]");
		return "adminUser?faces-redirect=true";
	}
}