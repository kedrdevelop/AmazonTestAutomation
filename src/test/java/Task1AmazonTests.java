import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Task1AmazonTests {
    // ANSI color codes for console output
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

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
            test11();
            test12();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            Assertions.fail("Test FAILED due:" + e.getMessage());
        }
    }

    private void test11() {
        driver.get("https://www.amazon.de/");

        // Wait, until the title is 'Amazon'
        wait.until(ExpectedConditions.titleContains("Amazon"));

        String pageTitle = driver.getTitle();

        Assertions.assertTrue(pageTitle.contains("Amazon"),
                "The loaded page title does not contain 'Amazon'");
        printSuccess("Die Amazon Startseite wird erfolgreich aufgerufen");
    }

    private void test12() {
        amazonHomePage.searchForProduct("Adidas Schuhe");
        By result = By.cssSelector("div[data-component-type='s-search-result']");
        wait.until(ExpectedConditions.presenceOfElementLocated(result));

        List<WebElement> searchResults = driver.findElements(result);

        Assertions.assertTrue(searchResults.size() >= 5,
                "Expected 5, but found: " + searchResults.size());

        printSuccess("Es werden mindestens 5 Schuhe angezeigt (Gefunden: "
                + searchResults.size() + ")");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void printSuccess(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }
}
