package net.soomsam.zirmegghuette.zars.persistence.dao;

import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.persistence.entity.Role;

public interface RoleDao extends EntityDao<Role> {
	public List<Role> findByPrimaryKeys(final Set<Long> roleIdSet);

	public Role retrieveByName(final String name);
}
