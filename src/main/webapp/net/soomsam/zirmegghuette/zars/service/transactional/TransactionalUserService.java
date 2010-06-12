package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.RoleEnum;
import net.soomsam.zirmegghuette.zars.exception.UniqueConstraintException;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.UserDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.service.utils.ServiceBeanMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional(timeout = 1000)
public class TransactionalUserService implements UserService {
	@Autowired
	private ServiceBeanMapper serviceBeanMapper;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public List<RoleBean> findAllRoles() {
		return serviceBeanMapper.map(RoleBean.class, roleDao.findAll());
	}

	@Override
	public void createAllRoles() {
		for (final RoleEnum roleEnum : RoleEnum.values()) {
			final Role role = new Role(roleEnum.getRoleName());
			roleDao.persist(role);
		}
	}

	@Override
	@Transactional(rollbackFor = UniqueConstraintException.class)
	public UserBean createUser(final String username, final String password, final String emailAddress, final String firstName, final String lastName, final Set<Long> roleIdSet) throws UniqueConstraintException {
		final List<Role> roleList = roleDao.findByPrimaryKeys(roleIdSet);
		final User user = new User(username, password, emailAddress, true, new HashSet<Role>(roleList));
		userDao.createUser(user);
		return serviceBeanMapper.map(UserBean.class, user);
	}

	@Override
	public List<UserBean> finaAllUsers() {
		return serviceBeanMapper.map(UserBean.class, userDao.findAll());
	}
}
