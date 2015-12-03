from google.appengine.ext import ndb
import json
import logging
from time import time
import hashlib

# Get an instance of a logger
logger = logging.getLogger(__name__)


            
class User(ndb.Model):
    '''Models an individual User.'''
    
    # user_id is key
    name = ndb.StringProperty(indexed=False)
    mail_address = ndb.StringProperty()
    db_token = ndb.StringProperty(indexed=False)
    profile_image_id = ndb.IntegerProperty(indexed=False)

    # Parse JSON string to data. Throw exception on malformed input
    def from_json(self, json_string):
        logger.info("PARSE: "+json_string)
        json_object = json.loads(json_string)
        uid = json_object['user_id']
        self.request_user_id = uid
        self.name = json_object['user_name']
        self.mail_address = json_object['mail_address']
        if 'profile_image_id' in json_object:
            self.profile_image_id = json_object['profile_image_id']
        logger.info('Created user %s with email %s', self.name, self.mail_address)
        return True
                
    # Parse this into JSON string
    def to_json(self):
        if not self.profile_image_id:
            self.profile_image_id = -1
        hike_data = {
            'user_id': self.key.id(),
            'user_name': self.name,
            'mail_address': self.mail_address,
            'profile_image_id' : self.profile_image_id
        }
        return json.dumps(hike_data)
    
    
    # Parse this into JSON string
    def to_login_json(self):
        
        if not self.db_token:
            self.db_token = ""
        
        hike_data = {
            'user_id': self.key.id(),
            'mail_address': self.mail_address,
            'token': self.db_token
        }
        return json.dumps(hike_data)

# Factory to turn a json-string into a valid hike object
def build_user_from_json(json_string):
    user = User()
    if(user.from_json(json_string)):
        return user
    return None

def build_user_from_name_and_address(name, mail_address, token):
    user = User()
    user.name = name
    user.mail_address = mail_address
    user.db_token = hashlib.sha224((name + mail_address + repr(time()) + token).encode('utf-8')).hexdigest()
    return user