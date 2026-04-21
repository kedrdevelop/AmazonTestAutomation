import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AmazonHomePage {
    private WebDriver driver;

    // I've found this id on amazon.de
    private By searchBox = By.id("twotabsearchtextbox");

    public AmazonHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void searchForProduct(String productName) {
        WebElement input = driver.findElement(searchBox);
        input.clear();
        input.sendKeys(productName);
        input.submit();
    }
}
