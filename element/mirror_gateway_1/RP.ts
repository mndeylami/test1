import { step, TestSettings, Until, By, Device } from '@flood/element'
import * as assert from 'assert'

function sleep(milliseconds) {
  var start = new Date().getTime();
  for (var i = 0; i < 1e7; i++) {
    if ((new Date().getTime() - start) > milliseconds){
      break;
    }
  }
}

export const settings: TestSettings = {
	device: Device.iPadProLandscape,
	//userAgent: 'chrome',
	clearCache: true,
	disableCache: true,
	waitTimeout: 60,
}

/**
 * mirror_gateway_1
 * @version 1.0
 */
 
const username = "qa-test@rpperftest.com"
const password = "Elastica@123"

export default () => {
	step('Test: Start', async browser => {
	
		browser.clearBrowserCache()
		await browser.visit('https://portal.office.com')
		await browser.takeScreenshot()
		let pick_account_page = null
		let sign_in_page = null
		
		try {
			pick_account_page = await browser.findElement(By.visibleText('Pick an account'))
			sign_in_page = await browser.findElement(By.visibleText('Sign in'))
		} catch {
		}
		
		if (pick_account_page != null) {
			console.log(pick_account_page.toString())
			await browser.takeScreenshot()
			console.log("Pick Account Page")
			await browser.click(By.xpath('//*[@id="otherTileText"]'))
			await browser.wait(Until.elementIsVisible(By.visibleText('Sign in')))
		}
		await browser.type(By.xpath('//*[@id="i0116"]'), username)
		await browser.click(By.xpath('//*[@id="idSIButton9"]'))
		await browser.wait(Until.elementIsVisible(By.xpath('//*[@id="okta-signin-submit"]')) || Until.elementIsVisible(By.visibleText('Office 365')) || Until.elementIsVisible(By.visibleText('Stay signed in?')))		

		let okta_page = null
		try {
			okta_page = await browser.findElement(By.xpath('//*[@id="okta-signin-password"]'))
		} catch {
		}
		
		if (okta_page != null) {
			console.log("Okta Page")
			await browser.takeScreenshot()
			await browser.type(By.xpath('//*[@id="okta-signin-username"]'), username)
			await browser.type(By.xpath('//*[@id="okta-signin-password"]'), password)
			await browser.click(By.xpath('//*[@id="okta-signin-submit"]'))
		} else {
			await browser.takeScreenshot()
		}
		await browser.takeScreenshot()		
		//await browser.wait(Until.urlContains("isolate"))
		await browser.wait(Until.urlContains('login.microsoft') || Until.urlContains('isolate'))
		await browser.takeScreenshot()
		await browser.click(By.xpath('//*[@id="idSIButton9"]'))
		await browser.takeScreenshot()
		await browser.wait(Until.titleIs('Microsoft Office Home'))
		await browser.click(By.xpath('//*[@id="ShellDocuments_link"]/span/ohp-icon-font/span'))
		while (true) {}
	})
}




