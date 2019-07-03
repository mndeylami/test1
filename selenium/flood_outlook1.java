import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;

import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.openqa.selenium.support.ui.Select;

import io.flood.selenium.FloodSump;

public class flood_outlook1  {

	public static final String USERNAME = "mig-test@tryelasticarpedge.com";
	public static final String PASSWORD = "Elastica@123";
	public static final String HOMEPAGE = "https://portal.office.com";

    public static void main(String[] args) throws Exception {

        int iterations = 0;
        
        // Create a new instance of the html unit driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new RemoteWebDriver(new URL("http://" + System.getenv("WEBDRIVER_HOST") + ":" + System.getenv("WEBDRIVER_PORT") + "/wd/hub"), DesiredCapabilities.chrome());
        JavascriptExecutor js = (JavascriptExecutor)driver;
		WebDriverWait wait = new WebDriverWait(driver, 10);
		driver.manage().deleteAllCookies();
        
        // Create a new instance of the Flood agentF
        FloodSump flood = new FloodSump();
        
        // Inform Flood the test has started
        flood.started();
        
        while (iterations < 1) {
            try {
                System.out.println("Starting iteration " + String.valueOf(iterations));                
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);                
                driver.get("https://portal.office.com");
                
                // Log a passed transaction in Flood
				// Login
                flood.start_transaction("1");
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"idSIButton9\"]")));
				driver.findElement(By.xpath("//*[@id=\"i0116\"]")).sendKeys(USERNAME);
				driver.findElement(By.xpath("//*[@id=\"idSIButton9\"]")).click();
				flood.passed_transaction(driver, "1");
				// Okta
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\'okta-signin-submit\']")));
				Thread.sleep(10000);
				driver.findElement(By.xpath("//*[@id=\"okta-signin-username\"]")).sendKeys(USERNAME);
				driver.findElement(By.xpath("//*[@id=\"okta-signin-password\"]")).sendKeys(PASSWORD);
				driver.findElement(By.xpath("//*[@id=\"okta-signin-submit\"]")).click();
				flood.passed_transaction(driver, "2");
				
				// Stay singed in 
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value=\"Yes\"]")));
				Thread.sleep(10000);
				driver.findElement(By.xpath("//input[@value=\"Yes\"]")).click();
		
				// Click on Outlook
		
				//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span/ohp-icon-font/span")));
				Thread.sleep(10000);
				driver.findElement(By.xpath("//span/ohp-icon-font/span")).click();
				Thread.sleep(10000);
				flood.passed_transaction(driver, "3");

				WebElement btn_confirm = null;

				// In Firefox a new tab should be confirmed
				try {
					btn_confirm = driver.findElement(By.xpath("//*[@id=\"applyNewTabButton\"]"));
				} catch (Exception e) {}
				if (btn_confirm != null)
					btn_confirm.click();
		
				Thread.sleep(5000);

				// Switch to the Outlook tab
				ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
				driver.switchTo().window(tabs.get(1)); //switches to new tab
				Thread.sleep(2000);
		
				flood.passed_transaction(driver, "4");
		
				//driver.get("https://isolate.mig-eoe.elastica-inc.com/embeddedurl/?url=https://outlook.office365.com/");
				//Thread.sleep(5000);
		
				// New email CTRL+n
				Actions a = new Actions(driver);
				a.keyDown(Keys.CONTROL).sendKeys("n").build().perform();
				Thread.sleep(5000);

				// Address
				WebElement txt_address = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[2]/div/div[1]/div/div[3]/div[2]/div/div[5]/div/div/div[2]/div/div/div[1]/div/div[1]/div[1]/div[1]/div/div[1]/div[1]/div/div/div/span/div/form/input"));
				//txt_address.click();
				//txt_address.clear();
				txt_address.sendKeys("mndeylami@gmail.com");
				Thread.sleep(2000);
				txt_address.sendKeys(Keys.ENTER);
				Thread.sleep(2000);

				// Title
				WebElement txt_title = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[2]/div/div[1]/div/div[3]/div[2]/div/div[5]/div/div/div[2]/div/div/div[1]/div/div[1]/div[1]/div[1]/div/div[3]/div[2]/div/input"));
				txt_title.click();
				//txt_title.clear();
				txt_title.sendKeys("Title");
				Thread.sleep(2000);
		
				// Body
				WebElement txt_body = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[2]/div/div[1]/div/div[3]/div[2]/div/div[5]/div/div/div[2]/div/div/div[1]/div/div[1]/div[2]/div[2]/div/div/div/div[2]/textarea"));
				txt_body.click();
				//txt_body.clear();
				txt_body.sendKeys("Body");
				Thread.sleep(2000);

				// Send
				try {
					WebElement btn_send = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div[2]/div/div[1]/div/div[3]/div[2]/div/div[5]/div/div/div[2]/div/div/div[1]/div/div[2]/div/div[1]/button[1]"));
					btn_send.click();
				} catch (Exception ex) {}
				flood.passed_transaction(driver, "5");
		
				Thread.sleep(20000);
                
                iterations++;
                
                
            } catch (WebDriverException e) {
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Webdriver exception: " + lines[0]);
                flood.failed_transaction(driver);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Browser terminated early: " + lines[0]);
            } catch(Exception e) {
                String[] lines = e.getMessage().split("\\r?\\n");
                System.err.println("Other exception: " + lines[0]);
            } finally {
                iterations++;
            }
        }
        
        driver.quit();
        
        // Inform Flood that the test has finished
        flood.finished();
    }
}

