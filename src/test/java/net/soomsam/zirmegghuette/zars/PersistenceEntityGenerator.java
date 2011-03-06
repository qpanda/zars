package net.soomsam.zirmegghuette.zars;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

public class PersistenceEntityGenerator {
	public static final String ROLE_USER = "user";
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_ACCOUNTANT = "accountant";

	public static Role createUserRole() {
		return new Role(ROLE_USER);
	}

	public static Role createAdminRole() {
		return new Role(ROLE_ADMIN);
	}

	public static Role createAccountantRole() {
		return new Role(ROLE_ACCOUNTANT);
	}

	public static User createUserTest(final String userName, final String emailAddress, final Role... roles) {
		return new User(userName, String.valueOf(new Random().nextLong()), emailAddress, true, new HashSet<Role>(Arrays.asList(roles)));
	}

	public static Room createTestRoom() {
		return new Room("test", 4, 3, true);
	}

	public static Room createAnotherTestRoom() {
		return new Room("anotherTest", 4, 4, true);
	}
}
