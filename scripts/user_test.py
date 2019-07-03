import json
import logging
import threading
import time
import random
import sys
import traceback
import base64
import requests
from requests import RequestException
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.util.retry import Retry
#from urllib3.util import Retry


TENANT_NAME = 'elasticaco'
DOMAIN_NAME = '@elastica.co'
USER_NAME = 'auto_perf_user@elastica.co'
PASSWORD = 'Auto_perf_user@123'

CREATE_USER = "https://perfapi.elastica-inc.com/" + TENANT_NAME + "/api/admin/v1/users/"
GET_USER_DETAIL_BY_EMAIL = "https://perfapi.elastica-inc.com/" + TENANT_NAME + "/api/admin/v1/users/?email={email}"
DELETE_USER = "https://perfapi.elastica-inc.com/" + TENANT_NAME + "/api/admin/v1/users/{id}"
BULK_USER_ADD = "https://perfapi.elastica-inc.com/" + TENANT_NAME + "/api/admin/v1/users/"


class RestClient(object):
    """ Implements all Rest methods """

    def __init__(self):
        self.retries = 5
        self.back_off_factor = 0.3
        self.status_force_list = (500, 502, 504)
        self.session = None
        self.header = {
            'authorization': get_base_auth_token(user_name=USER_NAME, password=PASSWORD),
            'cache-control': 'no-cache',
            'content-type': 'application/json',
            'x-elastica-dbname-resolved': 'True',
        }

    def requests_retry_session(self):
        """ Builds request session with """
        session = self.session or requests.Session()
        retry = Retry(
            total=self.retries,
            read=self.retries,
            connect=self.retries,
            backoff_factor=self.back_off_factor,
            status_forcelist=self.status_force_list
        )
        adapter = HTTPAdapter(max_retries=retry)
        session.mount('https://', adapter)
        return session

    def do_delete(self, url):
        """ Makes delete call """
        try:
            return self.requests_retry_session().delete(url, headers=self.header)
        except RequestException as exception:
            logging.error('Unable to make delete call')
            logging.error(exception)
            traceback.print_stack()

    def do_get(self, url):
        """ Makes get call """
        try:
            return self.requests_retry_session().get(url, headers=self.header)
        except RequestException as exception:
            logging.error('Unable to make get call')
            logging.error(exception)
            traceback.print_stack()

    def do_post(self, url, data):
        """ Makes post call """
        try:
            return self.requests_retry_session().post(url, headers=self.header, data=data)
        except RequestException as exception:
            logging.error('Unable to make post call')
            logging.error(exception)
            traceback.print_stack()

    def do_put(self, url, data):
        """ Makes put call """
        try:
            return self.requests_retry_session().put(url=url, headers=self.header, data=data)
        except RequestException as exception:
            logging.error('Unable to make put call')
            logging.error(exception)
            traceback.print_stack()

    def do_patch(self, url, data):
        """ Makes Patch call """
        try:
            return self.requests_retry_session().patch(url=url, headers=self.header, data=data)
        except RequestException as exception:
            logging.error('Unable to make patch call')
            logging.error(exception)
            traceback.print_stack()


def get_base_auth_token(user_name, password):
    return 'Basic ' + (base64.b64encode(user_name + ":" + password))


class UserHandler(object):
    """ Implements all CloudSoc User methods """

    def __init__(self):
        self.client = RestClient()

    def delete_user(self, u_id):
        """ Deletes the give user """
        request_resp = self.client.do_delete(DELETE_USER.replace('{id}', u_id))
        #logging.info("Request returned with status code: %s", request_resp.status_code)
        if request_resp.status_code == 204 or request_resp.status_code == 200 or request_resp.status_code == 201:
            #('User %s deleted', u_id)
            print "DELETED " + str(u_id)


    def get_user_details_by_email(self, user_email):
        """ Get User details for the given email_id """
        logging.debug("getting user details for %s", user_email)
        request_resp = self.client.do_get(GET_USER_DETAIL_BY_EMAIL.replace('{email}', user_email))
        if request_resp.status_code == 200:
            json_data = json.loads(request_resp.text)
            if json_data:
                return json_data['objects'][0]['id'], user_email
            else:
                logging.error('Received empty response')
                sys.exit(-1)
        else:
            logging.error('Unable to get user user details for %s', user_email)
            logging.error('Get user details API failed with status code: %s', request_resp.status_code)
            sys.exit(-1)

    def create_bulk_users(self, no_of_users):
        user_details = {}
        for counter in range(no_of_users):
            user_name = "perf_test_"+str(int(round(time.time())))
            user_email = user_name+DOMAIN_NAME
            user_post_data = {
                "is_dpo": False,
                "first_name": user_name,
                "last_name": "perf_test",
                "title": "",
                "work_phone": "",
                "notes": "Generated by Automation Script",
                "is_active": True,
                "secondary_user_id": "",
                "cell_phone": "",
                "access_profiles": [],
                "is_admin": False,
                "password": "",
                "email": user_email
            }
            print "Sending request:\n" + CREATE_USER + "\n" + str(json.dumps(user_post_data))
            request_resp = self.client.do_post(url=CREATE_USER, data=json.dumps(user_post_data))
            print "request_resp = " + str(request_resp) 
            if request_resp.status_code == 201:
                time.sleep(1)
                user_id, user_email = self.get_user_details_by_email(user_email)
                logging.debug('User %s created with email %s and id %s', counter, user_email, user_id)
                user_details[user_id] = user_email
            else:
                logging.error("Failed to create %s users. So stopping executions", no_of_users)
                logging.error("Request failed with status code: %s", request_resp.status_code)
                sys.exit(-1)
        return json.dumps(user_details)

'''
def main(): #delete
    #logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)
    logging.basicConfig(filename='tmp.log', level=logging.DEBUG)
    #user_id = "5a4fcdbf67dc8d1c7d422203"
    handler = UserHandler()
    f = open("TTTae", 'r')
    for id in f:
        id = id.strip()
        handler.delete_user(id)
    chunk_size = 15
    #while chunk_size <= 30:
    #    print handler.get_user_details_by_email("perf_test_*")
    #    chunk_size += 15
    #print handler.get_user_details_by_email("perf_test_1515179458@elastica.co")
    print handler.get_users_details_by_email("perf_test")
'''

def main_create():
	handler = UserHandler()
	handler.create_bulk_users(1)
main_create()
