package org.example.test;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class GooglePage {
    private WebDriver driver;
    private By searchBox = By.name("q");
    private By firstResult = By.cssSelector("h3");

    public GooglePage(WebDriver driver) {
        this.driver = driver;
    }

    public void search(String query) {
        driver.findElement(searchBox).sendKeys(query + Keys.ENTER);
    }

    public String waitForFirstResult() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement result = wait.until(ExpectedConditions.elementToBeClickable(firstResult));
        return result.getText();
    }
}