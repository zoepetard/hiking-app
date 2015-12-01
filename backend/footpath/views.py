from django.http import HttpResponse
from django.core import serializers

from google.appengine.api import users
from google.appengine.api import search

from footpath.hike import *
from footpath.user import *
from footpath.image import *
from footpath.auth import *
from footpath.comment import *
import re

import logging
import json

# Get an instance of a logger
logger = logging.getLogger(__name__)

def get_hike(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    request_hike_id = int(request.META.get('HTTP_HIKE_ID', -1))
    logger.info('got request for hike id %s', repr(request_hike_id))
    if request_hike_id <= 0:
        return response_bad_request()
    
    hike = ndb.Key(Hike, request_hike_id).get()
    if not hike:
        return response_not_found()

    return response_data(hike.to_json(visitor_id))


# Temporary: Function to quickly see the database in browser
# Get multiple hikes, as specified in a list inside the field
# hike_ids of the GET request
def get_hikes(request):
    
    hikes = Hike.query().fetch()
    
    all_hikes = ""
    for hike in hikes:
        hike_string = hike.to_json(visitor_id)
        key_string = str(hike.key.id()).strip('L')
        all_hikes += hike_string + ' with key=' + key_string + '\n'
    
    return HttpResponse(all_hikes, content_type='application/javascript')
 

# Gets all hikes in a bounding box specified in the request.
def get_hikes_in_window(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    # Get window from input
    request_bounding_box = request.META.get('HTTP_BOUNDING_BOX', -1)
    logger.info('got request for hikes in window %s', request_bounding_box)
    
    bb = json.loads(request_bounding_box)
    lat_min = float(bb['lat_min'])
    lng_min = float(bb['lng_min'])
    lat_max = float(bb['lat_max'])
    lng_max = float(bb['lng_max'])
    window_southwest = ndb.GeoPt(lat=lat_min, lon=lng_min)
    window_northeast = ndb.GeoPt(lat=lat_max, lon=lng_max)
    
    # query database
    # since queries by two different keys are not possible, we query by one key and sort afterwards
    hikes = Hike.query(Hike.bb_northeast > window_southwest).fetch()
    
    # check results of query and assemble output string
    hike_ids = []
    for hike in hikes:
        if (hike.bb_southwest.lat < window_northeast.lat and hike.bb_southwest.lon < window_northeast.lon
            and hike.bb_northeast.lat > window_southwest.lat and hike.bb_northeast.lon > window_southwest.lon):
            hike_ids.append(hike)

    return response_hike_locations(hike_ids)


# Gets all hikes in a bounding box specified in the request.
def get_hikes_of_user(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    # Get window from input
    request_user_id = int(request.META.get('HTTP_USER_ID', -1))
    logger.info('got request for hikes of user %s', repr(request_user_id))
    
    # query database
    hikes = Hike.query(Hike.owner_id == request_user_id).fetch()

    return response_hike_locations(hikes)


# Gets all hikes containing a certain keyword
def get_hikes_with_keywords(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    # Get window from input
    request_keywords = request.META.get('HTTP_KEYWORDS', -1)
    keywords = re.findall("[a-z0-9]+", request_keywords.lower())
    #keywords = " AND ".join(keywords)
    logger.info("Keywords are: "+repr(keywords))

    hikes = Hike.query()
    hikes = hikes.filter(Hike.title.IN(keywords))
    hikes = hikes.fetch()

    return response_hike_locations(hikes)


# Format a brief summary of the hike, i.e. it's ID,
# and location information. Currently only formats the ID.
def hike_location(hike):
    return str(hike.key.id()).strip('L')


def post_hike(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    logger.info('POST request '+repr(request.body))
    
    # Create new Hike object
    hike = build_hike_from_json(request.body)
    if not hike:
        return response_bad_request()
            
    if not has_write_permission(visitor_id, hike.owner_id):
        return response_forbidden()
    
    #TODO(simon): set test flag on hikes that should be automatically removed
        
    # If update hike: Authenticate and check for existing hikes in database
    if(hike.hike_id >= 0):
        old_hike = ndb.Key(Hike, hike.hike_id).get()
        
        if not old_hike:
            return response_not_found()
            
        if old_hike.owner_id != hike.owner_id:
            return response_forbidden()
        
        hike.key = ndb.Key(Hike, hike.hike_id)
    
    new_key = hike.put()

    return response_id('hike_id', new_key.id())


# Delete a user. The hike can only be deleted by its author.
def delete_hike(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    delete_hike_id = int(json.loads(request.body)['hike_id'])
    
    hike = ndb.Key(Hike, delete_hike_id).get()
    if not hike:
        return response_not_found()
    if not has_write_permission(visitor_id, hike.owner_id):
        return response_forbidden()

    hike.key.delete()
    return response_data('')


# Login a user. The email address is stored in the http request field "user_mail_address"
def login_user(request):
    
    login_request = request.META.get('HTTP_LOGIN_REQUEST', '')
    if len(login_request) == 0:
        #return response_bad_request() TODO(simon) compatibility activate
        #logger.error(repr(request))
        user = find_user_with_email(request.META.get('HTTP_USER_MAIL_ADDRESS', ''))
    
        test_users = User.query(User.mail_address == "bort@googlemail.com").fetch()
        for test_user in test_users:
            if test_user.key.id() != user.key.id():
                test_user.key.delete()

        return response_data(user.to_json())

    login_request = json.loads(login_request)
    request_user_email = login_request['mail_address']
    
    logger.info("Searching for user with email "+request_user_email)
    user = find_user_with_email(request_user_email)

    #if user:
        #TODO(simon) compatibility delete
        #user.key.delete()
        #user = None

    if not user:
        name = login_request['user_name_hint']
        id_token = login_request['id_token']
        user = build_user_from_name_and_address(name, request_user_email, id_token)
        if not user:
            return response_internal_error()
        user.put()

    return response_data(user.to_login_json())


# Get the user ID from an email address. Returns -1 on not found.
# Returns ID of some user if more than one user have the same address.
def find_user_with_email(mail_address):
    users = User.query(User.mail_address == mail_address).fetch()
    if users and len(users)>0:
        logger.info("Found "+repr(len(users))+" user(s) with email "+mail_address+"!")
        return users[0]
    return None


# Create a new user in the database, or update a current one
def post_user(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST request '+repr(request.body))
    
    # Create new User object
    user = build_user_from_json(request.body)
    if not user:
        return response_bad_request()

    if user.request_user_id <= 0:
        #return response_bad_request() TODO(simon) compatibility activate
        user.put()
        user.request_user_id = user.key.id()

    
    # If update hike: Authenticate and check for existing hikes in database
    if not has_write_permission(visitor_id, user.request_user_id):
        return response_forbidden()
    
    old_user = ndb.Key(User, user.request_user_id).get()
        
    if not old_user:
        return response_not_found()
        
    # Set the new user's database key to an existing one,
    # so that one will be overwritten
    user.key = ndb.Key(User, user.request_user_id)
    user.db_token = old_user.db_token

    # Store new user in database and return the new id
    new_key = user.put()
    logger.info('respond with ID '+repr(new_key.id()))
    return response_id('user_id', new_key.id())


# Get a user. The numerical user ID is stored in the http request field "user_id"
def get_user(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    request_user_id = int(request.META.get('HTTP_USER_ID', -1))
    logger.info('get_user got request for user id %s', repr(request_user_id))

    if request_user_id <= 0:
        return response_bad_request()
    
    user = ndb.Key(User, request_user_id).get()
    if not user:
        return response_not_found()
        
    return response_data(user.to_json())


# Delete a user. The author of this request can only delete himself.
def delete_user(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    delete_user_id = int(json.loads(request.body)['user_id'])

    if not has_write_permission(visitor_id, delete_user_id):
        return response_forbidden()

    user_obj = ndb.Key(User, delete_user_id).get()
    if not user_obj:
        return response_not_found()

    user_obj.key.delete()

    return response_data('')


# Post a new image
def post_image(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST image from '+repr(visitor_id))
    request_image_id = int(request.META.get('HTTP_IMAGE_ID', '-1'))
    
    # Create new Hike object
    img = build_image(visitor_id, request.body)
    if not img:
        return response_bad_request()
    if not has_write_permission(visitor_id, img.owner_id):
        return response_forbidden()

    # If update hike: Authenticate and check for existing hikes in database
    logger.info('request post to ID '+repr(request_image_id))
    if(request_image_id > 0):
        old_image = ndb.Key(Image, request_image_id).get()
        
        if not old_image:
            return response_not_found()

        if not has_write_permission(visitor_id, old_image.owner_id):
            return response_forbidden()
        
        img.key = old_image.key

    new_key = img.put()
    logger.info('respond with ID '+repr(new_key.id()))
    return response_id('image_id', new_key.id())


# Get an existing image
def get_image(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    request_image_id = int(request.META.get('HTTP_IMAGE_ID', -1))
    logger.info('gget_image got request for image id %s', repr(request_image_id))
    if request_image_id < 0:
        return response_bad_request()
    
    img = ndb.Key(Image, request_image_id).get()
    if not img:
        return response_not_found()

    return response_image(img.image_data)


# Delete an image
def delete_image(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    delete_image_id = int(json.loads(request.body)['image_id'])

    # Find image in datastore
    img = ndb.Key(Image, delete_image_id).get()
    if not img:
        return response_not_found()
    
    if not has_write_permission(visitor_id, img.owner_id):
        return response_forbidden()

    img.key.delete()
    return response_data('')


# Post a new comment
def post_comment(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST comment: '+request.body)
    
    # Create new Hike object
    comment = build_comment_from_json(request.body)
    if not comment:
        return response_bad_request()

    if not has_write_permission(visitor_id, comment.owner_id):
        return response_forbidden()

    hike = Hike.get_by_id(comment.hike_id)
    if not hike:
        return response_bad_request()

    if(comment.requested_id > 0):
        old_comment = ndb.Key(Comment, comment.comment_id).get()
        
        if not old_comment:
            return response_not_found()

        if not has_write_permission(visitor_id, old_comment.owner_id):
            return response_forbidden()
        
        comment.key = old_comment.key
    
    new_key = comment.put()
    logger.info('respond with ID '+repr(new_key.id()))
    return response_id('comment_id', new_key.id())


# Delete a comment
def delete_comment(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    delete_comment_id = int(json.loads(request.body)['comment_id'])

    # Find comment in datastore
    comment = ndb.Key(Comment, delete_comment_id).get()
    if not comment:
        return response_not_found()

    if not has_write_permission(visitor_id, comment.owner_id):
        return response_forbidden()
    
    comment.key.delete()
    return response_data('')



# Post a new image
def post_vote(request):
    
    visitor_id = authenticate(request)
    if not has_query_permission(visitor_id):
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST vote: '+request.body)

    # Create new Hike object
    rating = build_rating_from_json(request.body)
    if not rating:
        return response_bad_request()
    
    if not has_write_permission(visitor_id, rating.owner_id):
        return response_forbidden()
    
    hike = Hike.get_by_id(rating.hike_id)
    if not hike:
        return response_bad_request()

    # Remove previous votes of visitor for hike
    old_ratings = Rating.query(ndb.AND(Rating.owner_id == rating.owner_id,Rating.hike_id == rating.hike_id)).fetch()
    for old_rating in old_ratings:
        old_rating.key.delete()

    rating.put()
    return response_id('success', 1)



# Clean datastore: Remove all entities that obviously do not belong here.
def clean_datastore():
    hikes = Hike.query().fetch()
    for hike in hikes:
        # Remove malformed hikes
        if(hike.owner_id < 1):
            hike.key.delete()
            continue
        
        # Remove orphaned hikes
        logger.error("ID is "+repr(hike.owner_id))
        owner = User.get_by_id(hike.owner_id)
        if not owner:
            hike.key.delete()

    comments = Comment.query().fetch()
    for comment in comments:
        # Remove malformed comments
        if(comment.hike_id < 1):
            comment.key.delete()
            continue

        if(comment.owner_id < 1):
            comment.key.delete()
            continue
        
        # Remove orphaned comments
        hike = ndb.Key(Hike, comment.hike_id).get()
        if not hike:
            comment.key.delete()

        # Remove orphaned comments
        user = ndb.Key(User, comment.owner_id).get()
        if not user:
            comment.key.delete()

    images = Image.query().fetch()
    for image in images:
        # Remove malformed images
        if(image.owner_id < 1):
            image.key.delete()
            continue
        
        # Remove orphaned images
        owner = ndb.Key(User, image.owner_id).get()
        if not owner:
            image.key.delete()

    # Clean votes
    ratings = Rating.query().fetch()
    for rating in ratings:
        # Remove malformed ratings
        if(rating.owner_id < 1):
            rating.key.delete()
            continue
        
        # Remove orphaned ratings
        owner = ndb.Key(User, rating.owner_id).get()
        if not owner:
            rating.key.delete()

        # Remove malformed ratings
        if(rating.hike_id < 1):
            rating.key.delete()
            continue
        
        # Remove orphaned ratings
        hike = ndb.Key(Hike, rating.hike_id).get()
        if not hike:
            rating.key.delete()


    # Clean Database of test user
    test_users = User.query(User.mail_address == "bort@googlemail.com").fetch()
    for test_user in test_users:
        test_user.key.delete()

    return


# Delete datastore: Remove all entities except the long hikes
def delete_datastore(request):
    clean_datastore()
    
    hikes = Hike.query().fetch()
    for hike in hikes:
        if len(hike.hike_data) < 1000:
            hike.key.delete()

    users = User.query().fetch()
    for user in users:
        user.key.delete()

    images = Image.query().fetch()
    for img in images:
        img.key.delete()

    return response_data('')


def response_bad_request():
    return HttpResponse(status=400)
    
def response_forbidden():
    return HttpResponse(status=403)
    
def response_not_found():
    return HttpResponse(status=404)
    
def response_internal_error():
    return HttpResponse(status=500)

# Create response to POST, containing JSON for a named int/long quantity.
def response_id(id_name, id_value):
    if not isinstance(id_value, (int, long)):
        response_internal_error()
    id_string = str(id_value).strip('L')
    json_string = "{'" + id_name + "':" + id_string + "}"
    return HttpResponse(json_string, content_type='application/json', status=201)
        
# Create response containing the data string. data should be valid JSON string.
def response_data(data):
    logger.info('response_string: return string '+data)
    return HttpResponse(data, content_type='application/json')

# Create response containing the image. data should be valid JPEG image.
def response_image(data):
    return HttpResponse(data, content_type='image/jpeg')

def response_hike_locations(hikes):

    # assemble output string
    hike_locations = ','.join([hike.to_location() for hike in hikes])
    
    # return result
    hike_ids_string = "{\"hike_ids\":[" + hike_locations + "]}";
    return response_data(hike_ids_string)


