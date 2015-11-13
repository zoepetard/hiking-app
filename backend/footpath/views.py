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
    # Database testing function, TODO remove
    create_hike_one()
    
    logger.info('got request %s', repr(request))
    request_hike_id = int(request.META.get('HTTP_HIKE_ID', -1))
    logger.info('for hike id %s',repr(request_hike_id))
    
    #random_hike = Hike.query().order(-Hike.date)
    hikes = Hike.query(Hike.hike_id == request_hike_id).fetch(1)
    logger.error('found '+repr(len(hikes))+' entries for hike '+repr(request_hike_id))
    if hikes!=None and len(hikes) > 0:
        hike_string = hikes[0].to_json()
            
        logger.error('Return string '+repr(hike_string))
        return HttpResponse(hike_string, content_type='application/json')
    return HttpResponse(status=404)
    
# Get multiple hikes, as specified in a list inside the field
# hike_ids of the GET request
def get_hikes(request):
    # Database testing function, TODO remove
    create_hike_one()
    
    #random_hike = Hike.query().order(-Hike.date)
    hikes = Hike.query().fetch()
    
    #response_text = type(hikes)
    
    all_hikes = ""
    for hike in hikes:#random_hike = hikes[0]
        hike_string = hike.to_json() #hike_to_json(hike)
        all_hikes += hike_string + '\n'
        debug_new_hike = Hike()
        debug_new_hike.from_json(hike_string)
    
    return HttpResponse(all_hikes, content_type='application/javascript')
    #return HttpResponse(serializers.serialize("json", random_hike), content_type='application/json')
 
def get_hikes_in_window(request):
    
    # Get window from input
    request_bounding_box = request.META.get('HTTP_BOUNDING_BOX', -1)
    
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
        if (hike.bb_southwest < window_northeast):
            hike_ids.append(hike.hike_id)
     
    # return result       
    hike_ids_string = "{\"hike_ids\":" + repr(hike_ids) + "}";
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
    if request.method == 'POST':
        logger.error('POST request '+repr(request.body))
        #author = request.POST.get('author') some sort of idenfication needs to happen here
        hike = build_hike_from_json(request.body)
        if hike:
            # Temporary: Remove old hikes with the same ID (to avoid ID collision)  
            old_hikes = Hike.query(Hike.hike_id == hike.hike_id).fetch()
            for old_hike in old_hikes:
                old_hike.key.delete()
                
            hike.put()
                
        response = HttpResponse("{'hike_id':"+repr(hike.hike_id)+"}",\
                                content_type='application/json', status=201)
        return response
    return HttpResponse(status=404)
    