import time
import requests
import sys
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.common.by import By

application_url = sys.argv[1].rstrip('/')
token = ""
REFRESH_PAGE_TIMEOUT = 55


def get_valid_jwt(driver):
	print("[+] Getting brand new auth token...")
	global token

	body = {
		"username" : "remediation-admin@email.com",
		"password" : "R3m3dl@tl0n5"
	}

	resp = requests.post(application_url + "/api/authenticate", json = body)
	token = resp.json()["token"]
	driver.execute_script("window.localStorage.setItem('token','" + token  + "');")


def is_jwt_valid(token):
	headers = {"Authorization" : "Bearer " + token}
	resp = requests.get(application_url + "/api/verify", headers = headers)
		
	return resp.status_code != 401

#
# STEP 1 : Wait for the application to start properly
#
print("[-] Waiting for application...")
run = False

while not run:
	try :
		response = requests.get(application_url)
		if response.status_code == 200:
			run = True
			print("[+] Application is started and reachable...")
	except KeyboardInterrupt:
		exit()
	except:
		time.sleep(3)

#
# STEP 2 : Check & answer messages every minute
#
try:
	print("[+] Launching emulated browser...")
	options = Options()
	options.add_argument('--headless')
	driver = webdriver.Firefox(options=options)
	driver.get(application_url + "/")

	get_valid_jwt(driver)
	
	print("[+] Reaching messages page for the first time...")
	driver.get(application_url + "/admin/view/message")
	driver.set_window_size(1000, 700)
	time.sleep(5)
	
	while True :
		if not is_jwt_valid(token):
			get_valid_jwt()

		print(f"=====================  {datetime.now().strftime('%H:%M:%S')}  =====================")
		print(f"[+] Checking admin messages...")
		driver.refresh()
		time.sleep(5)

		print("[+] Taking screenshot (to debug)")
		driver.save_screenshot('/tmp/screen.png')
		
		has_messages = True if 'Waiting for client' in driver.page_source else False
		print(f"[+] Message detected : {has_messages}")

		print("[+] Deleting read messages")
		buttons = driver.find_elements(By.CLASS_NAME, "action-delete")
		for button in buttons:
			button.click()
			time.sleep(1)
		print("======================================================\n")

		time.sleep(REFRESH_PAGE_TIMEOUT - len(buttons))

except Exception as e :
	print(f"[!] ERROR : {e}")
finally:
	driver.quit()
	exit()
