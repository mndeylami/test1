package Elastica.RegressionResultsAnalyser;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class SeleniumExample {
	public static void main(String[] args) throws Exception {
		int iterations = 0;

		// Create a new instance of the html unit driver
		// Notice that the remainder of the code relies on the interface,
		// not the implementation.
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/lib/chromedriver");
		WebDriver driver = new ChromeDriver();
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Create a new instance of the Flood agentF
		FloodSump flood = new FloodSump();
		String username = "testuser1@mirrorgatewayo365auto1.com";
		// Inform Flood the test has started
		flood.started();

		// It's up to you to control test duration / iterations programatically.
		while (iterations < 1) {
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
				driver.findElement(By.xpath("//input[@type='email']")).sendKeys(username);
				flood.passed_transaction(driver, "Step: Username Entered in app");

				driver.findElement(By.xpath("//input[@type='submit']")).click();
				flood.passed_transaction(driver, "Step: Click Next After Username Entered");

				Thread.sleep(30000);
				// okta login flow ---
				flood.start_transaction("okta Login");
				driver.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
				driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Elastica@123");
				driver.findElement(By.xpath("//input[@value='Sign In']")).click();
				flood.passed_transaction(driver, "Step: okta Login Done");
				Thread.sleep(30000);
				driver.findElement(By.xpath("//input[@value='No']")).click();
				flood.passed_transaction(driver, "current url is" + driver.getCurrentUrl());
				Thread.sleep(30000);

				flood.start_transaction("Redirection to Excel");
				driver.get(
						"https://isolate.mig-eoe.elastica-inc.com/embeddedurl/?url=https://www.office.com/launch/excel");
				flood.passed_transaction(driver, "Step: navigated to Excel location");

				// flood.start_transaction(driver,"click on new workbook");
				Thread.sleep(20000);
				driver.findElement(By.xpath(
						"/html/body/ohp-app/div/div/div/div/div/ohp-wac-start/div/div/div[2]/div/div[1]/ohp-template-item/a/div[1]/div/div"))
						.click();
				// flood.passed_transaction("new workbook opened");

				Thread.sleep(20000);

				String originalHandle = driver.getWindowHandle();
				for (String handle : driver.getWindowHandles()) {
					if (!handle.equals(originalHandle)) {
						driver.switchTo().window(handle);
						// Perform operation on tab
						Thread.sleep(5000);
						driver.close();
					}
				}

				driver.switchTo().window(originalHandle);
				Thread.sleep(10000);

				flood.start_transaction("Step: navigated to cancel new tab");
				driver.findElement(By.xpath("//button[@id='cancelNewTabButton']")).click();
				flood.passed_transaction(driver, "Step: Cancel new tab done");

				Thread.sleep(10000);

				/**
				 * flood.start_transaction("Step: navigated to mouse hover"); WebElement
				 * moreoptions = driver.findElement(By.xpath("(//a[contains(@aria-label,'Private
				 * Excel document')])[1]")); Actions action = new Actions(driver);
				 * action.moveToElement(moreoptions).perform();
				 * flood.passed_transaction(driver,"Step: Mouse hover done");
				 **/
				flood.start_transaction("Redirection to one drive");
				driver.get(
						"https://isolate.mig-eoe.elastica-inc.com/embeddedurl/?url=https://mirrorgatewayo365auto1-my.sharepoint.com");
				flood.passed_transaction(driver, "Step: navigated to OneDrive");

				Thread.sleep(10000);

				flood.start_transaction("select file start");
				Actions action = new Actions(driver);
				WebElement checkboxelement = driver.findElement(
						By.xpath("(//a[contains(.,'Book')]/../../../../../../../../..//div[@role='checkbox'])[1]"));
				action.moveToElement(checkboxelement).click().perform();
				Thread.sleep(10000);
				flood.passed_transaction(driver, "Step: file selected");

				flood.start_transaction("file delete action start");
				driver.findElement(By.xpath("//button[@aria-label='Delete']")).click();
				flood.passed_transaction(driver, "Step: File deleted");
				Thread.sleep(5000);

				driver.findElement(By.xpath("//span[text()='Delete']/..")).click();
				Thread.sleep(5000);
				flood.passed_transaction(driver, "Step: file deleted");

				/**
				 * flood.start_transaction("Step: navigated to more option location");
				 * driver.findElement(By.xpath("(//button[@title='More options'])[1]")).click();
				 * flood.passed_transaction(driver,"Step: more option seen");
				 * 
				 * Thread.sleep(5000);
				 * 
				 * flood.start_transaction("Step: trying to remove file");
				 * driver.findElement(By.xpath("//li[@aria-label='Remove from list']")).click();
				 * flood.passed_transaction(driver, "Step: excel file deleted");
				 * 
				 **/
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