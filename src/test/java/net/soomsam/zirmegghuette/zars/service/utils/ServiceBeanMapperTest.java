package net.soomsam.zirmegghuette.zars.service.utils;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.PersistenceEntityGenerator;
import net.soomsam.zirmegghuette.zars.persistence.entity.GroupReservation;
import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;
import net.soomsam.zirmegghuette.zars.service.bean.GroupReservationBean;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/core-context.xml" })
public class ServiceBeanMapperTest {
	@Autowired
	private ServiceBeanMapper serviceBeanMapper;

	@Test
	public void mapRole() {
		Role adminRole = PersistenceEntityGenerator.createAdminRole();

		RoleBean adminRoleBean = serviceBeanMapper.map(RoleBean.class, adminRole);
		Assert.assertEquals(adminRole.getName(), adminRoleBean.getName());
		Assert.assertEquals(adminRole.getRoleId(), adminRoleBean.getRoleId());
		Assert.assertEquals(adminRole.getRoleTimestamp(), adminRoleBean.getRoleTimestamp());
	}

	@Test
	public void mapGroupReservation() {
		Role userRole = PersistenceEntityGenerator.createUserRole();
		Role adminRole = PersistenceEntityGenerator.createAdminRole();
		User beneficiary = PersistenceEntityGenerator.createUserTest("beneficiary", "beneficiary@test.com", userRole, adminRole);
		User accountant = PersistenceEntityGenerator.createUserTest("accountant", "accountant@test.com", userRole, adminRole);
		Room testRoom = PersistenceEntityGenerator.createTestRoom();
		Room anotherTestRoom = PersistenceEntityGenerator.createAnotherTestRoom();
		DateTime booked = new DateTime();
		DateMidnight arrival = new DateMidnight();
		DateMidnight departure = arrival.plusDays(1);
		long guests = 3;
		GroupReservation groupReservation = new GroupReservation(beneficiary, accountant, booked, arrival, departure, guests);
		groupReservation.associateRoom(testRoom);
		groupReservation.associateRoom(anotherTestRoom);

		GroupReservationBean groupReservationBean = serviceBeanMapper.map(GroupReservationBean.class, groupReservation);
		Assert.assertEquals(groupReservationBean.getGroupReservationId(), groupReservation.getGroupReservationId());
		Assert.assertEquals(groupReservationBean.getGroupReservationTimestamp(), groupReservation.getGroupReservationTimestamp());

		Assert.assertEquals(groupReservationBean.getBooked(), groupReservation.getBooked().toDate());
		Assert.assertEquals(groupReservationBean.getArrival(), groupReservation.getArrival().toDate());
		Assert.assertEquals(groupReservationBean.getDeparture(), groupReservation.getDeparture().toDate());

		Assert.assertEquals(groupReservationBean.getGuests(), groupReservation.getGuests());
		Assert.assertEquals(groupReservationBean.getComment(), groupReservation.getComment());

		Assert.assertEquals(groupReservationBean.getBeneficiary().getUserId(), groupReservation.getBeneficiary().getUserId());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getUserTimestamp(), groupReservation.getBeneficiary().getUserTimestamp());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getUsername(), groupReservation.getBeneficiary().getUsername());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getPassword(), groupReservation.getBeneficiary().getPassword());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getEmailAddress(), groupReservation.getBeneficiary().getEmailAddress());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getFirstName(), groupReservation.getBeneficiary().getFirstName());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getLastName(), groupReservation.getBeneficiary().getLastName());
		Assert.assertEquals(groupReservationBean.getBeneficiary().getRoles().size(), groupReservation.getBeneficiary().getRoles().size());

		Assert.assertEquals(groupReservationBean.getAccountant().getUserId(), groupReservation.getAccountant().getUserId());
		Assert.assertEquals(groupReservationBean.getAccountant().getUserTimestamp(), groupReservation.getAccountant().getUserTimestamp());
		Assert.assertEquals(groupReservationBean.getAccountant().getUsername(), groupReservation.getAccountant().getUsername());
		Assert.assertEquals(groupReservationBean.getAccountant().getPassword(), groupReservation.getAccountant().getPassword());
		Assert.assertEquals(groupReservationBean.getAccountant().getEmailAddress(), groupReservation.getAccountant().getEmailAddress());
		Assert.assertEquals(groupReservationBean.getAccountant().getFirstName(), groupReservation.getAccountant().getFirstName());
		Assert.assertEquals(groupReservationBean.getAccountant().getLastName(), groupReservation.getAccountant().getLastName());
		Assert.assertEquals(groupReservationBean.getAccountant().getRoles().size(), groupReservation.getAccountant().getRoles().size());

		Assert.assertEquals(groupReservationBean.getRooms().size(), groupReservation.getRooms().size());
	}
}
