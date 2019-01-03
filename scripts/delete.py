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
from urllib3.util import Retryfrom

requests
import RequestException
from requests.adapters import HTTPAdapter

TENANT_NAME = 'elasticaco'
DOMAIN_NAME = '@elastica.co'
USER_NAME = 'anuvrath.joshi@elastica.co'
PASSWORD = 'Elastica@2017'

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

    def __init__(self, no_of_users):
        self.no_of_users = int(no_of_users)
        self.client = RestClient()

    def delete_user(self, u_id):
        """ Deletes the give user """
        request_resp = self.client.do_delete(DELETE_USER.replace('{id}', u_id))
        logging.info("Request returned with status code: %s", request_resp.status_code)
        if request_resp.status_code == 204:
            logging.info('User %s deleted', u_id)



