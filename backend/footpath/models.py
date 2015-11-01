from google.appengine.ext import ndb
import json
from datetime import datetime

DEFAULT_NAME = 'some_global_string'

# SET NO PARENT KEY, tracks should be in different entity groups.
# We set a parent key on the 'Greetings' to ensure that they are all in the same
# entity group. Queries across the single entity group will be consistent.
# However, the write rate should be limited to ~1/second.

#def _key(key_name=DEFAULT_NAME):
#    '''Constructs a Datastore key for a _ entity with key_name.'''
#    return ndb.Key('KeyNameString', key_name)

# A proper response is
# { 
#   "track_id": 268, 
#   "owner_id": 153, 
#   "date": 123201,
#   "track_data": [
#      [0.0, 0.0, 123201], [0.1, 0.1, 123202], [0.2, 0.0, 123203],
#      [0.3,89.9, 123204], [0.4, 0.0, 123205]
#   ]
# }
            
class Track(ndb.Model):
    '''Models an individual Track entry.'''
    # TODO add indexed=False to properties that should not be indexed
    # Management (set by backend)
    author = ndb.UserProperty()
    last_changed = ndb.DateTimeProperty(auto_now_add=True)
    bounding_botleft = ndb.GeoPtProperty()
    bounding_toprght = ndb.GeoPtProperty()
    
    # Header
    track_id = ndb.IntegerProperty()
    owner_id = ndb.IntegerProperty()
    date = ndb.IntegerProperty()
    start_point = ndb.GeoPtProperty()
    finish_point = ndb.GeoPtProperty()
    
    # Data
    track_data = ndb.JsonProperty(repeated=True,indexed=False)
    
    # DEBUG
    some_string = ndb.StringProperty()

    # Parse JSON string to data. Return false on malformed input
    # TODO: check input
    def from_json(self, json_string):
        json_object = json.loads(json_string)
        self.track_id = json_object['track_id'] # TODO this will be automatically set
        self.owner_id = json_object['owner_id']
        self.date = json_object['date']
        self.track_data = json_object['track_data']
        #self.some_string = json_object['some_string']
        return True
                
    # Parse this into JSON string
    def to_json(self):
        track_data = {
            'track_id': self.track_id,
            'owner_id': self.owner_id,
            'date': self.date,
            'track_data': self.track_data,
            'some_string': self.some_string,
        }
        return json.dumps(track_data)
    
# Get an array-dict containing the data from the track_data object
def read_track_data(track_data):
    track_data
    # TODO implement

# Factory to turn a json-string into a valid track object
def build_track_from_json(json_string):
    t = Track()
    if(t.from_json(json_string)):
        return t
    return None