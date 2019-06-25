import time
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.common.action_chains import ActionChains

username = "mig-test@tryelasticarpedge.com"
password = "Elastica@123"
ELEMENT_DELAY = 100
FIREFOX_DRIVER_PATH = "/usr/local/bin/geckodriver"
options = Options()
#options.add_argument("--headless")

def wait_for_element(driver, look_by, element, delay=ELEMENT_DELAY):

    try:
        return WebDriverWait(driver, delay, poll_frequency=.2).until(ec.presence_of_element_located((look_by, element)))
    except NoSuchElementException:
        driver.save_screenshot("/home/madmin/screenshot.png")
        raise

def main():
    try:
        profile = webdriver.FirefoxProfile()
        profile.accept_untrusted_certs = True
        #profile.set_preference("browser.link.open_newwindow", 1)
        #profile.set_preference("browser.link.open_newwindow.restriction", 0)
        options.set_preference("browser.link.open_newwindow.restriction", 0)
        options.set_preference("browser.link.open_newwindow", 3)
        driver = webdriver.Firefox(firefox_options=options, firefox_profile=profile) #, executable_path=FIREFOX_DRIVER_PATH)
        driver.get('https://portal.office.com')

        # Sign in page
        txt_username = wait_for_element(driver=driver, look_by=By.XPATH, element='//*[@id="i0116"]')
        txt_username.send_keys(username)
        time.sleep(1)
        btn_next = driver.find_element_by_xpath('//*[@id="idSIButton9"]')
        btn_next.click()

        # Okta page
        txt_okta_username = wait_for_element(driver=driver, look_by=By.XPATH, element='//*[@id="okta-signin-username"]')
        txt_okta_username.send_keys(username)
        txt_okta_password = btn_next = driver.find_element_by_xpath('//*[@id="okta-signin-password"]')
        txt_okta_password.send_keys(password)
        time.sleep(1)
        btn_okta_sign_in = driver.find_element_by_xpath('//*[@id="okta-signin-submit"]')
        btn_okta_sign_in.click()

        # Stay signed in page
        btn_yes =  wait_for_element(driver=driver, look_by=By.XPATH, element='//input[@value="Yes"]')
        time.sleep(1)
        btn_yes.click()

        #Office365 page
        #link_outlook = wait_for_element(driver=driver, look_by=By.XPATH, element='//span/ohp-icon-font/span')
        link_outlook = wait_for_element(driver=driver, look_by=By.XPATH, element='/html/body/ohp-app/div/div/div/div/div/ohp-hero/div/div/div/ohp-app-tiles-list/div/div[2]/div[1]/div/div[1]/ohp-app-tile/div/a/div')
        link_outlook.click()

        time.sleep(1)
        btn_confirm = None
        try:
            btn_confirm = driver.find_element_by_xpath('//*[@id="applyNewTabButton"]')
        except Exception as ex:
            pass
        if btn_confirm:
            btn_confirm.click()

        driver.save_screenshot("tmp1.png")
        #driver.close()
        tab_email = driver.window_handles[1]
        driver.switch_to.window(tab_email)
        #driver.find_element_by_tag_name('body').send_keys(Keys.CONTROL + Keys.TAB)
        #btn_new = wait_for_element(driver=driver, look_by=By.XPATH, element='//*[@id="_ariaId_23"]')
        #btn_new = wait_for_element(driver=driver, look_by=By.ID, element='_ariaId_23')
        #btn_new.click()
        #driver.get_screenshot_as_png()
        driver.save_screenshot("tmp2.png")
        #time.sleep(10)
        wait_for_element(driver=driver, look_by=By.PARTIAL_LINK_TEXT, element="Office 365")
        time.sleep(1)
        ActionChains(driver).key_down(Keys.CONTROL).send_keys('n').key_up(Keys.CONTROL).perform()
        driver.find_element_by_tag_name('body').send_keys('mndeylami@gmail.com')
        driver.find_element_by_tag_name('body').send_keys(Keys.ENTER)
        driver.find_element_by_tag_name('body').send_keys(Keys.TAB)
        driver.find_element_by_tag_name('body').send_keys(Keys.TAB)
        driver.find_element_by_tag_name('body').send_keys('Test Email')
        driver.find_element_by_tag_name('body').send_keys(Keys.TAB)
        driver.find_element_by_tag_name('body').send_keys('How are you?')
        driver.find_element_by_tag_name('body').send_keys(Keys.ENTER)
        driver.find_element_by_tag_name('body').send_keys(Keys.TAB)
        time.sleep(1)
        driver.find_element_by_tag_name('body').send_keys(Keys.ENTER)
        time.sleep(10)

        #wait_for_element(driver, By.NAME, 'waiting', 1000)
    finally:
        driver.close()

main()

