package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.PreferenceDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Preference;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.utils.ServiceBeanMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional(timeout = 10000)
public class TransactionalUserService implements UserService {
	@Autowired
	private ServiceBeanMapper serviceBeanMapper;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PreferenceDao preferenceDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<RoleBean> findAllRoles() {
		return serviceBeanMapper.map(RoleBean.class, roleDao.findAll());
	}

	@Override
	public void createAllRoles() {
		for (final RoleType roleEnum : RoleType.values()) {
			final Role role = new Role(roleEnum.getRoleName());
			roleDao.persist(role);
		}
	}

	@Override
	public void createDefaultUsers() {
		final Role adminRole = roleDao.retrieveByName(RoleType.ROLE_ADMIN.getRoleName());
		final User adminUser = new User("admin", encodePassword("admin"), "admin@zars.soomsam.net", true, adminRole);
		userDao.persist(adminUser);

		final Preference adminTimezonePreference = preferenceDao.create(adminUser.getUserId(), PreferenceType.TIMEZONE, PreferenceType.TIMEZONE.getPreferenceDefaultValue());
		preferenceDao.persist(adminTimezonePreference);

		final Preference adminLocalePreference = preferenceDao.create(adminUser.getUserId(), PreferenceType.LOCALE, PreferenceType.LOCALE.getPreferenceDefaultValue());
		preferenceDao.persist(adminLocalePreference);
	}

	@Override
	@Transactional(rollbackFor = UniqueConstraintException.class)
	public UserBean createUser(final String username, final String password, final String emailAddress, final String firstName, final String lastName, final Set<Long> roleIdSet) throws UniqueConstraintException {
		final List<Role> roleList = roleDao.findByPrimaryKeys(roleIdSet);
		final User user = new User(username, encodePassword(password), emailAddress, firstName, lastName, true, new HashSet<Role>(roleList));
		userDao.persistUser(user);
		return serviceBeanMapper.map(UserBean.class, user);
	}

	@Override
	@Transactional(rollbackFor = UniqueConstraintException.class)
	public UserBean updateUser(final long userId, final String username, final String emailAddress, final String firstName, final String lastName, final Set<Long> roleIdSet) throws UniqueConstraintException {
		final List<Role> roleList = roleDao.findByPrimaryKeys(roleIdSet);
		final User user = userDao.retrieveByPrimaryKey(userId);
		user.setUsername(username);
		user.setEmailAddress(emailAddress);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.updateRoles(new HashSet<Role>(roleList));
		userDao.persistUser(user);
		return serviceBeanMapper.map(UserBean.class, user);
	}

	@Override
	public UserBean resetUser(final long userId, final String password, final boolean enabled) {
		final User user = userDao.retrieveByPrimaryKey(userId);
		user.setPassword(encodePassword(password));
		user.setEnabled(enabled);
		userDao.persist(user);
		return serviceBeanMapper.map(UserBean.class, user);
	}

	@Override
	public UserBean changePassword(final String password) {
		final User user = userDao.retrieveCurrentUser();
		user.setPassword(encodePassword(password));
		userDao.persist(user);
		return serviceBeanMapper.map(UserBean.class, user);
	}

	@Override
	public UserBean retrieveUser(final long userId) {
		return serviceBeanMapper.map(UserBean.class, userDao.retrieveByPrimaryKey(userId));
	}

	@Override
	public List<UserBean> findAllUsers() {
		return serviceBeanMapper.map(UserBean.class, userDao.findAll());
	}

	@Override
	public void enableUser(final long userId) {
		final User user = userDao.retrieveByPrimaryKey(userId);
		user.setEnabled(true);
	}

	@Override
	public void disableUser(final long userId) {
		final User user = userDao.retrieveByPrimaryKey(userId);
		user.setEnabled(false);
	}

	@Override
	public List<UserBean> findUsers(final RoleType roleType) {
		if (null == roleType) {
			throw new IllegalArgumentException("'roleType' must not be null");
		}

		final Role role = roleDao.retrieveByName(roleType.getRoleName());
		return serviceBeanMapper.map(UserBean.class, userDao.findByRoleId(role.getRoleId()));
	}

	@Override
	public UserBean retrieveUser(final String username) {
		return serviceBeanMapper.map(UserBean.class, userDao.retrieveByUsername(username));
	}

	@Override
	public UserBean retrieveCurrentUser() {
		return serviceBeanMapper.map(UserBean.class, userDao.retrieveCurrentUser());
	}

	protected String encodePassword(final String rawPassword) {
		return passwordEncoder.encodePassword(rawPassword, null);
	}
}
