package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.primefaces.model.DualListModel;

@Named
@SessionScoped
public class AddUserController implements Serializable {
	@Inject
	private transient UserService userService;

	@NotEmpty(message = "{sectionsApplicationAddUserUserNameError}")
	private String username;

	@NotEmpty(message = "{sectionsApplicationAddUserPasswordError}")
	private String password;

	@NotEmpty(message = "{sectionsApplicationAddUserEmailAddressError}")
	@Email(message = "{sectionsApplicationAddUserEmailAddressError}")
	private String emailAddress;

	private String firstName;

	private String lastName;

	private DualListModel<RoleBean> roleDualListModel;

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

	public DualListModel<RoleBean> getRoleDualListModel() {
		if (null == roleDualListModel) {
			List<RoleBean> availableRoles = userService.findAllRoles();
			// TODO
			availableRoles.add(new RoleBean(1, new Date(), "ADMIN"));
			availableRoles.add(new RoleBean(2, new Date(), "USER"));
			List<RoleBean> assignedRoles = new ArrayList<RoleBean>();
			roleDualListModel = new DualListModel<RoleBean>(availableRoles, assignedRoles);
		}

		return roleDualListModel;
	}

	public void setRoleDualListModel(DualListModel<RoleBean> roleDualListModel) {
		this.roleDualListModel = roleDualListModel;
	}

	public String create() {
		System.out.println(roleDualListModel.getTarget());
		return null;
	}
}