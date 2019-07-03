import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.WebDriverException;

import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.openqa.selenium.support.ui.Select;

import io.flood.selenium.FloodSump;

public class flood_outlook2  {
	public static final String USERNAME = "mig-test@tryelasticarpedge.com";
	public static final String PASSWORD = "Elastica@123";
    public static void main(String[] args) throws Exception {
        int iterations = 0;
        
        // Create a new instance of the html unit driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new RemoteWebDriver(new URL("http://" + System.getenv("WEBDRIVER_HOST") + ":" + System.getenv("WEBDRIVER_PORT") + "/wd/hub"), DesiredCapabilities.chrome());
        JavascriptExecutor js = (JavascriptExecutor)driver;
        
        // Create a new instance of the Flood agentF
        FloodSump flood = new FloodSump();
        String username="mig-test@tryelasticarpedge.com";
        // Inform Flood the test has started
        flood.started();

        // It's up to you to control test duration / iterations programatically.
        while (iterations < 2) {
            try {
                System.out.println("Starting iteration " + String.valueOf(iterations));

                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

                // And now use this to visit the target site

                driver.get("https://portal.office.com");

                // Log a passed transaction in Flood
                Thread.sleep(15000);
                flood.start_transaction("Username Entry");
                driver.findElement(By.xpath("//input[@type='email']")).click();
                driver.findElement(By.xpath("//input[@type='email']")).clear();
                driver.findElement(By.xpath("//input[@type='email']")).sendKeys(USERNAME);
                flood.passed_transaction(driver, "Step: Username Entered in app");

                driver.findElement(By.xpath("//input[@type='submit']")).click();
                flood.passed_transaction(driver, "Step: Click Next After Username Entered");

                Thread.sleep(30000);
                // okta login flow ---
                flood.start_transaction("okta Login");
                driver.findElement(By.xpath("//input[@name='username']")).sendKeys(USERNAME);
                driver.findElement(By.xpath("//input[@name='password']")).sendKeys(PASSWORD);
                driver.findElement(By.xpath("//input[@value='Sign In']")).click();
                flood.passed_transaction(driver, "Step: okta Login Done");

                Thread.sleep(40000);
                driver.findElement(By.xpath("//input[@value='No']")).click();
                flood.passed_transaction(driver, "current url is" + driver.getCurrentUrl());

                Thread.sleep(50000);
                flood.start_transaction("Redirection to Excel");
                driver.get(
                        "https://isolate.mig-eoe.elastica-inc.com/embeddedurl/?url=https://outlook.office365.com/");
                Thread.sleep(30000);
                 flood.passed_transaction(driver, "Step: navigated to Outlook");
                // flood.start_transaction(driver,"click on new workbook");
				iterations++;

            }

            catch (WebDriverException e) {
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Webdriver exception: " + lines[0]);
                flood.failed_transaction(driver);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Browser terminated early: " + lines[0]);
            } catch (Exception e) {
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Other exception: " + lines[0]);
            } finally {
                iterations++;

            }

            // Inform Flood that the test has finished
            
        }
        flood.finished();
        driver.quit();
    }
}
