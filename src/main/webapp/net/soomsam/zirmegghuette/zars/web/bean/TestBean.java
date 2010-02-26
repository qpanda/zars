package net.soomsam.zirmegghuette.zars.web.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class TestBean implements Serializable {
	public String signup() {
		if (Math.random() < 0.2) {
			return ("accepted");
		} else {
			return ("rejected");
		}
	}
}
