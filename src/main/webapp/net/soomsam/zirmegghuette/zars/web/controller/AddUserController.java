package net.soomsam.zirmegghuette.zars.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.primefaces.model.DualListModel;

@Named
@RequestScoped
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
			final List<RoleBean> availableRoles = userService.findAllRoles();
			final List<RoleBean> assignedRoles = new ArrayList<RoleBean>();
			roleDualListModel = new DualListModel<RoleBean>(availableRoles, assignedRoles);
		}

		return roleDualListModel;
	}

	public void setRoleDualListModel(final DualListModel<RoleBean> roleDualListModel) {
		this.roleDualListModel = roleDualListModel;
	}

	public String create() {
		final Set<Long> roleIdSet = new HashSet<Long>();
		for (final RoleBean roleBean : roleDualListModel.getTarget()) {
			roleIdSet.add(roleBean.getRoleId());
		}

		final UserBean userBean = userService.createUser(username, password, emailAddress, firstName, lastName, roleIdSet);
		return "groupReservation";
	}
}