package net.soomsam.zirmegghuette.zars.web.pages;

import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends PageObject {
	@FindBy(id = "j_username")
	private WebElement usernameField;
	
	@FindBy(id = "j_password")
	private WebElement passwordField;
	
	@FindBy(id = "login")
	private WebElement loginButton;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void testLogin() {
		loginButton.click();
	}
	
	public void testValidLogin() {
		usernameField.sendKeys("admin");
		passwordField.sendKeys("admin");
		loginButton.click();
	}
}
