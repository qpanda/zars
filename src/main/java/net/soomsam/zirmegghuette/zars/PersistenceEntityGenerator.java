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

	public static Role createUserRole() {
		return new Role(ROLE_USER);
	}

	public static Role createAdminRole() {
		return new Role(ROLE_ADMIN);
	}

	public static User createUserTest(final Role... roles) {
		return new User("test", String.valueOf(new Random().nextLong()), true, new HashSet<Role>(Arrays.asList(roles)));
	}

	public static Room createFirstRoom() {
		return new Room("first", 4, 1);
	}

	public static Room createSecondRoom() {
		return new Room("second", 4, 2);
	}
}
