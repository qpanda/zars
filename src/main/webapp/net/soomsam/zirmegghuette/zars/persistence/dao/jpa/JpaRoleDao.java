package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;

import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class JpaRoleDao extends JpaEntityDao<Role> implements RoleDao {
	@Override
	protected Class<Role> determineEntityClass() {
		return Role.class;
	}

	public List<Role> findByPrimaryKeys(final Set<Long> roleIdSet) {
		if (null == roleIdSet) {
			throw new IllegalArgumentException("'roleIdSet' must not be null");
		}

		final Query findRoleByPrimaryKeyQuery = createNamedQuery(Role.FINDROLE_ID_QUERYNAME);
		findRoleByPrimaryKeyQuery.setParameter("roleIdSet", roleIdSet);
		return findRoleByPrimaryKeyQuery.getResultList();
	}
}
