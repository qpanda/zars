package net.soomsam.zirmegghuette.zars.web.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import net.soomsam.zirmegghuette.zars.web.pages.LoginPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class EndUserSteps extends ScenarioSteps {
	public EndUserSteps(Pages pages) {
		super(pages);
	}
	
	@Step
	public void testLogin() {
		LoginPage loginPage = getPages().currentPageAt(LoginPage.class);
		loginPage.testLogin();
	}
	
	@Step
	public void testValidLogin() {
		LoginPage loginPage = getPages().currentPageAt(LoginPage.class);
		loginPage.testValidLogin();
	}
}
