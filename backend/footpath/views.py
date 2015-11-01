from django.http import HttpResponseRedirect, HttpResponse
#from django.views.generic.simple import direct_to_template
from django.core import serializers

from google.appengine.api import users

from footpath.models import Track

import json

import urllib

def get_track(request):
    track_id = request.GET.get('track_id', 0)
    
    # Ancestor Queries, as shown here, are strongly consistent with the High
    # Replication Datastore. Queries that span entity groups are eventually
    # consistent. If we omitted the ancestor from this query there would be
    # a slight chance that Greeting that had just been written would not
    # show up in a query.
    random_track = Track.query().order(-Track.date)
    tracks = Track.query().fetch(100)
    
    response_text = type(tracks)
    
    all_tracks = ""
    for track in tracks:#random_track = tracks[0]
        track_string = track_to_json(track)
        all_tracks += track_string + '\n'
        
    return HttpResponse(all_tracks, content_type='application/json')
    #return HttpResponse(serializers.serialize("json", random_track), content_type='application/json')
    
def post_track(request):
    if request.method == 'POST':#True:#
        #author = request.POST.get('author') some sort of idenfication happens here
        track = json_to_track(request.body)
        if track:
            track.put()
        
        response = HttpResponse("{'id':42}" + track_to_json(track), content_type='application/json', status=201)
        return response
    return HttpResponse(status=404)
    
def track_to_json(track):
    track_data = {
        'author': track.author,
        'date': track.date.strftime("%Y-%m-%d %H:%M:%S"),
        'some_string': track.some_string,
    }
    return json.dumps(track_data)
    
def json_to_track(json_string):
    json_object = json.loads(json_string)
    track = Track()
    track.some_string = json_object['some_string']
    #track.some_string = 'jtt_set_author'
    return track