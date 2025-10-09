package org.example.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GooglePageObjectTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.google.com/");
    }

    @Test
    void testSearchWithPageObject() {
        GooglePage googlePage = new GooglePage(driver);
        googlePage.search("Selenium WebDriver");

        String result = googlePage.waitForFirstResult();
        assertTrue(result.toLowerCase().contains("selenium"));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}