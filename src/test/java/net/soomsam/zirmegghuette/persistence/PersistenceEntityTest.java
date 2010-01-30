package net.soomsam.zirmegghuette.persistence;

import java.util.List;

import net.soomsam.zirmegghuette.persistence.dao.RoleDao;
import net.soomsam.zirmegghuette.persistence.entity.Role;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class PersistenceEntityTest {
	@Autowired
	RoleDao roleDao;

	@Test
	public void testCreateRole() {
		final Role adminRole = new Role("ADMIN");
		roleDao.persist(adminRole);
		final Role userRole = new Role("USER");
		roleDao.persist(userRole);
		final List<Role> roleList = roleDao.findAll();
		for (final Role role : roleList) {
			System.out.println(role);
		}
	}
}
