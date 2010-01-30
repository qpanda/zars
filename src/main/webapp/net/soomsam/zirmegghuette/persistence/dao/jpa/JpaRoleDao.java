package net.soomsam.zirmegghuette.persistence.dao.jpa;

import net.soomsam.zirmegghuette.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.persistence.entity.Role;

import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class JpaRoleDao extends JpaBaseDao<Role> implements RoleDao {
	@Override
	protected Class<Role> determineEntityClass() {
		return Role.class;
	}
}
