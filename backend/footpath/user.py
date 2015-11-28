from google.appengine.ext import ndb
import json
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)


            
class User(ndb.Model):
    '''Models an individual User.'''
    # TODO add indexed=False to properties that should not be indexed
    
    # user_id is key
    name = ndb.StringProperty()
    mail_address = ndb.StringProperty()

    # Parse JSON string to data. Throw exception on malformed input
    def from_json(self, json_string):
        logger.info("PARSE: "+json_string)
        json_object = json.loads(json_string)
        uid = json_object['user_id']
        self.request_user_id = uid
        self.name = json_object['user_name']
        self.mail_address = json_object['mail_address']
        logger.info('Created user %s with email %s', self.name, self.mail_address)
        return True
                
    # Parse this into JSON string
    def to_json(self):
        hike_data = {
            'user_id': self.key.id(),
            'user_name': self.name,
            'mail_address': self.mail_address,
        }
        return json.dumps(hike_data)

# Factory to turn a json-string into a valid hike object
def build_user_from_json(json_string):
    user = User()
    if(user.from_json(json_string)):
        return user
    return None

def build_user_from_name_and_address(name, mail_address):
    user = User()
    user.name = name
    user.mail_address = mail_address
    return user