import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AmazonTests {
    // ANSI color codes for console output
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private WebDriver driver;
    private AmazonHomePage amazonHomePage;
    private WebDriverWait wait;
    private List<WebElement> searchResults = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        // It was recommended for stability
        driver.manage().window().maximize();
        amazonHomePage = new AmazonHomePage(driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testCase1() {
        try {
            test11();
            test12();
            test13();
            test14();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            Assertions.fail("Test FAILED due:" + e.getMessage());
        }
    }

    private void test11() {
        driver.get("https://www.amazon.de/");

        // Wait, until the title is 'Amazon'
        wait.until(ExpectedConditions.titleContains("Amazon"));

        // Handle the cookie consent banner if it appears
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement acceptCookiesBtn = shortWait
                    .until(ExpectedConditions.elementToBeClickable(By.id("sp-cc-accept")));
            acceptCookiesBtn.click();
            // Short pause to allow the banner closing animation to finish
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("No cookie banner displayed, proceeding...");
        }

        String pageTitle = driver.getTitle();

        Assertions.assertTrue(pageTitle.contains("Amazon"),
                "The loaded page title does not contain 'Amazon'");
        printSuccess("Die Amazon Startseite wird erfolgreich aufgerufen");
    }

    private void test12() {
        amazonHomePage.searchForProduct("Adidas Schuhe");
        By result = By.cssSelector("div[data-component-type='s-search-result']");
        wait.until(ExpectedConditions.presenceOfElementLocated(result));

        searchResults = driver.findElements(result);

        Assertions.assertTrue(searchResults.size() >= 5,
                "Expected 5, but found: " + searchResults.size());

        printSuccess("Es werden mindestens 5 Schuhe angezeigt (Gefunden: "
                + searchResults.size() + ")");
    }

    private void test13() {
        By resultLocator = By.cssSelector("div[data-component-type='s-search-result']");
        searchResults = driver.findElements(resultLocator);

        WebElement productLinkToClick = null;

        for (WebElement product : searchResults) {
            // Check for the sponsored label
            List<WebElement> sponsoredLabels = product
                    .findElements(By.cssSelector(".puis-sponsored-label-info-icon, .s-sponsored-label-info-icon"));

            boolean isSponsored = false;
            for (WebElement label : sponsoredLabels) {
                if (label.isDisplayed()) {
                    isSponsored = true;
                    break;
                }
            }

            if (!isSponsored) {
                List<WebElement> titleElements = product
                        .findElements(By.cssSelector("a h2, h2 a, h2.a-size-base-plus"));

                if (!titleElements.isEmpty()) {
                    productLinkToClick = titleElements.getFirst();
                    break;
                }
            }
        }

        Assertions.assertNotNull(productLinkToClick, "Could not find any unsponsored product on the page");
        productLinkToClick.click();

        WebElement productName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
        Assertions.assertTrue(productName.isDisplayed(), "Product page did not load correctly");

        printSuccess("Das Produkt wird erfolgreich aufgerufen");
    }

    private void test14() {
        // Check if size selection is a standard dropdown
        List<WebElement> sizeDropdowns = driver.findElements(By.id("native_dropdown_selected_size_name"));
        if (!sizeDropdowns.isEmpty()) {
            Select sizeSelect = new Select(sizeDropdowns.getFirst());

            // Directly locate the first available option using CSS and extract its value
            WebElement firstAvailable = driver.findElement(By
                    .cssSelector("#native_dropdown_selected_size_name option.dropdownAvailable"));
            sizeSelect.selectByValue(firstAvailable.getAttribute("value"));

            // Allow time for the page to dynamically update the color or price based on size
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        }

        // Check if size selection consists of button swatches
        List<WebElement> sizeSwatches = driver.findElements(By.cssSelector("#variation_size_name ul li.swatchAvailable"));
        if (!sizeSwatches.isEmpty()) {
            // Click the first available size that isn't out of stock
            sizeSwatches.getFirst().click();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        }

        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
        addToCartBtn.click();

        wait.until(d -> {
            String countText = d.findElement(By.id("nav-cart-count")).getText();
            try {
                return Integer.parseInt(countText) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        printSuccess("Das Produkt wird erfolgreich dem Einkaufswagen hinzugefügt.");
    }

    @Test
    public void testCase2() {
        try {
            driver.get("https://www.amazon.de/");
            wait.until(ExpectedConditions.titleContains("Amazon"));

            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement acceptCookiesBtn = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("sp-cc-accept")));
                acceptCookiesBtn.click();
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("No cookie banner displayed, proceeding...");
            }

            // The reusable methode
            searchAndAddToCart("Adidas Herren Questar Flow Laufschuhe");
            searchAndAddToCart("Puma Tazon 6");
            searchAndAddToCart("Nike Air Max");

            // Final step - verification
            verifyCart();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            Assertions.fail("Test Case 2 FAILED due: " + e.getMessage());
        }
    }

    private void searchAndAddToCart(String productName) {
        amazonHomePage.searchForProduct(productName);

        // Checking that >= 1 result was found
        By resultLocator = By.cssSelector("div[data-component-type='s-search-result']");
        wait.until(ExpectedConditions.presenceOfElementLocated(resultLocator));

        // Save the current number of items in the cart before adding
        int initialCartCount = getCartCount();

        List<WebElement> searchResults = driver.findElements(resultLocator);
        Assertions.assertFalse(searchResults.isEmpty(),
                "Expected at least 1 result for " + productName);
        printSuccess("Gefunden >= 1 für: " + productName);

        // Selecting the first non-sponsored product (code from test13)
        WebElement productLinkToClick = null;
        for (WebElement product : searchResults) {
            List<WebElement> sponsoredLabels = product
                    .findElements(By.cssSelector(".puis-sponsored-label-info-icon, .s-sponsored-label-info-icon"));
            boolean isSponsored = false;

            for (WebElement label : sponsoredLabels) {
                if (label.isDisplayed()) {
                    isSponsored = true;
                    break;
                }
            }

            if (!isSponsored) {
                List<WebElement> titleElements = product
                        .findElements(By.cssSelector("a h2, h2 a, h2.a-size-base-plus"));
                if (!titleElements.isEmpty()) {
                    productLinkToClick = titleElements.getFirst();
                    break;
                }
            }
        }

        Assertions.assertNotNull(productLinkToClick,
                "Could not find any unsponsored product for: " + productName);
        productLinkToClick.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));

        // Select size and add to cart (code from test14)
        List<WebElement> sizeDropdowns = driver.findElements(By.id("native_dropdown_selected_size_name"));
        if (!sizeDropdowns.isEmpty()) {
            Select sizeSelect = new Select(sizeDropdowns.getFirst());
            WebElement firstAvailable = driver.findElement(
                    By.cssSelector("#native_dropdown_selected_size_name option.dropdownAvailable"));
            sizeSelect.selectByValue(firstAvailable.getAttribute("value"));

            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        }

        List<WebElement> sizeSwatches = driver.findElements(
                By.cssSelector("#variation_size_name ul li.swatchAvailable"));
        if (!sizeSwatches.isEmpty()) {
            sizeSwatches.getFirst().click();

            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        }

        WebElement addToCartBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
        addToCartBtn.click();

        // Check via Lambda to verify,
        // that the item has been successfully added (the cart count has increased)
        wait.until(d -> getCartCount() > initialCartCount);

        printSuccess("Das Produkt '" + productName + "' wird erfolgreich im Warenkorb abgelegt");
    }

    private int getCartCount() {
        try {
            String countText = driver.findElement(By.id("nav-cart-count")).getText();
            return Integer.parseInt(countText);
        } catch (Exception e) {
            return 0;
        }
    }

    private void verifyCart() {
        WebElement cartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart")));
        cartButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sc-active-cart")));

        String cartText = driver.findElement(By.id("sc-active-cart")).getText().toLowerCase();
        Assertions.assertTrue(cartText.contains("adidas")
                        && cartText.contains("puma")
                        && cartText.contains("nike"),
                "Not all products were found in the cart!");

        printSuccess("""
                
                Im Einkaufswagen sind die folgenden 3 Produkte zu finden:
                
                - Adidas Herren Questar Flow Laufschuhe
                - Puma Tazon 6
                - Nike Air Max""");
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
