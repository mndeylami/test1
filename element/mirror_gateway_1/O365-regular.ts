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
 
const username = "qa-admin@tryelasticarpedge.onmicrosoft.com"
const password = "WshV]L&9rHnQed&S"

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
		await browser.wait(Until.elementIsVisible(By.visibleText('Enter password')))		
		await browser.takeScreenshot()		

		await browser.type(By.xpath('//*[@id="i0118"]'), password)
		await browser.click(By.xpath('//*[@id="idSIButton9"]'))
		await browser.takeScreenshot()

		let keep_signed_in = null
		
		try {
			keep_signed_in = await browser.findElement(By.visibleText('Stay signed in?'))
		} catch {
		}
		if (keep_signed_in != null) {
			await browser.click(By.xpath('//*[@id="idSIButton9"]'))
		}

		await browser.wait(Until.titleIs('Microsoft Office Home'))
		await browser.click(By.xpath('//*[@id="ShellDocuments_link"]/span/ohp-icon-font/span'))
		//while (true) {}
	})
}




