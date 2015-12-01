from google.appengine.ext import ndb
import json
import logging
from footpath.comment import *
import re

# Get an instance of a logger
logger = logging.getLogger(__name__)

# SET NO PARENT KEY, hikes should be in different entity groups.
# We set a parent key on the 'Greetings' to ensure that they are all in the same
# entity group. Queries across the single entity group will be consistent.
# However, the write rate should be limited to ~1/second.

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
    tags = ndb.StringProperty(repeated=True)
    
    # Data
    hike_data = ndb.JsonProperty(repeated=True,indexed=False)
    annotations = ndb.JsonProperty(repeated=True,indexed=False)


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
            self.tags = re.findall("[a-z0-9]+", self.title.lower())
        else:
            self.title = "Untitled Hike"
            self.tags = ""
        
        if 'annotations' in json_object:
            self.annotations = json_object['annotations']
        
        self.hike_data = json_object['hike_data']
        bb = get_bounding_box(self.hike_data)
        self.bb_southwest = ndb.GeoPt(bb['lat_min'], bb['lng_min'])
        self.bb_northeast = ndb.GeoPt(bb['lat_max'], bb['lng_max'])
        logger.info('lat in bounds %s:%s, lng in bounds %s:%s', bb['lat_min'], bb['lat_max'], bb['lng_min'], bb['lng_max'])
        return True
            
            
    # Parse this into JSON string
    # comments is a list of JSON objects
    def to_json(self, visitor_id):
        # TODO(simon): remove extra code after migration (24Nov15)
        title = "Untitled Hike"
        if self.title:
            title = self.title
                
        comments = get_comment_list(self.key.id(), visitor_id)
        rating = get_rating(self.key.id(), visitor_id)
        
        hike_data = {
            'hike_id': self.key.id(),
            'owner_id': self.owner_id,
            'date': self.date,
            'hike_data': self.hike_data,
            'title': title,
            'comments': comments,
            'rating': rating
        }
        if self.annotations:
            hike_data.update({'annotations':self.annotations})
        
        return json.dumps(hike_data)


    # Format a brief summary of the hike, i.e. it's ID,
    # and location information. Currently only formats the ID.
    def to_location(self):
        return str(self.key.id()).strip('L')


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


# Get a list of all comments on a specific hike
def get_comment_list(hike_id, visitor_id):
    comment_list = []
    comments = Comment.query(Comment.hike_id == hike_id).fetch()
    for comment in comments:
        comment_list.append(json.loads(comment.to_json()))
    
    return comment_list


# Gets the average rating of a specific hike
def get_rating(hike_id, visitor_id):
    NO_VISITOR_RATING = -1
    
    ratings = Rating.query(Rating.hike_id == hike_id).fetch()
    if (not ratings) or (len(ratings) == 0):
        return {"rating":2.5,"count":0,"visitor_rating":NO_VISITOR_RATING}
    
    count = len(ratings)
    sum_rating = 0
    visitor_rating = NO_VISITOR_RATING
    for rating in ratings:
        if rating.owner_id == visitor_id:
            visitor_rating = rating.value
        sum_rating += rating.value
    
    return {"rating":(sum_rating/count),"count":count,"visitor_rating":visitor_rating}


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


# The Rating class models one vote for one hike.
# It can be set by the user, but never returned individually.
class Rating(ndb.Model):
    '''Models an individual Rating entry.'''
    owner_id = ndb.IntegerProperty()
    hike_id = ndb.IntegerProperty()
    value = ndb.FloatProperty(indexed=False)

    def from_json(self, json_string):
        json_object = json.loads(json_string)
        self.hike_id = json_object['hike_id']
        self.owner_id = json_object['owner_id']
        self.value = json_object['value']
        
        # Voting must be between 0 and 5 stars
        if(self.value < 0 or self.value > 5):
            return False
        return True

    # A to_json function does not make sense,
    # because ratings are converted into average on output


# Factory to turn a json-string into a valid hike object
def build_rating_from_json(json_string):
    r = Rating()
    if(r.from_json(json_string)):
        return r
    return None




