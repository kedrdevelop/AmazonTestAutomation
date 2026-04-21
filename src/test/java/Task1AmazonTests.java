import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Task1AmazonTests {
    private WebDriver driver;
    private AmazonHomePage amazonHomePage;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        // It was recommended for stability
        driver.manage().window().maximize();
        amazonHomePage = new AmazonHomePage(driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testAmazonStartPageLoads() {
        try {
            driver.get("https://www.amazon.de/");

            // Wait, until the title is 'Amazon'
            wait.until(ExpectedConditions.titleContains("Amazon"));

            String pageTitle = driver.getTitle();
            System.out.println("DEBUG ACTUAL TITLE: '" + pageTitle + "'");

            Assertions.assertTrue(pageTitle.contains("Amazon"),
                    "The loaded page title does not contain 'Amazon'");

            System.out.println("Die Amazon Startseite wird erfolgreich aufgerufen");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            Assertions.fail("Test FAILED due:" + e.getMessage());
        }
    }


    @Test
    public void testAmazonSneakersSearch() {

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
