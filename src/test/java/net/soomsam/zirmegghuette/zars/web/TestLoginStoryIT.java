package net.soomsam.zirmegghuette.zars.web;

import net.soomsam.zirmegghuette.zars.web.steps.EndUserSteps;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
@Story(Zars.Login.TestLogin.class) 
public class TestLoginStoryIT {
    @Managed(uniqueSession=true)
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://localhost:8080/views/login.jsf")
    public Pages pages;

    @Steps
    public EndUserSteps endUser;

    @Test
    public void should_see_login_page() {
        endUser.testLogin();                                           
    }
    
    @Test
    public void should_see_login_page_again() {
        endUser.testValidLogin();                                           
    }
}
