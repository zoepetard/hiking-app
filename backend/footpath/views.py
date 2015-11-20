from django.http import HttpResponseRedirect, HttpResponse
#from django.views.generic.simple import direct_to_template
from django.core import serializers

from google.appengine.api import users

from footpath.models import *

import urllib
import logging
import json

# Get an instance of a logger
logger = logging.getLogger(__name__)

def get_hike(request):
    
    #logger.info('got request %s', repr(request))
    request_hike_id = int(request.META.get('HTTP_HIKE_ID', -1))
    logger.info('got request for hike id %s', repr(request_hike_id))
    
    hike = ndb.Key(Hike, request_hike_id).get()
    if not hike:
        return response_not_found()
         
    # TODO: remove old query example code
    #random_hike = Hike.query().order(-Hike.date)
    #hikes = Hike.query(Hike.hike_id == request_hike_id).fetch(1)
    #logger.info('found '+repr(len(hikes))+' entries for hike '+repr(request_hike_id))
    #if hikes!=None and len(hikes) > 0:
    return response_hike(hike)
    
# Get multiple hikes, as specified in a list inside the field
# hike_ids of the GET request
def get_hikes(request):
    #for hike in Hike.query().fetch():
    #    hike.key.delete()
    
    # Database testing function, TODO remove
    #create_hike_one()
    
    hikes = Hike.query().fetch()
    
    all_hikes = ""
    #all_hikes += hikezero.to_json() + '!! key=' + repr(hikezero.key.id()) + '\n'
    for hike in hikes:#random_hike = hikes[0]
        hike_string = hike.to_json() #hike_to_json(hike)
        all_hikes += hike_string + ' with key=' + repr(hike.key.id()) + '\n'
    
    return HttpResponse(all_hikes, content_type='application/javascript')
    #return HttpResponse(serializers.serialize("json", random_hike), content_type='application/json')
 
def get_hikes_in_window(request):
    
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
    
    # query database and assemble output
    hikes = Hike.query(Hike.bb_northeast > window_southwest).fetch()
    
    hike_ids = [];
    for hike in hikes:
        if (hike.bb_southwest.lat < window_northeast.lat and hike.bb_southwest.lon < window_northeast.lon
            and hike.bb_northeast.lat > window_southwest.lat and hike.bb_northeast.lon > window_southwest.lon):
            hike_ids.append(hike.hike_id)
     
    # return result       
    hike_ids_string = "{\"hike_ids\":" + repr(hike_ids) + "}";
    logger.info("return string "+hike_ids_string)
    return HttpResponse(hike_ids_string, content_type='application/json')
    
    
    
# Create a hike for testing   
def create_hike_one():
    hike_one = Hike.query(Hike.hike_id == 1).fetch()
    if(len(hike_one) < 1):
        build_sample_hike(1, 1).put()
    elif(len(hike_one) > 1):
        for old_hike in hike_one[1:]:
            old_hike.key.delete()
    
def post_hike(request):
    if not request.method == 'POST':
        return response_bad_request()
    #author = request.POST.get('author') TODO some sort of authentification needs to happen here
    logger.info('POST request '+repr(request.body))
    
    # Create new Hike object
    hike = build_hike_from_json(request.body)
    if not hike:
        return response_bad_request()
        
    #TODO: set test flag on hikes that should be automatically removed
        
    # If update hike: Authenticate and check for existing hikes in database
    if(hike.hike_id >= 0):
        old_hike = ndb.Key(Hike, hike.hike_id).get()
        
        if not old_hike:
            return response_not_found()
            
        # TODO: Authenticate if new owner matches old owner
        
        hike.key = ndb.Key(Hike, hike.hike_id)
    
    new_key = hike.put()
    
    #hike = new_key.get()
    hike.hike_id = new_key.id()
    hike.put()
               
    return response_hike_id(new_key.id())
    
    
def response_bad_request():
    return HttpResponse(status=400)
    
def response_not_found():
    return HttpResponse(status=404)
    
def response_internal_error():
    return HttpResponse(status=500)

def response_hike_id(hike_id):
    if not isinstance(hike_id, ( int, long ) ):
        response_internal_error()
    hike_id_string = str(hike_id).strip('L')
    return HttpResponse("{'hike_id':"+hike_id_string+"}",\
                                content_type='application/json', status=201)
                                
def response_hike(hike):
    hike_string = hike.to_json()
            
    logger.info('return string '+repr(hike_string))
    return HttpResponse(hike_string, content_type='application/json')
    