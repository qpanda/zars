package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.soomsam.zirmegghuette.zars.service.UserService;

@Named
@SessionScoped
public class TestBean implements Serializable {
	@Inject
	private transient UserService userService;

	private final String test = "abc";

	public String getTest() {
		return test;
	}

	public String signup() {
		if (Math.random() < 0.2) {
			System.out.println("$$$" + userService);
			return ("accepted");
		} else {
			System.out.println("$$$" + userService);
			return ("rejected");
		}
	}
}
