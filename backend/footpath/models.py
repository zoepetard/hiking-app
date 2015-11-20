from google.appengine.ext import ndb
import json
import logging

DEFAULT_NAME = 'some_global_string'

# Get an instance of a logger
logger = logging.getLogger(__name__)

# SET NO PARENT KEY, hikes should be in different entity groups.
# We set a parent key on the 'Greetings' to ensure that they are all in the same
# entity group. Queries across the single entity group will be consistent.
# However, the write rate should be limited to ~1/second.

#def _key(key_name=DEFAULT_NAME):
#    '''Constructs a Datastore key for a _ entity with key_name.'''
#    return ndb.Key('KeyNameString', key_name)

# A proper response is
# { 
#   "hike_id": 268, 
#   "owner_id": 153, 
#   "date": 123201,
#   "hike_data": [
#      [0.0, 0.0, 123201], [0.1, 0.1, 123202], [0.2, 0.0, 123203],
#      [0.3,89.9, 123204], [0.4, 0.0, 123205]
#   ]
# }
            
class Hike(ndb.Model):
    '''Models an individual Hike entry.'''
    # TODO add indexed=False to properties that should not be indexed
    # Management (set by backend)
    author = ndb.UserProperty()
    last_changed = ndb.DateTimeProperty(auto_now_add=True)
    bb_southwest = ndb.GeoPtProperty()
    bb_northeast = ndb.GeoPtProperty()
    
    # Header
    hike_id = ndb.IntegerProperty()
    owner_id = ndb.IntegerProperty()
    date = ndb.IntegerProperty()
    start_point = ndb.GeoPtProperty()
    finish_point = ndb.GeoPtProperty()
    title = ndb.StringProperty()
    
    # Data
    hike_data = ndb.JsonProperty(repeated=True,indexed=False)

    # Parse JSON string to data. Return false on malformed input
    # TODO: check input
    def from_json(self, json_string):
        json_object = json.loads(json_string)
        self.hike_id = json_object['hike_id'] # TODO this will be automatically set
        self.owner_id = json_object['owner_id']
        self.date = json_object['date']
        # TODO(simon): remove extra code after migration (24Nov15)
        if 'title' in json_object:
            self.title = json_object['title']
        else:
            self.title = "Untitled Hike"
        self.hike_data = json_object['hike_data']
        #self.some_string = json_object['some_string']
        bb = get_bounding_box(self.hike_data)
        self.bb_southwest = ndb.GeoPt(bb['lat_min'], bb['lng_min'])
        self.bb_northeast = ndb.GeoPt(bb['lat_max'], bb['lng_max'])
        logger.info('lat in bounds %s:%s, lng in bounds %s:%s', bb['lat_min'], bb['lat_max'], bb['lng_min'], bb['lng_max'])
        return True
                
    # Parse this into JSON string
    def to_json(self):
        # TODO(simon): remove extra code after migration (24Nov15)
        title = "Untitled Hike"
        if self.title:
            title = self.title
        
        hike_data = {
            'hike_id': self.key.id(),
            'owner_id': self.owner_id,
            'date': self.date,
            'hike_data': self.hike_data,
            'title': title,
        }
        return json.dumps(hike_data)
    
# Get an array-dict containing the data from the hike_data object
def get_bounding_box(hike_data):
    latitudes = [point[0] for point in hike_data]
    longitudes = [point[1] for point in hike_data]   
    
    # TODO Note that for a hike across the pacific
    # the bounding box is not calculated correctly
    lat_min = min(latitudes)
    lng_min = min(longitudes)
    lat_max = max(latitudes)
    lng_max = max(longitudes)
    
    return {'lat_min' : lat_min, 'lng_min' : lng_min, 'lat_max' : lat_max, 'lng_max' : lng_max}

# Factory to turn a json-string into a valid hike object
def build_hike_from_json(json_string):
    t = Hike()
    if(t.from_json(json_string)):
        return t
    return None
    
def build_sample_hike(hike_id, owner_id):
    return build_hike_from_json("{\n"\
            + "  \"hike_id\": "+repr(hike_id)+",\n"\
            + "  \"owner_id\": "+repr(owner_id)+",\n"\
            + "  \"date\": 123201,\n"\
            + "  \"hike_data\": [\n"\
            + "    [0.0, 0.0, 123201, 0.5],\n"\
            + "    [0.1, 0.1, 123202, 1.0],\n"\
            + "    [0.2, 0.0, 123203, 0.5],\n"\
            + "    [0.3,89.9, 123204, 2.0],\n"\
            + "    [0.4, 0.0, 123205, 1.0]\n"\
            + "  ]\n"\
            + "}")