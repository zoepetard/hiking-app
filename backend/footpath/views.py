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
    track_id = request.GET.get('track_id', 0)
    logger.error('Request for track id '+repr(track_id))
    
    #random_track = Track.query().order(-Track.date)
    track = Track.query(Track.track_id == track_id).fetch()
    if track and track.size() > 0:
        track_string = track[0].to_json()
            
        logger.error('Return string '+repr(track_string))
        return HttpResponse(track_string, content_type='application/json')
    return HttpResponse(status=404)
    
# Get multiple tracks, as specified in a list inside the field
# track_ids of the GET request
def get_tracks(request):
    
    #random_track = Track.query().order(-Track.date)
    tracks = Track.query().fetch(100)
    
    #response_text = type(tracks)
    
    all_tracks = ""
    for track in tracks:#random_track = tracks[0]
        track_string = track.to_json() #track_to_json(track)
        all_tracks += track_string + '\n'
        
    return HttpResponse(all_tracks, content_type='application/json')
    #return HttpResponse(serializers.serialize("json", random_track), content_type='application/json')
    
def post_track(request):
    if request.method == 'POST':
        logger.error('POST request '+repr(request.body))
        #author = request.POST.get('author') some sort of idenfication needs to happen here
        track = build_track_from_json(request.body)
        if track:
            # Temporary: Remove old tracks with the same ID (to avoid ID collision)  
            old_tracks = Track.query(Track.track_id == track.track_id).fetch(100)
            for old_track in old_tracks:
                old_track.key.delete()
                
            track.put()
                
        response = HttpResponse("{'track_id':"+repr(track.track_id)+"}",\
                                content_type='application/json', status=201)
        return response
    return HttpResponse(status=404)
    