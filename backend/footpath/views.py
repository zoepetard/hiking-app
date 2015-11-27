from django.http import HttpResponse
from django.core import serializers

from google.appengine.api import users

from footpath.hike import *
from footpath.user import *
from footpath.image import *
from footpath.auth import *
from footpath.comment import *

import logging
import json

# Get an instance of a logger
logger = logging.getLogger(__name__)

def get_hike(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    request_hike_id = int(request.META.get('HTTP_HIKE_ID', -1))
    logger.info('got request for hike id %s', repr(request_hike_id))
    
    hike = ndb.Key(Hike, request_hike_id).get()
    if not hike:
        return response_not_found()
    
    comments = get_comment_list(request_hike_id)
    logger.error("Comments are "+str(len(comments)))
    return response_data(hike.to_json(comments))
    
# Temporary: Function to quickly see the database in browser
# Get multiple hikes, as specified in a list inside the field
# hike_ids of the GET request
def get_hikes(request):
    
    hikes = Hike.query().fetch()
    
    all_hikes = ""
    for hike in hikes:
        hike_string = hike.to_json()
        key_string = str(hike.key.id()).strip('L')
        all_hikes += hike_string + ' with key=' + key_string + '\n'
    
    return HttpResponse(all_hikes, content_type='application/javascript')
 

# Gets all hikes in a bounding box specified in the request.
def get_hikes_in_window(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
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
    if visitor_id < 0:
        return response_forbidden()
    
    # Get window from input
    request_user_id = int(request.META.get('HTTP_USER_ID', -1))
    logger.info('got request for hikes of user %s', repr(request_user_id))
    
    # query database
    hikes = Hike.query(Hike.owner_id == request_user_id).fetch()

    return response_hike_locations(hikes)


# Format a brief summary of the hike, i.e. it's ID,
# and location information. Currently only formats the ID.
def hike_location(hike):
    return str(hike.key.id()).strip('L')
    
def post_hike(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    logger.info('POST request '+repr(request.body))
    
    # Create new Hike object
    hike = build_hike_from_json(request.body)
    if not hike:
        return response_bad_request()

    #if not visitor_id == hike.owner_id:    #TODO(simon) iss77
    #return response_forbidden()
    
    # Temporary: Clear database with specially prepared post request iss77
    if(hike.hike_id == 342):
        delete_datastore()
        return response_id('hike_id', 342)
    
    #TODO(simon): set test flag on hikes that should be automatically removed
        
    # If update hike: Authenticate and check for existing hikes in database iss77
    if(hike.hike_id >= 0):
        old_hike = ndb.Key(Hike, hike.hike_id).get()
        
        if not old_hike:
            return response_not_found()
            
        if old_hike.owner_id != hike.owner_id:
            return response_forbidden()
        
        hike.key = ndb.Key(Hike, hike.hike_id)
    
    new_key = hike.put()
    
    hike.hike_id = new_key.id()
    hike.put()

    #TODO(simon): remove test code
    #clean_datastore()
    comment_string = "{\"comment_id\":-1,\"hike_id\":"+repr(hike.hike_id).strip('L')+",\"user_id\":12345,\"comment_text\":\"blablabla\"}"
    comment = build_comment_from_json(comment_string)
    if comment:
        logger.error("Comment was created.")
        comment.put()
    else:
        logger.error("Comment was not created.")

    return response_id('hike_id', new_key.id())

# Delete a user. The hike can only be deleted by its author.
def delete_hike(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    delete_hike_id = int(json.loads(request.body)['hike_id'])
    
    #if not delete_user_id == visitor_id: TODO(simon) iss77
    #    return response_forbidden()
    
    hike = ndb.Key(Hike, delete_hike_id).get()
    visitor_id = hike.owner_id #TODO(simon) remove iss77
    if not hike:
        return response_not_found()
    if not hike.owner_id == visitor_id:
        return response_forbidden()

    hike.key.delete()
    return response_data('')


# Login a user. The email address is stored in the http request field "user_mail_address"
def login_user(request):
    
    request_user_email = request.META.get('HTTP_USER_MAIL_ADDRESS', '')
    if len(request_user_email) == 0:
        return response_bad_request()
    
    logger.info("Searching for user with email "+request_user_email)
    request_user_id = find_user_with_email(request_user_email)
    if request_user_id < 0:
        return response_not_found()
    
    user = ndb.Key(User, request_user_id).get()
    if not user:
        return response_not_found()
    
    return response_data(user.to_json())


# Get the user ID from an email address. Returns -1 on not found.
# Returns ID of some user if more than one user have the same address.
def find_user_with_email(mail_address):
    users = User.query(User.mail_address == mail_address).fetch(100)
    if users and len(users)>0:
        logger.info("Found "+repr(len(users))+" user(s) with email "+mail_address+"!")
        return users[0].key.id()
    return -1


# Create a new user in the database, or update a current one
def post_user(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST request '+repr(request.body))
    
    # Create new Hike object
    user = build_user_from_json(request.body)
    if not user:
        return response_bad_request()

    # If update hike: Authenticate and check for existing hikes in database
    if(user.request_user_id >= 0):
        old_user = ndb.Key(User, user.request_user_id).get()
        
        if not old_user:
            return response_not_found()
        
        # TODO(simon) authenticate iss77
        
        # Set the new user's database key to an existing one,
        # so that one will be overwritten
        user.key = ndb.Key(User, user.request_user_id)

    # Temporary: Clean Database of test users
    test_users = User.query(User.mail_address == "bort@googlemail.com").fetch()
    for test_user in test_users:
        test_user.key.delete()

    # Store new user in database and return the new id
    new_key = user.put()
    logger.info('respond with ID '+repr(new_key.id()))
    return response_id('user_id', new_key.id())


# Get a user. The numerical user ID is stored in the http request field "user_id"
def get_user(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    request_user_id = int(request.META.get('HTTP_USER_ID', -1))
    logger.info('get_user got request for user id %s', repr(request_user_id))

    # Find user id via email lookup
    # this feature will be removed soon
    if request_user_id < 0:
        request_user_email = request.META.get('HTTP_USER_MAIL_ADDRESS', '')
        if len(request_user_email) > 0:
            request_user_id = find_user_with_email(request_user_email)


    if request_user_id < 0:
        return response_not_found()
    
    user = ndb.Key(User, request_user_id).get()
    if not user:
        return response_not_found()
        
    return response_data(user.to_json())


# Delete a user. The author of this request can only delete himself.
def delete_user(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()

    delete_user_id = int(json.loads(request.body)['user_id'])

    #if not delete_user_id == visitor_id:
    #    return response_forbidden()

    user_obj = ndb.Key(User, delete_user_id).get()
    if not user_obj:
        return response_not_found()
                         
    user_obj.key.delete()
    return response_data('')


# Post a new image
def post_image(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    logger.info('POST image from '+repr(visitor_id))
    request_image_id = int(request.META.get('HTTP_IMAGE_ID', '-1'))
    
    # Create new Hike object
    img = build_image(visitor_id, request.body)
    if not img:
        return response_bad_request()

    # If update hike: Authenticate and check for existing hikes in database iss77
    logger.info('request post to ID '+repr(request_image_id))
    if(request_image_id >= 0):
        old_image = ndb.Key(Image, request_image_id).get()
        
        if not old_image:
            return response_not_found()
        
        if not old_image.owner_id == img.owner_id:
            return response_forbidden()
        
        img.key = old_image.key

    new_key = img.put()
    logger.info('respond with ID '+repr(new_key.id()))
    return response_id('image_id', new_key.id())


# Get an existing image
def get_image(request):
    
    visitor_id = authenticate(request)
    if visitor_id < 0:
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
    if visitor_id < 0:
        return response_forbidden()
    
    if not request.method == 'POST':
        return response_bad_request()
    
    delete_image_id = int(json.loads(request.body)['image_id'])

    # Find image in datastore
    img = ndb.Key(Image, delete_image_id).get()
    if not img:
        return response_not_found()

    if not img.owner_id == visitor_id:
        return response_forbidden()

    img.key.delete()
    return response_data('')


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

        if(comment.user_id < 1):
            comment.key.delete()
            continue
        
        # Remove orphaned comments
        hike = ndb.Key(Hike, comment.hike_id).get()
        if not hike:
            comment.key.delete()

        # Remove orphaned comments
        user = ndb.Key(User, comment.user_id).get()
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
            hike.key.delete()


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


def get_comment_list(hike_id):
    comment_list = []
    comments = Comment.query(Comment.hike_id == hike_id).fetch()
    for comment in comments:
        comment_list.append(json.loads(comment.to_json()))

    return comment_list


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


