package net.soomsam.zirmegghuette.zars;

import java.util.Arrays;
import java.util.HashSet;

import net.soomsam.zirmegghuette.zars.persistence.entity.Role;
import net.soomsam.zirmegghuette.zars.persistence.entity.Room;
import net.soomsam.zirmegghuette.zars.persistence.entity.User;

public class PersistenceEntityGenerator {
	public static final String ROLE_USER = "user";
	public static final String ROLE_ADMIN = "admin";
	
	public static Role createRoleUser() {
		return new Role(ROLE_USER);
	}
	
	public static Role createRoleAdmin() {
		return new Role(ROLE_ADMIN);
	}
	
	public static User createUserTest(Role... roles) {
		return new User("test", "test", true, new HashSet<Role>(Arrays.asList(roles)));
	}
	
	public static Room createMajorRoom() {
		return new Room("major", 8, 1);
	}
}
