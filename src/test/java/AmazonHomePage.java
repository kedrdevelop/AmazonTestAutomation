import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonHomePage {
    private WebDriver driver;

    // I've found this id on amazon.de
    private By searchBox = By.id("twotabsearchtextbox");

    public AmazonHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void searchForProduct(String productName) {
        driver.findElement(searchBox).sendKeys(productName);
        driver.findElement(searchBox).submit();
    }
}
