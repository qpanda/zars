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

	@Inject
	private transient UserService userService;

	public String getCommandLinkSelectedUserIdAttributeName() {
		return commandLinkSelectedUserIdAttributeName;
	}

	public List<UserBean> getAllUsers() {
		return userService.findAllUsers();
	}

	public void onDisableUser(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink)commandLinkActionEvent.getComponent();
			final Long selectedUserId = (Long)commandLink.getAttributes().get(commandLinkSelectedUserIdAttributeName);

			userService.disableUser(selectedUserId);
			logger.debug("disabled user with id [" + selectedUserId + "]");
		}
	}

	public void onEnableUser(final ActionEvent commandLinkActionEvent) {
		if ((null != commandLinkActionEvent) && (commandLinkActionEvent.getComponent() instanceof CommandLink)) {
			final CommandLink commandLink = (CommandLink)commandLinkActionEvent.getComponent();
			final Long selectedUserId = (Long)commandLink.getAttributes().get(commandLinkSelectedUserIdAttributeName);

			userService.enableUser(selectedUserId);
			logger.debug("enabled user with id [" + selectedUserId + "]");
		}
	}
}