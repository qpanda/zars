package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
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

	@Override
	@Transactional(readOnly = true)
	public List<RoleBean> findAllRoles() {
		final List<Role> roleList = roleDao.findAll();
		return serviceBeanMapper.map(RoleBean.class, roleList);
	}
}
