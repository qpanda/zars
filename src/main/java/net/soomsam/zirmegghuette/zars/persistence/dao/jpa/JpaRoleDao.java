package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class JpaRoleDao extends JpaEntityDao<Role> implements RoleDao {
	@Override
	protected Class<Role> determineEntityClass() {
		return Role.class;
	}

	@Override
	public List<Role> findAll() {
		return findAll(null);
	}

	@Override
	public List<Role> findAll(final Pagination pagination) {
		final TypedQuery<Role> findRoleTypedQuery = createNamedTypedQuery(Role.FINDROLE_QUERYNAME);

		if (null != pagination) {
			findRoleTypedQuery.setFirstResult(pagination.getFirstResult());
			findRoleTypedQuery.setMaxResults(pagination.getMaxResults());
		}

		return findRoleTypedQuery.getResultList();
	}

	@Override
	public List<Role> findByPrimaryKeys(final Set<Long> roleIdSet) {
		if ((null == roleIdSet) || roleIdSet.isEmpty()) {
			throw new IllegalArgumentException("'roleIdSet' must not be null or empty");
		}

		final TypedQuery<Role> findRoleByPrimaryKeyTypedQuery = createNamedTypedQuery(Role.FINDROLE_ID_QUERYNAME);
		findRoleByPrimaryKeyTypedQuery.setParameter("roleIdSet", roleIdSet);
		return findRoleByPrimaryKeyTypedQuery.getResultList();
	}

	@Override
	public Role retrieveByName(final String name) {
		if (null == name) {
			throw new IllegalArgumentException("'name' must not be null");
		}

		final TypedQuery<Role> findRoleByNameTypedQuery = createNamedTypedQuery(Role.FINDROLE_NAME_QUERYNAME);
		findRoleByNameTypedQuery.setParameter("name", name);

		try {
			return findRoleByNameTypedQuery.getSingleResult();
		} catch (final NoResultException noResultException) {
			throw new EntityNotFoundException("role with name [" + name + "] not found", noResultException);
		}
	}
}
