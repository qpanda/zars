package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;

import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class JpaRoleDao extends JpaEntityDao<Role> implements RoleDao {
	@Override
	protected Class<Role> determineEntityClass() {
		return Role.class;
	}

	@Override
	public List<Role> findByPrimaryKeys(final Set<Long> roleIdSet) {
		if ((null == roleIdSet) || roleIdSet.isEmpty()) {
			throw new IllegalArgumentException("'roleIdSet' must not be null or empty");
		}

		final Query findRoleByPrimaryKeyQuery = createNamedQuery(Role.FINDROLE_ID_QUERYNAME);
		findRoleByPrimaryKeyQuery.setParameter("roleIdSet", roleIdSet);
		return findRoleByPrimaryKeyQuery.getResultList();
	}

	@Override
	public Role retrieveByName(final String name) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		final Query findRoleByNameQuery = createNamedQuery(Role.FINDROLE_NAME_QUERYNAME);
		findRoleByNameQuery.setParameter("name", name);
		final Role role = (Role) findRoleByNameQuery.getSingleResult();
		if (role == null) {
			throw new EntityNotFoundException("role with name [" + name + "] not found");
		}

		return role;
	}
}
