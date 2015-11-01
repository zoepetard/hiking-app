from django.http import HttpResponseRedirect, HttpResponse
#from django.views.generic.simple import direct_to_template
from django.core import serializers

from google.appengine.api import users

from footpath.models import *

import urllib
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)


def get_track(request):
    logger.error(repr(request))
    request_track_id = int(request.META.get('HTTP_TRACK_ID', -1))
    logger.error('Request for track id '+repr(request_track_id))
    
    #random_track = Track.query().order(-Track.date)
    tracks = Track.query(Track.track_id == request_track_id).fetch(1)
    logger.error('found '+repr(len(tracks))+' entries for track '+repr(request_track_id))
    if tracks!=None and len(tracks) > 0:
        track_string = tracks[0].to_json()
            
        logger.error('Return string '+repr(track_string))
        return HttpResponse(track_string, content_type='application/json')
    return HttpResponse(status=404)
    
# Get multiple tracks, as specified in a list inside the field
# track_ids of the GET request
def get_tracks(request):
    
    #random_track = Track.query().order(-Track.date)
    tracks = Track.query().fetch()
    
    #response_text = type(tracks)
    
    all_tracks = ""
    for track in tracks:#random_track = tracks[0]
        track_string = track.to_json() #track_to_json(track)
        all_tracks += track_string + '\n'
            
    # TODO remove: testing functionality
    track_one = Track.query(Track.track_id == 1).fetch()
    if(len(track_one) < 1):
        build_sample_track(1, 1).put()
    elif(len(track_one) > 1):
        for old_track in track_one[1:]:
            old_track.key.delete()
    
    return HttpResponse(all_tracks, content_type='application/javascript')
    #return HttpResponse(serializers.serialize("json", random_track), content_type='application/json')
    
def post_track(request):
    if request.method == 'POST':
        logger.error('POST request '+repr(request.body))
        #author = request.POST.get('author') some sort of idenfication needs to happen here
        track = build_track_from_json(request.body)
        if track:
            # Temporary: Remove old tracks with the same ID (to avoid ID collision)  
            old_tracks = Track.query(Track.track_id == track.track_id).fetch()
            for old_track in old_tracks:
                old_track.key.delete()
                
            track.put()
                
        response = HttpResponse("{'track_id':"+repr(track.track_id)+"}",\
                                content_type='application/json', status=201)
        return response
    return HttpResponse(status=404)
    